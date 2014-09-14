package dk.osaa.psaw.machine;

import lombok.Data;

/**
 * A Q2.30 fixed point number which fits in a 32 bit word
 * 
 * @author ff
 */
@Data
public class Q30 {
	static final long ONE = 1<<30;
	int intValue;
	long value;

	public Q30(double floating) {
		setDouble(floating);
	}
	
	public Q30(long fixed) {
		setLong(fixed);
	}
	
	public void setDouble(double floating) {
		double f = Math.round(floating * ONE);
		if (Math.abs(f) > ONE) {
			throw new RuntimeException("Overflow, Q30 cannot contain values larger than 1: "+floating);
		}
		intValue = (int)f;
		value = 0xffffffffL & (long)f;		
	}
	
	public double toDouble() {
		return ((double)intValue)/ONE;
	}
	
	public String toString() {
		return Long.toHexString(value).toLowerCase();
	}

	public void addDouble(double d) {
		setDouble(toDouble()+d);
	}

	public long getLong() {
		return intValue;
	}	

	public void setLong(long l) {
		intValue = (int)l;
		value = 0xffffffffL & l;		
	}	
}
