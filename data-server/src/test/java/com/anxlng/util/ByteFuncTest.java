/**
 * data-server. 2012-6-27
 */
package com.anxlng.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static com.anxlng.util.ByteFunc.*;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class ByteFuncTest {

	private byte[] b0 = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
	private byte[] b1 = {(byte)0xff, (byte)0xff, (byte)0x80, (byte)0x01};
	private byte[] b2 = {(byte)0x80, (byte)0x01};
	private byte[] b3 = {(byte)0x01, (byte)0x98};
	private byte[] b4 = {(byte)0x00, (byte)0x11};
	private byte[] b5 = {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#printHex(byte[])}.
	 */
	@Test
	public void testPrintHexByteArray() {
	
		assertEquals("00 00 00 00", printHex(b0));
		assertEquals("ff ff 80 01", printHex(b1));
		assertEquals("80 01", printHex(b2));
		assertEquals("01 98", printHex(b3));
		assertEquals("00 11", printHex(b4));
		assertEquals("ff ff ff ff", printHex(b5));
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#printHex(byte[], int, int)}.
	 */
	@Test
	public void testPrintHexByteArrayIntInt() {
		
		assertEquals("00 00", printHex(b0, 0, 2));
		assertEquals("80 01", printHex(b1, 2, 2));
		assertEquals("01", printHex(b2, 1, 1));
		assertEquals("98", printHex(b3, 1, 1));
		assertEquals("11", printHex(b4, 1, 1));
		assertEquals("ff", printHex(b5, 3, 1));
		assertEquals("", printHex(b0, 3, 0));
//		assertEquals("00", printHex(b0, 3, 100));
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toHex(byte[])}.
	 */
	@Test
	public void testToHexByteArray() {
		assertEquals("00000000", toHex(b0));
		assertEquals("ffff8001", toHex(b1));
		assertEquals("8001", toHex(b2));
		assertEquals("0198", toHex(b3));
		assertEquals("0011", toHex(b4));
		assertEquals("ffffffff", toHex(b5));
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toHex(byte[], int, int)}.
	 */
	@Test
	public void testToHexByteArrayIntInt() {
		assertEquals("0000", toHex(b0, 0, 2));
		assertEquals("8001", toHex(b1, 2, 2));
		assertEquals("01", toHex(b2, 1, 1));
		assertEquals("98", printHex(b3, 1, 1));
		assertEquals("11", toHex(b4, 1, 1));
		assertEquals("ff", toHex(b5, 3, 1));
//		assertEquals("ff", toHex(b5, 3, 10));
		assertEquals("", toHex(b5, 3, 0));
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#arrayEquals(byte[], int, byte[], int, int)}.
	 */
	@Test
	public void testArrayEquals() {
		
		assertTrue(arrayEquals(b1, 2, b2, 0, 2));
		assertTrue(arrayEquals(b1, 0, b5, 0, 2));
		assertTrue(arrayEquals(b0, 0, b0, 2, 2));
		assertFalse(arrayEquals(b5, 0, b4, 0, 2));
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toInt2(byte[])}.
	 */
	@Test
	public void testToInt2ByteArray() {
		
		assertEquals(toInt2(b0), 0);
		assertEquals(toInt2(b1), 65535);
		assertEquals(toInt2(b2), 32769);
		assertEquals(toInt2(b3), 408);
		assertEquals(toInt2(b4), 17);
		assertEquals(toInt2(b5), 65535);
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toInt2(byte[], int)}.
	 */
	@Test
	public void testToInt2ByteArrayInt() {
		
		assertEquals(toInt2(b0, 2), 0);
		assertEquals(toInt2(b1, 2), 32769);
		assertEquals(toInt2(b2, 0), 32769);
		assertEquals(toInt2(b3, 0), 408);
		assertEquals(toInt2(b4, 0), 17);
		assertEquals(toInt2(b5, 2), 65535);
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toInt4(byte[])}.
	 */
	@Test
	public void testToInt4ByteArray() {
		assertEquals(toInt4(b0), 0);
		assertEquals(toInt4(b1), -32767);
		assertEquals(toInt4(b5), -1);
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toInt4(byte[], int)}.
	 */
	@Test
	public void testToInt4ByteArrayInt() {
		
		assertEquals(toInt4(b0, 0), 0);
		assertEquals(toInt4(b1, 0), -32767);
		assertEquals(toInt4(b5, 0), -1);
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toInt(byte[], int, int)}.
	 */
	@Test
	public void testToInt() {

		assertEquals(toInt(b0, 0, 3), 0);
		assertEquals(toInt(b1, 1, 3), 16744449);
		assertEquals(toInt(b5, 1, 3), 16777215);
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toBinary(byte[])}.
	 */
	@Test
	public void testToBinaryByteArray() {
		
		assertEquals(toBinary(b0), "00000000000000000000000000000000");
		assertEquals(toBinary(b1), "11111111111111111000000000000001");
		assertEquals(toBinary(b2), "1000000000000001");
	}

	/**
	 * Test method for {@link com.anxlng.util.ByteFunc#toBinary(byte[], int, int)}.
	 */
	@Test
	public void testToBinaryByteArrayIntInt() {
		
		assertEquals(toBinary(b0, 2, 1), "00000000");
		assertEquals(toBinary(b1, 0, 1), "11111111");
		assertEquals(toBinary(b5, 3, 1), "11111111");
//		assertEquals(toBinary(b0, 2, 100), "0000000000000000");
		assertEquals(toBinary(b0, 2, 0), "");
	}

}
