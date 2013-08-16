package timeclock.core;

import java.util.Calendar;
import java.util.Scanner;

public class SimpleDate {

	private int month;
	private int date;
	private int year;
	
	public SimpleDate(int month, int date, int year) {
		this.month = month;
		this.date = date;
		this.year = year;
	}
	
	public SimpleDate(String fromString) {
		Scanner scan = new Scanner(fromString);
		scan.useDelimiter("-");
		month = scan.nextInt();
		date = scan.nextInt();
		year = scan.nextInt();
		scan.close();
	}
	
	public SimpleDate(Calendar c) {
		this.month = c.get(Calendar.MONTH) + 1;
		this.date  = c.get(Calendar.DATE);
		this.year  = c.get(Calendar.YEAR);
	}
	
	public String getDayOfWeek() {
		int w = year - (14 - month) / 12;
		int x = w + w / 4 - w / 100 + w / 400;
		int z = month +  12 * ((14 - month) / 12) - 2;
		int weekday = (date + x + (31 * z) / 12) % 7;
		switch (weekday) {
		case 1:
			return "Monday";
		case 2:
			return "Tuesday";
		case 3:
			return "Wednesday";
		case 4:
			return "Thursday";
		case 5:
			return "Friday";
		case 6:
			return "Saturday";
		default:
			return "Sunday";
		}
	}
	
	public SimpleDate nextCalendarDay() {
		Calendar c = this.toCalendar();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
		SimpleDate ret = new SimpleDate(c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE), c.get(Calendar.YEAR));
		return ret;
	}
	
	public SimpleDate previousCalendarDay() {
		Calendar c = this.toCalendar();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 1);
		return new SimpleDate(c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE), c.get(Calendar.YEAR));
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public Calendar toCalendar() {
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date);
		return c;
	}
	
	@Override
	public String toString() {
		String ret = "";
		ret += month + "-";
		ret += date + "-";
		ret += year;
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + date;
		result = prime * result + month;
		result = prime * result + year;
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
		SimpleDate other = (SimpleDate) obj;
		if (date != other.date)
			return false;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	
}
