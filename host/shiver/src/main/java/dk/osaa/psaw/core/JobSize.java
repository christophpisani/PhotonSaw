package dk.osaa.psaw.core;

import dk.osaa.psaw.job.JobRenderTarget;
import dk.osaa.psaw.machine.Move;
import dk.osaa.psaw.machine.Point;

public class JobSize implements JobRenderTarget {

	int lineCount = 0;
	double lineLength = 0;
	Point pos;

	Planner p;
	public JobSize(Planner p) {
		this.p = p;
		pos = p.lastBufferedLocation;
	}
	
	@Override
	public void moveTo(Point p) {
		double s = 0;
		for (int i=0;i<Move.AXES;i++) {
			s += Math.pow(p.axes[i]-pos.axes[i], 2);
		}
		lineLength += Math.sqrt(s);
		lineCount++;
	}

	@Override
	public void cutTo(Point p, double intensity, double maxSpeed) {
		moveTo(p);
	}

	@Override
	public void engraveTo(Point p, double intensity, double maxSpeed,
			boolean[] pixels) {
		moveTo(p);
	}

	@Override
	public double getEngravingXAccelerationDistance(double speed) {
		return p.getEngravingXAccelerationDistance(speed);
	}

	@Override
	public double getEngravingYStepSize() {
		return p.getEngravingYStepSize();
	}

	@Override
	public void setAssistAir(boolean on) {
		// Ignore
	}

	@Override
	public void moveToAtSpeed(Point p, double maxSpeed) {
		moveTo(p);		
	}

	@Override
	public void startShape(String id) {
		// TODO Auto-generated method stub
		
	}
}
