package dk.osaa.psaw.core;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import dk.osaa.psaw.job.Job;
import dk.osaa.psaw.job.JobManager;
import dk.osaa.psaw.machine.CommandReply;
import dk.osaa.psaw.machine.Commander;
import dk.osaa.psaw.machine.CommanderInterface;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.MoveVector;
import dk.osaa.psaw.machine.Q30;
import dk.osaa.psaw.machine.SimulatedCommander;
import dk.osaa.psaw.config.LegacyConfiguration;
import dk.osaa.psaw.machine.PhotonSawCommandFailed;
import dk.osaa.psaw.machine.ReplyTimeout;

import lombok.Getter;
import lombok.val;
import lombok.extern.java.Log;

/**
 * The main machine interface class that all the other bits hang off of. 
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Log
public class PhotonSaw extends Thread {
	LegacyConfiguration cfg;
	CommanderInterface commander;
	JobManager jobManager;
	@Getter
	Planner planner;

	@Getter
	boolean currentAssistAir = false;
	
	public static final int MOVE_QUEUE_SIZE = 100;
	
	@Getter
	ArrayBlockingQueue<Move> moveQueue = new ArrayBlockingQueue<Move>(MOVE_QUEUE_SIZE);
	
	public PhotonSaw(LegacyConfiguration cfg) throws IOException, ReplyTimeout, NoSuchPortException, PortInUseException, UnsupportedCommOperationException, PhotonSawCommandFailed  {
		this.cfg = cfg;
		planner = new Planner(this);
		
		if (cfg.hostConfig.isSimulating()) {
			commander = new SimulatedCommander();			
		} else {
			commander = new Commander();
		}		
		commander.connect(cfg.hostConfig.getSerialPort());
		
		jobManager = new JobManager(cfg);
		
		setDaemon(true);
		setName("PhotonSaw thread, keeps the hardware fed");				
		
		try {
			run("");
			run("br");
			run("mr");
			run("ac 0");
		} catch (Exception e) {			
		}

		try {
			//run("ai 10100");
		} catch (Exception e) {			
		}


		configureMotors();

		planner.start();
		this.start();	
	}

	boolean idle;
	
	MoveVector lastSpeed;
	//static final double QF = 0.0375*50000/(1<<30);
	private void checkJerk(Move move) {
		MoveVector startSpeed = new MoveVector();
		MoveVector endSpeed = new MoveVector();
		for (int i=0;i<Move.AXES;i++) {
			double qf = cfg.movementConstraints.getTickHZ()*cfg.movementConstraints.getAxes()[i].mmPerStep/(1<<30);
			startSpeed.setAxis(i,  move.getAxisSpeed(i)                                          * qf);
			endSpeed  .setAxis(i, (move.getAxisSpeed(i)+move.getAxisAccel(i)*move.getDuration()) * qf);
		}

		if (lastSpeed != null) {
			for (int i=0;i<Move.AXES;i++) {
				double jerk = lastSpeed.getAxis(i)-startSpeed.getAxis(i);
				if (Math.abs(jerk) > cfg.movementConstraints.getAxes()[i].maxJerk*1.25) {
					log.info("Jerk too large: jerk:"+jerk+" from:"+lastSpeed.getAxis(i)+" to:"+startSpeed.getAxis(i)+" id:"+move.getId()+" axis: "+i+" "+lastSpeed+" "+startSpeed);
				// 	throw new RuntimeException("Jerk too large: jerk:"+jerk+" from:"+lastSpeed.getAxis(i)+" to:"+startSpeed.getAxis(i)+" id:"+move.getId()+" axis: "+i+" "+lastSpeed+" "+startSpeed);
				}
			}
		}
		
		lastSpeed = endSpeed;
	}
	
	public void putMove(Move move) throws InterruptedException {
		
		checkJerk(move);
				
		logMove(move);
		
		moveQueue.put(move);
		
		if (idle) {
			this.interrupt(); // Wake up from sleep.
		}
	}	

	public void putAssistAir(boolean assistAir) throws InterruptedException {
		if (currentAssistAir == assistAir) {
			return;
		}
		currentAssistAir = assistAir;
		
		Move m = new Move(Line.getMoveId(), cfg.machineConfig.getAssistAirDelay());
		m.setAssistSwitch(assistAir);
	}
	
	static FileWriter logWriter;
	
	private void logMove(Move move) {
		try {

			if (logWriter == null) {
				logWriter = new FileWriter(new File("move.log"));			
			}
			
			logWriter.write(move.toString());			
			logWriter.write("\n");
			logWriter.flush();
		
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to log move", e);
		}		
	}
	
	public void run() {
		while (true) {
			try {
				commander.bufferMoves(moveQueue);
				if (moveQueue.isEmpty()) {
					idle = true;
					try {
						Thread.sleep(250); // Wait and see if another move becomes available					
					} catch (InterruptedException e) {
						// Never mind.
					}			
					idle = false;
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Caught exception while buffering moves", e);
			}
		}
	}
		
	public CommandReply run(String cmd) throws IOException, ReplyTimeout, PhotonSawCommandFailed {		
		log.fine("Running: "+cmd);
		val r = commander.run(cmd);
		if (r.get("result").isOk()) {
			log.fine("command '"+cmd+"' worked: "+r);
		} else {
			log.severe("command '"+cmd+"' gave error: "+r);
			throw new PhotonSawCommandFailed("command '"+cmd+"' gave error: "+r);
		}
		return r;
	}
		
	private void configureMotors() throws IOException, ReplyTimeout, PhotonSawCommandFailed {
		for (int i=0;i<Move.AXES;i++) {
			run("me "+i+" "+
					cfg.movementConstraints.getAxes()[i].coilCurrent+" "+
					cfg.movementConstraints.getAxes()[i].microSteppingMode);
		}
	}

	public CommanderInterface getCommander() {
		return commander;
	}

	public boolean active() throws IOException, ReplyTimeout, PhotonSawCommandFailed {		
		CommandReply r = run("st");
		return r.get("motion.active").getBoolean();
	}
	
	static XStream xstreamInstance = null;
	public static XStream getStatusXStream() {
		if (xstreamInstance == null) {
			xstreamInstance = new XStream(new StaxDriver());
			xstreamInstance.setMode(XStream.NO_REFERENCES);
			
			xstreamInstance.alias("status", PhotonSawStatus.class);
		}
		return xstreamInstance;
	}

	public PhotonSawStatus getStatus() {
		PhotonSawStatus r = new PhotonSawStatus();
		
		r.setTimestamp(System.currentTimeMillis());
		r.setHardwareStatus(commander.getLastReplyValues());
		r.setLineBufferLength(planner.getLineBufferLength());
		r.setLineBufferLengthTarget(LineBuffer.BUFFER_LENGTH);
		r.setLineBufferSize(planner.getLineBufferCount());
		r.setLineBufferSizeTarget(LineBuffer.BUFFER_SIZE);
		r.setMoveBufferInUse(moveQueue.size());
		r.setMoveBufferSize(MOVE_QUEUE_SIZE);
		r.setJobLength(planner.getCurrentJobLength());
		r.setJobSize(planner.getCurrentJobSize());
		r.setJobRenderingProgressLineCount(planner.getRenderedLines());
		
		File rd = cfg.hostConfig.getRecordDir();
		if (cfg.hostConfig.isRecording() && rd != null) {
			File f = new File(rd, r.getTimestamp()+".psawstate");

			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
				getStatusXStream().toXML(r, bos);
				bos.close();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Failed to store recorded status", e);
			}
		}		
		
		return r;
	}

	public JobManager getJobManager() {
		return jobManager;
	}

	public void setJogSpeed(MoveVector direction) {
		String cmd = "jv";
		// full step: 0.00625
		log.info("jog: "+direction);
		
		for (int ax=0;ax<Move.AXES;ax++) {
			double speed = cfg.movementConstraints.getAxes()[ax].maxSpeed;
			if (speed > 100) {
				speed = 100;
			}
			
			double stepsPerTick = speed * direction.getAxis(ax) / cfg.movementConstraints.getAxes()[ax].mmPerStep / cfg.movementConstraints.getTickHZ();
				
			// We run for 5000 ticks per interval, so don't allow any partial steps to be taken or the motor will not run smoothly.
			stepsPerTick = Math.round(stepsPerTick*5000)/5000.0;
			Q30 s = new Q30(stepsPerTick);
			
			cmd += " "+s.getLong();
		}
		
		log.info(cmd);
		
		try {
			commander.run(cmd);
		} catch (Exception e) { // TODO: re-throw to tell the client about the problem.
			log.log(Level.SEVERE, "Failed to run jog command '"+cmd+"': ", e);
		}		
	}

	public boolean startJob(String id) {
		try {
			Job job = getJobManager().getJobById(id);
			getPlanner().startJob(job);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to start job: "+id, e);
			return false;
		}
	}

	public String getCurrentJob() {
		// TODO: Fail.
		return null;
	}

}
