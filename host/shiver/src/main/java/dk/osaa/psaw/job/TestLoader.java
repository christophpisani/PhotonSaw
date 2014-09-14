package dk.osaa.psaw.job;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Loads a test path into the Job
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
public class TestLoader {

	ArrayList<Point2D.Double> path = new ArrayList<Point2D.Double>();
	void addPoint(double x, double y) {
		path.add(new Point2D.Double(x,y));
	}

	public static JobNode load(Job job) {
				
		TestLoader tl = new TestLoader();

		tl.addPoint(0,0);
		tl.addPoint(30, 60);
		
		for (int i=1;i<60+1;i++) {
			tl.addPoint(0.2*i, 1.4*i);
		}
				
		final int N = 50;
		for (int i=0;i<N*10;i++) {
			tl.addPoint(((i)/N)*30*Math.sin((i*Math.PI*2)/N),
					((i)/N)*60*Math.cos((i*Math.PI*2)/N));			
		}
		
		tl.addPoint(0,0);
		
		JobNodeGroup test = new JobNodeGroup(job.getNodeId("test"), null);
		//test.addChild(new CutPath(job.getNodeId("testcut"), 0.5, 1000, 1, true, tl.path));
		return test;
	}

}
