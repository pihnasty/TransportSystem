package org.pom;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pom.utils.MathUtil;
import org.pom.utils.ParametersValidator;

import java.util.*;

/**
 * The OutputFlow class calculates and manages the output flow values of a transport system
 * based on provided bunker flow rates, speed, initial density, and transport delay mappings.
 * The class uses a TreeMap to maintain a mapping of tau values to output flow values and
 * provides methods to add new flow values or retrieve existing ones. It also contains helper methods
 * for flow calculations based on delay or initial density.
 */
@Slf4j
public class OutputFlow implements KeysValuesProvider<Double> {
    private final Bunker bunker;
    private final Speed speed;
    private final InitialDensity initialDensity;
    private final TransportDelay delay;
    @Getter
    private final TreeMap<Double, Double> tauToFlowOutputMap;

    public OutputFlow() {
        this(null, null, null, null);
    }

    /**
     * Constructs an OutputFlow object with the specified dependencies.
     *
     * @param bunker         The bunker for output flow data.json.
     * @param speed          The speed for speed data.json.
     * @param initialDensity The initialDensity for density data.json.
     * @param transportDelay The TransportDelay for delay mappings.
     */
    public OutputFlow(Bunker bunker, Speed speed, InitialDensity initialDensity, TransportDelay transportDelay) {
        this.bunker = bunker;
        this.speed = speed;
        this.initialDensity = initialDensity;
        this.delay = transportDelay;
        this.tauToFlowOutputMap = new TreeMap<>();
        log.debug("OutputFlow initialized with Bunker, Speed, InitialDensity, and TransportDelay dependencies.");
    }

    /**
     * Adds a new output flow value for a given tau and delay tau.
     *
     * @param tau      The tau value for which the output flow is being added.
     * @param delayTau The delay tau value, determining the calculation method.
     */
    public void addOutputFlowValue(double tau, double delayTau) {
        ParametersValidator.validateNonNegativeKeyValue("OutputFlow", Map.of("tau", tau));
        log.debug("Speed value added. Tau: {}, Speed: {}", tau, speed);
        double flowOutput= (delayTau >= 0)
                ? calculateOutputFlowForDelay(tau, delayTau)
                : calculateOutputFlowForInitialDensity(tau);
        this.tauToFlowOutputMap.put(tau, flowOutput);
        log.debug("Output flow value added. Tau: {}, FlowOutput: {}", tau, flowOutput);
    }

    /**
     * Retrieves the output flow corresponding to the specified tau value.
     *
     * @param tau The tau value.
     * @return The output flow corresponding to the given tau value.
     */
    public Double getOutputFlowAtTau(double tau) {
        return MathUtil.getValueByKey(this.tauToFlowOutputMap, tau);
    }

    private double calculateOutputFlowForDelay(double tau, double delayTau) {
        return this.bunker.getOutputFlowFromBunkerToConveyorBelt(tau - delayTau) / this.speed.getSpeedAtTau(tau - delayTau)
                * this.speed.getSpeedAtTau(tau);
    }

    private double calculateOutputFlowForInitialDensity(double tau) {
        return this.initialDensity.getDensityAtDistance(this.delay.getDeltaDistanceFromStart(tau))
                * this.speed.getSpeedAtTau(tau);
    }

    @Override
    public Collection<Double> values() {
        return Collections.unmodifiableCollection(tauToFlowOutputMap.values());
    }

    @Override
    public Collection<Double> keys() {
        return  Collections.unmodifiableCollection(tauToFlowOutputMap.keySet());
    }

    public Double lastKey() {
        return Objects.nonNull(tauToFlowOutputMap) ? tauToFlowOutputMap.lastKey() : 0.0;
    }
}
