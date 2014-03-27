package de.eduras.eventingserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.eduras.eventingserver.CannotSplitException;
import de.eduras.eventingserver.SplitByLastFullMessagePolicy;

public class SplitLocationTest {

	@Test
	public void testSplitByLastFullMessage() {
		SplitByLastFullMessagePolicy splitPolicy = new SplitByLastFullMessagePolicy();

		try {
			assertEquals(splitPolicy.determineSplitLocation(2, "####"), 2);
		} catch (CannotSplitException e1) {
			assertTrue(false);
		}

		try {
			splitPolicy.determineSplitLocation(0, "");
			assertTrue(false);
		} catch (CannotSplitException e) {
			assertTrue(true);
		}

		try {
			splitPolicy.determineSplitLocation(5, "##sadwadasdwadd##ads");
			assertTrue(false);
		} catch (CannotSplitException e) {
			assertTrue(true);
		}

		try {
			assertEquals(10,
					splitPolicy.determineSplitLocation(10, "##sadwadas##ads"));
		} catch (CannotSplitException e) {
			assertTrue(false);
		}

		try {
			assertEquals(20, splitPolicy.determineSplitLocation(20,
					"##sadwadas##sadwadas##sadwadas"));
		} catch (CannotSplitException e) {
			assertTrue(false);
		}

		try {
			assertEquals(20, splitPolicy.determineSplitLocation(23,
					"##sadwadas##sadwadas##sadwadas"));
		} catch (CannotSplitException e) {
			assertTrue(false);
		}

		try {
			assertEquals(
					20,
					splitPolicy
							.determineSplitLocation(23,
									"##sadwadas##sadwadas##sadwadas355324232324312233254fdsgfgfdg"));
		} catch (CannotSplitException e) {
			assertTrue(false);
		}
	}
}
