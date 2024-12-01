package org.pom;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DensityTest {

    private Density density;
    private final List<Double> taus
            = List.of(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
    private final List<Double> validDensities
            = List.of(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);

    @BeforeEach
    void setUp() {
        density = new Density(TestConstant.MAX_AVAILABLE_DENSITY);
        for (var tau : taus) {
            density.addParametersValues(tau, validDensities.get(taus.indexOf(tau)));
        }
    }

    @Test
    void testAddParameters_validDEnsity() {
        for (var tau : taus) {
            log.info("tau : {}, validDensities: {}, density.getDensity: {}",tau, validDensities.get(taus.indexOf(tau)), density.getDensity(tau));
            assertEquals(validDensities.get(taus.indexOf(tau)), density.getDensity(tau));
        }
    }

    @Test
    void getMaxAvailableDensity() {
        assertEquals(TestConstant.MAX_AVAILABLE_DENSITY, density.getMaxAvailableDensity());
    }
}