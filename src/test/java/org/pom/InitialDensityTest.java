package org.pom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class InitialDensityTest {

    private static InitialDensity initialDensity;


    @BeforeAll
    static void setUp() {
        TreeMap<Double, Double> densityMap = new TreeMap<>();
        for (double distance = 0.0;
             distance < TestConstant.CONVEYOR_LENGTH + TestConstant.DELTA_LENGTH; distance += TestConstant.DELTA_LENGTH) {
            var density = 1.0 +distance;
            densityMap.put(distance, density);
        }
        initialDensity = new InitialDensity(densityMap);
    }

    @Test
    void testGetDensityAtDeltaDistanse() {
        assertEquals(1.0, initialDensity.getDensityAtDistance(0.0));
        assertEquals(2.0, initialDensity.getDensityAtDistance(TestConstant.CONVEYOR_LENGTH), TestConstant.DELTA_LENGTH);
    }

    @Test
    void testGetDensityAtDeltaDistanseNotFound() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> initialDensity.getDensityAtDistance(2.0 * TestConstant.CONVEYOR_LENGTH)
        );
        assertTrue(thrown.getMessage().contains(TestConstant.ARGUMENT_OUT_OF_RANGE_MESSAGE));
    }
}
