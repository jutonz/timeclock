package timeclock.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleDateTest {

	@Test
	public void test() {
		SimpleDate date = new SimpleDate("6-20-12");
		assertTrue(date.toString().equals("6-20-12"));
	}

}
