package util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BitUtilTest {

	@Test
	public void testMatchesSingleBitPattern() {

		assertTrue(BitUtil.matchesBitPattern("0", 0));
		assertTrue(BitUtil.matchesBitPattern("1", 1));
		assertTrue(BitUtil.matchesBitPattern("x", 0));
		assertTrue(BitUtil.matchesBitPattern("x", 1));

		assertFalse(BitUtil.matchesBitPattern("0", 1));
		assertFalse(BitUtil.matchesBitPattern("1", 0));

	}

	@Test
	public void testMatchesTwoBitPattern() {

		assertTrue(BitUtil.matchesBitPattern("00", 0));
		assertTrue(BitUtil.matchesBitPattern("01", 1));
		assertTrue(BitUtil.matchesBitPattern("10", 2));
		assertTrue(BitUtil.matchesBitPattern("11", 3));
		
		assertTrue(BitUtil.matchesBitPattern("xx", 0));
		assertTrue(BitUtil.matchesBitPattern("xx", 00));
		assertTrue(BitUtil.matchesBitPattern("xx", 01));
		assertTrue(BitUtil.matchesBitPattern("xx", 2));
		assertTrue(BitUtil.matchesBitPattern("xx", 3));

		assertFalse(BitUtil.matchesBitPattern("00", 1));
		assertFalse(BitUtil.matchesBitPattern("01", 0));

	}
	
	@Test
	public void testMatchesBitPattern() {
		assertTrue(BitUtil.matchesBitPattern("11101000", 0xE8));
		assertFalse(BitUtil.matchesBitPattern("11111000", 0xE8));
		
		assertTrue(BitUtil.matchesBitPattern("101xxx10", 0xA2));
		assertTrue(BitUtil.matchesBitPattern("101xxx10", 0xA6));
		assertTrue(BitUtil.matchesBitPattern("101xxx10", 0xAA));
		assertTrue(BitUtil.matchesBitPattern("101xxx10", 0xAE));
		assertTrue(BitUtil.matchesBitPattern("101xxx10", 0xB2));
		assertTrue(BitUtil.matchesBitPattern("101xxx10", 0xB6));
		assertTrue(BitUtil.matchesBitPattern("101xxx10", 0xBA));
		assertTrue(BitUtil.matchesBitPattern("101xxx10", 0xBE));
	}

}
