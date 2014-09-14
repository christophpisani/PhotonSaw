package dk.osaa.psaw.job;

import java.awt.geom.Point2D;

import lombok.Data;
import dk.osaa.psaw.machine.Point;

/**
 * Takes care of mapping the 2D points from a job into 4D space as understood by the machine.
 * 
 * @author Flemming Frandsen <dren.dk@gmail.com> <http://dren.dk>
 */
@Data
public class PointTransformation {
	public enum Rotation {
		NORMAL, 
		LEFT,  
		DOWN,  
		RIGHT  
	};

	static int rotationToInt(Rotation r) {
		if (r == Rotation.NORMAL) {
			return 0;
		} else if (r == Rotation.LEFT) {
			return 1;
		} else if (r == Rotation.DOWN) {
			return 2;
		} else {
			return 3;
		}
	}
	
	static Rotation intToRotation(int r) {
		if ((r & 3) == 0) {
			return Rotation.NORMAL;

		} else if ((r & 3) == 1) {
			return Rotation.LEFT;

		} else if ((r & 3) == 2) {
			return Rotation.DOWN;
		} else {
			return Rotation.RIGHT;
		}
	}
	
	public enum AxisMapping {
		XY,
		XA
	};
	
	AxisMapping axisMapping = AxisMapping.XY;
	Rotation rotation = Rotation.NORMAL;
	Point2D.Double offset = new Point2D.Double(0,0);
			
	public Point transform(Point2D p2d) {
		Point res = new Point();
		
		double x;
		double y;
		
		// First handle rotation
		if (rotation == Rotation.NORMAL) {
			x = p2d.getX();
			y = p2d.getY();

		} else if (rotation == Rotation.LEFT) {
			x = -p2d.getY();
			y = p2d.getX();

		} else if (rotation == Rotation.DOWN) {
			x = -p2d.getX();
			y = -p2d.getY();

		} else if (rotation == Rotation.RIGHT) {
			x = p2d.getY();
			y = -p2d.getX();

		} else {
			throw new RuntimeException("Rotation not implemented");
		}
		
		// Then add the offset
		x += offset.x;
		y += offset.y;
			
		// Then map the 2d point into machine space 
			
		if (axisMapping == AxisMapping.XY) {
			res.axes[0] = x;
			res.axes[1] = y;

		} else if (axisMapping == AxisMapping.XA) {
			res.axes[0] = x;
			res.axes[3] = y;

		} else {
			throw new RuntimeException("AxisMapping not implemented");
		}
		
		return res;
	}
	
	public PointTransformation add(PointTransformation a) {
		if (a == null) {
			return this;
		}
		
		PointTransformation res = new PointTransformation();
		res.axisMapping = this.axisMapping; // Can't remap the axis mapping.
		res.rotation = intToRotation(rotationToInt(this.rotation) + rotationToInt(a.rotation));
		res.offset.x = this.offset.x + a.offset.x; 
		res.offset.y = this.offset.y + a.offset.y; 
				
		return a;
	}
}
