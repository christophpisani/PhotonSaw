package dk.osaa.psaw.job;

import dk.osaa.psaw.machine.Point;

/**
 * This is the interface that a job renders its output to, this is implemented by the Planner
 * which turns it into Moves to be sent to the hardware and by the preview renderer for the GUI.
 * 
 * @author ff
 */
public interface JobRenderTarget {
	
	/**
	 * Called by the job to let the target know what the shape that's going to be rendered now is called,
	 * this is useful for progress indicators as well as debugability.
	 * @param id the id of the shape that's about to start
	 */
	void startShape(String id);
	
	/**
	 * Move as quickly as possible to this point
	 * @param p The point to move to
	 */
	void moveTo(Point p);

	/**
	 * Like moveTo, but asserts that the speed maxSpeed must be the exact speed at the end of the line
	 *  
	 * @param p the point to move to
	 * @param maxSpeed the target speed to hit at the end point
	 */
	void moveToAtSpeed(Point p, double maxSpeed);
	
	/**
	 * Turn on the laser and move to this point at the desired speed
	 * @param p the point to move to
	 * @param intensity The intensity (0..1) of the LASER during the move
	 * @param maxSpeed The desired speed
	 */
	void cutTo(Point p, double intensity, double maxSpeed); //TODO: Take LaserNodeSettings in stead
	
	/**
	 * Turn on the laser and move to this point at the desired speed while engraving a scanline of pixels.
	 * @param p the point to move to
	 * @param intensity The intensity (0..1) of the LASER during the move
	 * @param maxSpeed The desired speed
	 * @param pixels The pixels to engrave over this line.
	 */
	void engraveTo(Point p, double intensity, double maxSpeed, boolean[] pixels);//TODO: Take LaserNodeSettings in stead
	
	/**
	 * Calculates the distance in mm needed to accelerate the X axis from 0 to the desired speed.
	 * 
	 * This determines the lead-in and lead-out of the engraving moves.
	 * 
	 * @param speed in mm/s
	 * @return distance in mm
	 */
	double getEngravingXAccelerationDistance(double speed);
	
	/**
	 * @return The size, in mm of one step in the Y axis as used when engraving;
	 */
	double getEngravingYStepSize(); // TODO: line height should come from the user in stead of the machine limit!
	
	/**
	 * Turn assist air on or off
	 * @param assistAirOn status of assist air
	 */
	void setAssistAir(boolean assistAirOn);

	
}
