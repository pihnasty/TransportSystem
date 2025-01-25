package org.pom;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class BunkerTest {

    private Bunker bunker;
    private final List<Double> taus
            = List.of(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0);
    private final List<Double> inputs
            = List.of(10.0, 11.0, 0.0, 0.0, 120.0, 10.0, 10.0, 20.0, 120.0, 30.0, 10.0, 140.0, 140.0);
    private final List<Double> bunkerPlanedOutputFlows
            = List.of(5.0, 6.0, 90.0, 10.0, 0.0, 20.0, 120.0, 30.0, 10.0, 150.0, 10.0, 140.0, 140.0);
    private final List<Double> bunkerRealOutputFlows
            = List.of(5.0, 6.0, 60.0, 0.0, 0.0, 20.0, 100.0, 20.0, 10.0, 130.0, 10.0, 130.0, 130.0);
    private final List<Double> validFlowFromBunkerToConveyorBelt
            = List.of(5.0, 6.0, 60.0, 0.0, 0.0, 20.0, 80.0, 20.0, 10.0, 80.0, 10.0, 80.0, 80.0);
    private final List<Double> validCapacities
            = List.of(50.0, 55.0, 60.0, 0.0, 0.0, 100.0, 90.0, 0.0, 0.0, 100.0, 0.0, 0.0, 10.0);
    private final List<Double> validDensityOverMaxCapacities
            = List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0,  0.0, 20.0, 20.0, 20.0, 70.0, 70.0, 120.0);
    private final List<Double> validOverMaxCapacities
            = List.of(0.0, 0.0, 0.0, 0.0, 0.0, 20.0, 20.0, 20.0, 20.0, 30.0, 30.0, 30.0, 30.0, 30.0);
    private double outputForMaxDensity;

    @BeforeEach
    void setUp() {
        var initialCapacity = 50.0;
        var maxCapacity = 100.0;
        var bunkerOverMaxCapacity = 0.0;
        var densityOverMaxCapacity = 0.0;
        var minAvailableCapacity = 20.0;
        var maxAvailableCapacity = 80.0;
        var maxAvailableOutput = 130.0;
        this.outputForMaxDensity = 80.0;
        bunker = new Bunker(initialCapacity, bunkerOverMaxCapacity, densityOverMaxCapacity, maxCapacity,
                minAvailableCapacity, maxAvailableCapacity, maxAvailableOutput);
        for (var tau : taus) {
            bunker.addParametersValues(tau, inputs.get(taus.indexOf(tau)), bunkerPlanedOutputFlows.get(taus.indexOf(tau)), outputForMaxDensity);
        }
        System.out.println();
    }

    @Test
    void testAddParameters_validCapacity() {
        for (var tau : taus) {
            log.info("tau : {}, validCapacities: {}, bunker.getCapacityAtTau: {}",tau, validCapacities.get(taus.indexOf(tau)), bunker.getCapacityAtTau(tau));
            assertEquals(validCapacities.get(taus.indexOf(tau)), bunker.getCapacityAtTau(tau));
        }
    }

//    @Test
//    void testAddParameters_validOutputRealFlowFromBunker() {
//        for (var tau : taus) {
//            log.info("tau : {}, bunkerRealOutputFlows: {}, bunker.getOutputRealFlowFromBunker: {}",tau, bunkerRealOutputFlows.get(taus.indexOf(tau)), bunker.getOutputRealFlowFromBunker(tau));
//            assertEquals(bunkerRealOutputFlows.get(taus.indexOf(tau)), bunker.getOutputRealFlowFromBunker(tau));
//        }
//    }

//    @Test
//    void testAddParameters_validOutputFlowFromBunkerToConveyorBelt() {
//        for (var tau : taus) {
//            log.info("tau : {}, validFlowFromBunkerToConveyorBelt: {}, bunker.getOutputFlowFromBunkerToConveyorBelt: {}",tau, validFlowFromBunkerToConveyorBelt.get(taus.indexOf(tau)), bunker.getOutputFlowFromBunkerToConveyorBelt(tau));
//            assertEquals(validFlowFromBunkerToConveyorBelt.get(taus.indexOf(tau)), bunker.getOutputFlowFromBunkerToConveyorBelt(tau));
//        }
//    }

//    @Test
//    void testAddParameters_validBelowMinCapacities() {
//        for (var tau : taus) {
//            log.info("tau : {}, validBelowMinCapacities: {}, bunker.getDensityOverMaxCapacityAtTau: {}",tau, validDensityOverMaxCapacities.get(taus.indexOf(tau)), bunker.getDensityOverMaxCapacityAtTau(tau));
//            assertEquals(validDensityOverMaxCapacities.get(taus.indexOf(tau)), bunker.getDensityOverMaxCapacityAtTau(tau));
//        }
//    }

    @Test
    void testAddParameters_validOverMaxCapacities() {
        for (var tau : taus) {
            log.info("tau : {}, validOverMaxCapacities: {}, bunker.getOverMaxCapacityAtTau: {}",tau, validOverMaxCapacities.get(taus.indexOf(tau)), bunker.getOverMaxCapacityAtTau(tau));
            assertEquals(validOverMaxCapacities.get(taus.indexOf(tau)), bunker.getOverMaxCapacityAtTau(tau));
        }
    }

    @Test
    void testAddCharacteristics_negativeInput() {
        assertThrows(IllegalArgumentException.class, () -> bunker.addParametersValues(-1.0, 10.0, 10.0, this.outputForMaxDensity));
        assertThrows(IllegalArgumentException.class, () -> bunker.addParametersValues(1.0, -10.0, 10.0, this.outputForMaxDensity));
        assertThrows(IllegalArgumentException.class, () -> bunker.addParametersValues(1.0, 10.0, -10.0, this.outputForMaxDensity));
    }
}
