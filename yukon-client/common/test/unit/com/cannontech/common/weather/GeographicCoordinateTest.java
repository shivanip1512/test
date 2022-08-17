package com.cannontech.common.weather;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.weather.GeographicCoordinate;

public class GeographicCoordinateTest {

    @Test
    public void test_distanceTo_samePoint() {
        GeographicCoordinate loc = new GeographicCoordinate(0, 0);
        GeographicCoordinate loc2 = new GeographicCoordinate(0, 0);
        
        GeographicCoordinate anotherLoc = new GeographicCoordinate(56, -50);
        GeographicCoordinate anotherLoc2 = new GeographicCoordinate(56, -50);

        assertTrue("Distance should be zero.", loc.distanceTo(loc2) == 0.0);
        assertTrue("Distance should be equal.", loc.distanceTo(loc2) == loc2.distanceTo(loc));
        assertTrue("Distance should be zero.", anotherLoc.distanceTo(anotherLoc2) == 0.0);
        assertTrue("Distance should be equal.",
                   anotherLoc.distanceTo(anotherLoc2) == anotherLoc2.distanceTo(anotherLoc));
        
        assertFalse("Distance should be zero.", loc.distanceTo(anotherLoc) == 0.0);
    }

    @Test
    public void test_distanceTo_knownDistance() {
        GeographicCoordinate loc1 = new GeographicCoordinate(43.536863, -96.663927);
        GeographicCoordinate loc2 = new GeographicCoordinate(44.974461, -93.283925);

        double distance = loc1.distanceTo(loc2);

        assertCloseEnough("Distance should be 194.5. was " + distance, distance, 194.5);
    }

    @Test
    public void test_distanceTo_NorthToSouthPole() {
        GeographicCoordinate loc1 = new GeographicCoordinate(90, 0);
        GeographicCoordinate loc2 = new GeographicCoordinate(-90, 0);

        double distance = loc1.distanceTo(loc2);

        assertCloseEnough("Distance should be ~12,436.78 but was " + distance, distance, 12_436.78);
    }

    private void assertCloseEnough(String message, double a, double b) {
        if (!(Math.floor(a * 10) == Math.floor(b * 10))) {
            Assert.fail(message);
        }
    }
}
