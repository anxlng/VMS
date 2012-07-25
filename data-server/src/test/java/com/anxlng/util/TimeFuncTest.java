/**
 * data-server. 2012-6-27
 */
package com.anxlng.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static com.anxlng.util.TimeFunc.*;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class TimeFuncTest {

	private final String current = "2012-10-10 08:00:00";
	private final long currentMillis = 1349827200000L;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * Test method for {@link com.anxlng.util.TimeFunc#getDateTime(java.util.Date, java.lang.String)}.
	 */
	@Test
	public void testGetDateTimeDateString() {
		
		assertEquals(getDateTime(new Date(currentMillis), TimeFunc.DATE_TIME), current);
	}

	/**
	 * Test method for {@link com.anxlng.util.TimeFunc#utcToDefault(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUtcToDefault() {
		
		String defaultDate = utcToDefault(current, TimeFunc.DATE_TIME);
		String utcDate = defaultToUtc(defaultDate, TimeFunc.DATE_TIME);
		
		assertEquals(utcDate, current);
	}

	/**
	 * Test method for {@link com.anxlng.util.TimeFunc#defaultToUtc(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDefaultToUtc() {
		
		String utcDate = defaultToUtc(current, TimeFunc.DATE_TIME);
		String defaultDate = utcToDefault(utcDate, TimeFunc.DATE_TIME);
		
		assertEquals(defaultDate, current);
	}

	/**
	 * Test method for {@link com.anxlng.util.TimeFunc#stringtoLong(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testStringtoLongStringString() {
		
		assertEquals(stringtoLong(current, DATE_TIME), currentMillis);
	}

	/**
	 * Test method for {@link com.anxlng.util.TimeFunc#longtoString(long, java.lang.String)}.
	 */
	@Test
	public void testLongtoStringLongString() {
		
		assertEquals(longtoString(currentMillis, DATE_TIME), current);
	}

}
