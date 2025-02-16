package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pom.utils.ConveyorUtil;
import org.pom.utils.MathUtil;
import org.pom.utils.ParametersValidator;

import java.util.*;

/**
 * Represents a speed model that maps a given value of tau to its corresponding speed.
 * This class allows adding speed values for specific tau values, retrieving the speed for a given tau,
 * and performing validations on speed and tau ranges.
 * <p>
 * It uses a {@link TreeMap} to store the tau to speed mappings.
 */
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class Speed implements KeysValuesProvider<Double> {
    @Getter
    private TreeMap<Double, Double> tauToSpeedMap;
    private final double maxAvailableSpeed;
    private final double minAvailableSpeed;

    /**
     * Creates a new {@link Speed} object with the specified minimum and maximum available speeds.
     * Validates the speed range to ensure it is non-negative and the minimum speed is less than or equal
     * to the maximum speed.
     *
     * @param minAvailableSpeed The minimum available speed.
     * @param maxAvailableSpeed The maximum available speed.
     * @throws IllegalArgumentException if the speed range is invalid (negative or min > max).
     */
    @JsonCreator
    public Speed(
            @JsonProperty("minAvailableSpeed") double minAvailableSpeed,
            @JsonProperty("maxAvailableSpeed") double maxAvailableSpeed,
            @JsonProperty("speed") TreeMap<Double, Double> tauToSpeedMap) throws IllegalArgumentException {
        validateAvailableSpeed(minAvailableSpeed, maxAvailableSpeed);
        this.maxAvailableSpeed = maxAvailableSpeed;
        this.minAvailableSpeed = minAvailableSpeed;
        this.tauToSpeedMap = tauToSpeedMap;
        log.info("Speed object created with minAvailableSpeed: {} and maxAvailableSpeed: {}",
                minAvailableSpeed, maxAvailableSpeed);
    }

    /**
     * Retrieves the speed corresponding to the specified tau value.
     *
     * @param tau The tau value.
     * @return The speed corresponding to the given tau value.
     */
    public Double getSpeedAtTau(double tau) {
        return MathUtil.getValueByKey(tauToSpeedMap, tau);
    }

    /**
     * Adds a new speed value for the specified tau. This method validates that both tau and speed are non-negative
     * before adding the pair to the mapping.
     *
     * @param tau The tau value.
     * @param speed The speed value corresponding to the tau.
     * @throws IllegalArgumentException if tau or speed is negative.
     */
    public void addParametersValues(double tau, double speed) {
        ParametersValidator.validateNonNegativeKeyValue(
                "Speed", Map.of("tau", tau, "speed", speed)
        );

        this.tauToSpeedMap.put(tau, speed);
        log.debug("Speed value added. Tau: {}, Speed: {}", tau, speed);
    }

    private static void validateAvailableSpeed(double minAvailableSpeed, double maxAvailableSpeed) {
        if (minAvailableSpeed < 0 || maxAvailableSpeed < minAvailableSpeed) {
            log.error("Invalid speed range: minAvailableSpeed ({}) must be non-negative and less than or equal " +
                            "to maxAvailableSpeed ({}).", minAvailableSpeed, maxAvailableSpeed);
            throw new IllegalArgumentException("Invalid speed range: minAvailableSpeed (" + minAvailableSpeed +
                    ") must be non-negative and less than or equal to maxAvailableSpeed (" + maxAvailableSpeed + ").");
        }
    }

    public void setValues(TreeMap<Double, Double> tauToSpeedMap) {
        this.tauToSpeedMap = tauToSpeedMap;
    }

    @Override
    public Collection<Double> values() {
        return Collections.unmodifiableCollection(tauToSpeedMap.values());
    }

    @Override
    public Collection<Double> keys() {
        return Collections.unmodifiableCollection(tauToSpeedMap.keySet());
    }

    public void fillEmptyParametersByCurrentTau(double currentTau, double previousFinishTime, List<Double> taus) {
        ConveyorUtil.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus, tauToSpeedMap, 0.0);
    }
}
