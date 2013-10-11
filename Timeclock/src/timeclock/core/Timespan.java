package timeclock.core;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Tracks a span of time in the day using a 24-hour clock.
 *
 * @author Justin Toniazzo
 *
 */
public class Timespan {
	
	public static final double DEFAULT_START = 5.0;
	public static final double DEFAULT_END = 11.0;
	
	private double start;
	private double end;
	
	public Timespan(double start, double end) {
		this.start = start;
		this.end = end;
	}
	
	public Timespan() {
		this(DEFAULT_START, DEFAULT_END);
	}
	
	public double getLength() {
		return end - start;
	}

	public double getStart() {
		return start;
	}
	
	
	public String getStartFormatted() {
		double trimmed = start - (int) start;
		String s = String.format("%.2f", trimmed);
		if (s.endsWith(".00"))
			return (int) start + ":00";
		else if (s.endsWith(".50"))
			return (int) start + ":30";
		else if (s.endsWith(".75"))
			return (int) start + ":45";
		return "FAIL";
	}

	public void setStart(double start) {
		this.start = start;
	}

	public double getEnd() {
		return end;
	}

	
	public String getEndFormatted() {
		double trimmed = end - (int) end;
		String s = String.format("%.2f", trimmed);
		if (s.endsWith(".00"))
			return (int) end + ":00";
		else if (s.endsWith(".50"))
			return (int) end + ":30";
		else if (s.endsWith(".75"))
			return (int) end + ":45";
		return "FAIL";
	}

	public void setEnd(double end) {
		this.end = end;
	}
	
	public static boolean isValidTime(String s) {
		Scanner scan = new Scanner(s);
		scan.useDelimiter("[.]");
		String prefix, suffix;
		try {
			prefix = scan.next();
			suffix = scan.next();
		} catch (NoSuchElementException e) {
			scan.close();
			return false;
		}
		scan.close();
		
		int hours;
		int minutes;
		try {
			
			System.out.println("Input: " + s);
			System.out.println("Prefix: " + prefix);
			System.out.println("Suffix: " + suffix);
			hours = Integer.parseInt(prefix);
			minutes = Integer.parseInt(suffix);
			if (hours < 0 || hours > 24)
				return false;
			switch (minutes) {
			case 0:
				break;
			case 25:
				break;
			case 5: 
				break;
			case 75:
				break;
			default:
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String ret = "";
		ret += getStartFormatted() + " - ";
		ret += getEndFormatted();
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(end);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(start);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timespan other = (Timespan) obj;
		if (Double.doubleToLongBits(end) != Double.doubleToLongBits(other.end))
			return false;
		if (Double.doubleToLongBits(start) != Double
				.doubleToLongBits(other.start))
			return false;
		return true;
	}
}
