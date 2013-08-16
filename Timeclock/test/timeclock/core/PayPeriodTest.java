package timeclock.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

public class PayPeriodTest {
	
	private static final double DELTA = 1e-15;
	
	private PayPeriod p;
	
	//@Before
	public void setUp() {
		SimpleDate start = new SimpleDate(6, 10, 13);
		SimpleDate end = new SimpleDate(6, 23, 13);
		p = new PayPeriod(start, end);
		
		SimpleDate today = new SimpleDate(7, 26, 13);
		Workday wd = new Workday(today);
		wd.addHours(new Timespan(6, 12));
		wd.addHours(new Timespan(3.5, 9.5));
		p.addWorkday(wd);
		
		today = new SimpleDate(7, 27, 13);
		wd = new Workday(today);
		wd.addHours(new Timespan(5, 11));
		wd.addHours(new Timespan(1.5, 8.5));
		p.addWorkday(wd);
	}
	
	@Test
	public void testGetStartEndOfNext() {
		SimpleDate start = new SimpleDate(6, 10, 13);
		SimpleDate end = new SimpleDate(6, 23, 13);
		p = new PayPeriod(start, end);
		
		SimpleDate actual   = p.getStartOfNext();
		SimpleDate expected = new SimpleDate(6, 24, 13);
		assertEquals(expected, actual);
		
		actual   = p.getEndOfNext();
		expected = new SimpleDate(7, 7, 13);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetStartEndOfPrevious() {
		SimpleDate start = new SimpleDate(6, 24, 13);
		SimpleDate end = new SimpleDate(7, 7, 13);
		p = new PayPeriod(start, end);
		
		SimpleDate actual   = p.getStartOfPrevious();
		SimpleDate expected = new SimpleDate(6, 10, 13);
		assertEquals(expected, actual);
			
		actual   = p.getEndOfPrevious(); 
		expected = new SimpleDate(6, 23, 13);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPayday() {
		SimpleDate start = new SimpleDate(6, 24, 13);
		SimpleDate end = new SimpleDate(7, 7, 13);
		p = new PayPeriod(start, end);
		
		SimpleDate expected = new SimpleDate(7, 12, 13);
		SimpleDate actual   = p.getPayday();
		assertEquals(expected, actual);
		//
		//
		start = new SimpleDate(6, 10, 13);
		end = new SimpleDate(6, 23, 13);
		p = new PayPeriod(start, end);
		
		expected = new SimpleDate(6, 28, 13);
		actual   = p.getPayday();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetWeekOneTwoMarkoutTips() {
		SimpleDate start = new SimpleDate(6, 24, 13);
		SimpleDate end = new SimpleDate(7, 7, 13);
		p = new PayPeriod(start, end);
		p.setWeekOneMarkout("French");
		p.setWeekTwoMarkout("Italian");
		p.setWeekOneTips(40);
		p.setWeekTwoTips(32.5);
		
		Element e = p.toXML();
		p = new PayPeriod(e);
		assertEquals("French", p.getWeekOneMarkout());
		assertEquals("Italian", p.getWeekTwoMarkout());
		assertEquals(40, p.getWeekOneTips(), DELTA);
		assertEquals(32.5, p.getWeekTwoTips(), DELTA);
		//
		//
		p = new PayPeriod(start, end);
		e = p.toXML();
		p = new PayPeriod(e);
		assertNull(p.getWeekOneMarkout());
		assertNull(p.getWeekTwoMarkout());
		assertEquals(0.0, p.getWeekOneTips(), DELTA);
		assertEquals(0.0, p.getWeekTwoTips(), DELTA);
	}

//	//@Test
//	public void testSavePayPeriod() {
//		p.savePayPeriod();
//	} 
//	
//	@Test
//	public void testLoadPayPeriod() {
//		p.clearSchedule();
//		p.loadPayPeriod("Pay Period from 6-10-13 to 6-23-13.xml");
//		assertTrue(p.getStart().equals(new SimpleDate(6, 10, 13)));
//		assertTrue(p.getEnd().equals(new SimpleDate("6-23-13")));
//		assertEquals(25, p.getHoursWorked(), DELTA);
//		p.savePayPeriod("Pay Period from 6-10-13 to 6-23-13 OUT.xml");
//		System.out.println(p);
//		
//		p.clearSchedule();
//		p = new PayPeriod("C:\\\\Users\\Justin\\AppData\\Roaming\\Timeclock\\blah.xml");
//		p.savePayPeriod("C:\\\\Users\\Justin\\AppData\\Roaming\\Timeclock\\");
//
//	}

}
