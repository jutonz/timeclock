package timeclock.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimespanTest {

	@Test
	public void testTimespan() {
		
	}

	@Test
	public void testLength() {
		Timespan t = new Timespan(6, 12);
		assertTrue(t.getLength() == 6);		
	}

	@Test
	public void testGetStart() {
	}

	@Test
	public void testSetStart() {
	}

	@Test
	public void testGetEnd() {
	}

	@Test
	public void testSetEnd() {
	}

}
