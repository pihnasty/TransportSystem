package org.pom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class SpeedTest {

    private static Speed speed;

    @BeforeAll
    static void setUp() {
        speed = new Speed(0, TestConstant.DEFAULT_SPEED_VALUE, new TreeMap<>());
        speed.addParametersValues(10, 30);
        speed.addParametersValues(20, 50);
    }

    @Test
    void testConstructor_InvalidRange() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Speed(-10, 100, new TreeMap<>()));
        assertEquals("Invalid speed range: minAvailableSpeed (-10.0) must be non-negative and less than or equal to maxAvailableSpeed (100.0).", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> new Speed(50, 40, new TreeMap<>()));
        assertEquals("Invalid speed range: minAvailableSpeed (50.0) must be non-negative and less than or equal to maxAvailableSpeed (40.0).", exception.getMessage());
    }

    @Test
    void testAddSpeedValue_ValidInputs() {
        assertDoesNotThrow(() -> speed.addParametersValues(10.5, 50.2));
    }

    @Test
    void testAddSpeedValue_InvalidInputs() {
        var negativeTau = -1.0;
        assertThrows(IllegalArgumentException.class, () -> speed.addParametersValues(negativeTau, TestConstant.DEFAULT_SPEED_VALUE));
        var tau = 10.0;
        var negativeSpeedValue = -50.0;
        assertThrows(IllegalArgumentException.class, () -> speed.addParametersValues(tau, negativeSpeedValue));
    }

    @Test
    void testGetSpeedAtTau_ExactMatch() {
        speed.addParametersValues(10, 30);
        assertEquals(30, speed.getSpeedAtTau(10));
    }

    @Test
    void testGetSpeedAtTau_NoMatch() {
        assertThrows(IllegalArgumentException.class, () -> speed.getSpeedAtTau(5));
    }

    @Test
    void testGetSpeedAtTau_InterpolatedValue() {
        assertEquals(50, speed.getSpeedAtTau(15));
    }
}
