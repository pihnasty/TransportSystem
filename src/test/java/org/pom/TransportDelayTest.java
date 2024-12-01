package org.pom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TransportDelayTest {

    private static TransportDelay transportDelay;

    @BeforeAll
    static void setUp() {
        transportDelay = new TransportDelay();
        for (double tau = 0.0; tau < 1.0; tau += 0.0001) {
            var speed = 1.0 +tau;
            transportDelay.addParametersValues(tau, speed);
        }
    }

    @Test
    void testAddDistanceValue_ValidParameters() {
        // Add a valid distance value and ensure no exceptions occur
        var localTransportDelay = new TransportDelay();
        assertDoesNotThrow(() -> localTransportDelay.addParametersValues(5.0, 10.0));
    }

    @Test
    void testAddDistanceValue_InvalidParameters() {
        // Attempt to add invalid parameters and ensure the appropriate exception is thrown
        var localTransportDelay = new TransportDelay();
        assertThrows(IllegalArgumentException.class, () ->  localTransportDelay.addParametersValues(-5.0, 10.0));
        assertThrows(IllegalArgumentException.class, () ->  localTransportDelay.addParametersValues(5.0, -10.0));
    }

    @Test
    void testGetDelayByDistance_SimpleCase() {
        var delay = transportDelay.getDelayByDeltaDistance(1.0);
        assertNotNull(delay);
        assertTrue(delay > 0); // Verify a reasonable delay
    }

    @Test
    void testAddDistanceValueAndRetrieve() {
        // Retrieve delay by distance and validate the output
        var delay1 = transportDelay.getDelayByDeltaDistance(0.8);
        var delay2 = transportDelay.getDelayByDeltaDistance(0.7);

        assertNotNull(delay1);
        assertNotNull(delay2);
        assertTrue(delay1 > delay2); // Delay for larger distances should be greater
    }

    @Test
    void testBoundaryValues() {
        // Retrieve delay and validate behavior
        var delay = transportDelay.getDelayByDeltaDistance(0.0);
        assertNotNull(delay);
        assertEquals(0.0, delay); // No speed or distance change should yield zero delay
    }

    @Test
    void testGetDeltaDistanceFromStart() {
        assertEquals(0.625, transportDelay.getDeltaDistanceFromStart(0.5), 2.0 * TestConstant.DELTA_LENGTH);
        assertEquals(0.0, transportDelay.getDeltaDistanceFromStart(0.0), 2.0 * TestConstant.DELTA_LENGTH);
        assertThrows(IllegalArgumentException.class,
                () -> transportDelay.getDeltaDistanceFromStart(-0.1), TestConstant.ARGUMENT_OUT_OF_RANGE_MESSAGE);
    }
}
