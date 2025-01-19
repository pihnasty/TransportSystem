package org.pom;

import lombok.extern.slf4j.Slf4j;
import org.pom.utils.MathUtil;
import org.pom.utils.ParametersValidator;

import java.util.Map;
import java.util.TreeMap;

/**
 * The {@code TransportDelay} class models the relationship between delay and distance
 * for a transport system. It allows adding parameters such as delay ({@code tau})
 * and speed, and calculates distances and corresponding delay values.
 *
 * <p>This class relies on utility classes {@code ParametersValidator} and {@code MathUtil}
 * for validation and calculations, respectively. It also uses a {@link TreeMap} to maintain
 * a sorted collection of delay-to-distance mappings.</p>
 *
 * <p>The class logs debug information using SLF4J.</p>
 */
@Slf4j
public class TransportDelay {

    private final TreeMap<Double, Map<String, Double>> delayToDistance;
    private final TreeMap<Double, Double> distanceToDelay;

    /**
     * Constructs an empty {@code TransportDelay} object with an initialized
     * {@link TreeMap} for managing delay-to-distance mappings.
     */
    public TransportDelay() {
        this.delayToDistance = new TreeMap<>();
        this.distanceToDelay = new TreeMap<>();
    }

    /**
     * Adds a new delay ({@code tau}) and speed value to the delay-to-distance mapping.
     * Validates the parameters before adding them. Calculates the distance based on
     * the provided delay and adds it to the mapping.
     *
     * @param tau   the delay value to be added.
     * @param speed the speed value corresponding to the delay.
     * @throws IllegalArgumentException if {@code tau} or {@code speed} are invalid.
     */
    public void addParametersValues(double tau, double speed) {
        ParametersValidator.validateNonNegativeKeyValue(
                "Speed", Map.of("tau", tau, "speed", speed)
        );
        var distance = calculateDistance(tau);
        delayToDistance.put(distance, Map.of(Constants.TAU, tau, Constants.SPEED, speed));
        distanceToDelay.put(tau, distance);
        log.debug("Distance value added. Distance: {}, Tau: {}, Speed: {}", distance, tau, speed);
    }

    /**
     * Calculates the delay difference for a given distance adjustment ({@code deltaDistance}).
     * Finds the delay value associated with the adjusted distance and computes
     * the difference from the most recent delay value.
     *
     * @param deltaDistance the distance adjustment for which the delay difference is calculated.
     * @return the difference in delay values.
     */
    public Double getDelayByDeltaDistance(double deltaDistance) {
        var lastEntry = delayToDistance.lastEntry();
        var distance = lastEntry.getKey() - deltaDistance;
        return distance < 0.0
                ? distance
                :lastEntry.getValue().get(Constants.TAU)
                - MathUtil.getValueByKey(delayToDistance, distance).get(Constants.TAU);
    }

    /**
     * Calculates the difference in distance between the specified tau and the starting tau.
     * This method retrieves the distance associated with the given delay tau and subtracts
     * the distance corresponding to the first (earliest) tau in the mapping. The result
     * represents the delta distance relative to the starting point.
     *
     * @param tau the tau for which the delta distance is calculated.
     * @return the difference in distance between tau and the starting tau.
     */
    public Double getDeltaDistanceFromStart(double tau) {
        return MathUtil.getValueByKey(distanceToDelay, tau) - distanceToDelay.firstEntry().getValue();
    }

    private double calculateDistance(double tau) {
        return delayToDistance.isEmpty()
                ? 0.0
                : delayToDistance.lastKey() +
                delayToDistance.lastEntry().getValue().get(Constants.SPEED) *
                        (tau - delayToDistance.lastEntry().getValue().get(Constants.TAU));
    }
}
