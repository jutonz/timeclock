package timeclock.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WorkdayTest {
	
	public Workday wd;
	private SimpleDate today;
	
	@Before
	public void setUp() {
		today = new SimpleDate(7, 26, 13);
		wd = new Workday(today);
	}

	@Test
	public void testAddHours() {
		wd.addHours(new Timespan(6, 12));
		assertTrue(6 == wd.getHours());
		wd.addHours(new Timespan(3.5, 9.5));
		assertTrue(12 == wd.getHours());		
	}

	@Test
	public void testGetDate() {
		assertTrue(wd.getDate().equals(today));
	}
	
	@Test
	public void testToXML() {
		wd.addHours(new Timespan(6, 12));
		wd.addHours(new Timespan(3.5, 9.5));
		wd.toXML();
	}

}
