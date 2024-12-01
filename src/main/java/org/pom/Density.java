package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pom.utils.MathUtil;
import org.pom.utils.ParametersValidator;

import java.util.Map;
import java.util.TreeMap;
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class Density {
    @Getter
    private final double maxAvailableDensity;
    private final TreeMap<Double, Double> tauToDensityMap;

    @JsonCreator
    public Density(@JsonProperty("maxAvailableDensity") double maxAvailableDensity) {
        this.maxAvailableDensity = maxAvailableDensity;
        this.tauToDensityMap = new TreeMap<>();
    }

    public void addParametersValues(double tau, double density) {
        validateValues(tau, density);
        this.tauToDensityMap.put(tau, density);
        log.debug("Density value added. Tau: {}, Density: {}", tau, density);
    }

    /**
     * Retrieves the density at a specific time at the input of the conveyor.
     *
     * @param tau The time for which to retrieve the input of the conveyor.
     * @return The density at the input of the conveyor at time {@code tau}.
     */
    public double getDensity(double tau) {
        return getParameterAtTau(tau);
    }

    private void validateValues(double tau, double density) {
        ParametersValidator.validateNonNegativeKeyValue(
                "Density", Map.of(Constants.TAU, tau, Constants.DENSITY, density)
        );
        ParametersValidator.validateMaxValue(
                "density", density, maxAvailableDensity, Map.of(Constants.TAU, tau, Constants.DENSITY, density)
        );
    }

    private double getParameterAtTau(double tau) {
        return MathUtil.getValueByKey(tauToDensityMap, tau);
    }
}
