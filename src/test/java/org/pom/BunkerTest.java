package org.pom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BunkerTest {

    private Bunker bunker;
    private final List<Double> taus
            = List.of(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
    private final List<Double> inputs
            = List.of(10.0, 11.0, 0.0, 0.0, 120.0, 10.0, 10.0, 20.0, 120.0, 30.0, 10.0);
    private final List<Double> outputs
            = List.of(5.0, 6.0, 90.0, 10.0, 0.0, 20.0, 120.0, 30.0, 10.0, 150.0, 10.0);
    private final List<Double> validOutputs
            = List.of(5.0, 6.0, 60.0, 0.0, 0.0, 20.0, 100.0, 20.0, 10.0, 130.0, 10.0);
    private final List<Double> validCapacities
            = List.of(50.0, 55.0, 60.0, 0.0, 0.0, 100.0, 90.0, 0.0, 0.0, 100.0, 0.0);
    private final List<Double> validBelowMinCapacities
            = List.of(0.0, 0.0, -30.0, -40.0, -40.0, -40.0, -60.0, -70.0, -70.0, -90.0, -90.0);
    private final List<Double> validOverMaxCapacities
            = List.of(0.0, 0.0, 0.0, 0.0, 20.0, 20.0, 20.0, 20.0, 30.0, 30.0, 30.0);

    @BeforeEach
    void setUp() {
        double initialCapacity = 50.0;
        double maxCapacity = 100.0;
        double minAvailableCapacity = 20.0;
        double maxAvailableCapacity = 80.0;
        bunker = new Bunker(initialCapacity, maxCapacity, minAvailableCapacity, maxAvailableCapacity);
        for (Double tau : taus) {
            bunker.addParametersValues(tau, inputs.get(taus.indexOf(tau)), outputs.get(taus.indexOf(tau)));
        }
    }

    @Test
    void testAddParameters_validCapacity() {
        for (Double tau : taus) {
            assertEquals(validCapacities.get(taus.indexOf(tau)), bunker.getCapacityAtTau(tau));
        }
    }

    @Test
    void testAddParameters_validOutput() {
        for (Double tau : taus) {
            assertEquals(validOutputs.get(taus.indexOf(tau)), bunker.getOutputFlowAtTau(tau));
        }
    }

    @Test
    void testAddParameters_validBelowMinCapacities() {
        for (Double tau : taus) {
            assertEquals(validBelowMinCapacities.get(taus.indexOf(tau)), bunker.getBelowMinCapacityAtTau(tau));
        }
    }

    @Test
    void testAddParameters_validOverMaxCapacities() {
        for (Double tau : taus) {
            assertEquals(validOverMaxCapacities.get(taus.indexOf(tau)), bunker.getOverMaxCapacityAtTau(tau));
        }
    }

    @Test
    void testAddCharacteristics_negativeInput() {
        assertThrows(IllegalArgumentException.class, () -> bunker.addParametersValues(-1.0, 10.0, 10.0));
        assertThrows(IllegalArgumentException.class, () -> bunker.addParametersValues(1.0, -10.0, 10.0));
        assertThrows(IllegalArgumentException.class, () -> bunker.addParametersValues(1.0, 10.0, -10.0));
    }
}
