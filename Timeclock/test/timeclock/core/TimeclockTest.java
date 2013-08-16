package timeclock.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimeclockTest {
	
	public Timeclock tc;

	@Test
	public void testCalculateTaxesInt() {
	}
	
	@Test
	public void testLoadTimeclock() {
		tc = new Timeclock();
		System.out.println(tc);
		//tc.saveToFile();
	}

}
