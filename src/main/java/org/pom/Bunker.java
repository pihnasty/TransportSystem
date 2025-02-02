package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.pom.utils.MathUtil;
import org.pom.utils.MessagesUtil;
import org.pom.utils.ParametersValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The {@code Bunker} class models a storage system that tracks input and output flows
 * and calculates various parameters such as capacity and deviations from limits.
 */
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bunker {
    private final TreeMap<Double, Map<String, Double>> tauToBunkerParametersMap;
    private final double minCapacity;
    private final double maxCapacity;
    private final double capacity;
    private final double bunkerOverMaxCapacity;
    private final double densityOverMaxCapacity;
    private final double minAvailableCapacity;
    private final double maxAvailableCapacity;
    private final double maxAvailableOutput;

    /**
     * Constructs a {@code Bunker} with specified parameters.
     *
     * @param capacity               The initial capacity of the bunker.
     * @param bunkerOverMaxCapacity  The initial capacity of the bunker overflow.
     * @param densityOverMaxCapacity The initial capacity of the max density overflow.
     * @param maxCapacity            The maximum capacity of the bunker.
     * @param minAvailableCapacity   The minimum available capacity for safe operations.
     * @param maxAvailableCapacity   The maximum available capacity for safe operations.
     * @param maxAvailableOutput     The max available output from the bunker.
     */
    @JsonCreator
    public Bunker(@JsonProperty("initialCapacity") double capacity,
                  @JsonProperty("bunkerOverMaxCapacity") double bunkerOverMaxCapacity,
                  @JsonProperty("densityOverMaxCapacity") double densityOverMaxCapacity,
                  @JsonProperty("maxCapacity") double maxCapacity,
                  @JsonProperty("minAvailableCapacity") double minAvailableCapacity,
                  @JsonProperty("maxAvailableCapacity") double maxAvailableCapacity,
                  @JsonProperty("maxAvailableOutput") double maxAvailableOutput){
        this.maxCapacity = maxCapacity;
        this.capacity = capacity;
        this.bunkerOverMaxCapacity = bunkerOverMaxCapacity;
        this.densityOverMaxCapacity = densityOverMaxCapacity;
        this.minCapacity = 0.0;
        this.minAvailableCapacity = minAvailableCapacity;
        this.maxAvailableCapacity = maxAvailableCapacity;
        this.maxAvailableOutput = maxAvailableOutput;
        this.tauToBunkerParametersMap =new TreeMap<>();

        log.info("Bunker object created with initial capacity: {}, max capacity: {}, min available capacity: {}, " +
                        "max available capacity: {} max available output from the bunker: {}",
                capacity, maxCapacity, minAvailableCapacity, maxAvailableCapacity, maxAvailableOutput);
    }

    /**
     * Adds parameter values for a specific time point (tau).
     *
     * @param tau    The time at which the parameters are being recorded.
     * @param input  The input flow from bunker at time {@code tau}.
     * @param output The output flow from bunker at time {@code tau}.
     * @throws IllegalArgumentException If {@code tau}, {@code input}, or {@code output} is negative.
     */
    public void addParametersValues(double tau, double input, double output, double outputForMaxDensity) {
        ParametersValidator.validateNonNegativeKeyValue(
                "Bunker", Map.of("tau", tau, "input", input,
                        "output", output, "outputForMaxDensity", outputForMaxDensity)
        );
        if(tauToBunkerParametersMap.isEmpty()) {
            this.tauToBunkerParametersMap.put(tau, calculateInitialParameters(input, output));
        } else {
            this.tauToBunkerParametersMap.put(tau, getBunkerParameters(tau, input, output, outputForMaxDensity));
        }
        log.debug(MessagesUtil.addParametersMessage(
                "Bunker parameters added => ", tauToBunkerParametersMap.get(tau)));
    }

    /**
     * Retrieves the input flow rate at a specific time.
     *
     * @param tau The time for which to retrieve the input flow rate.
     * @return The input flow rate at time {@code tau}.
     */
    public double getInputFlowAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_INPUT_FLOW, tau);
    }

    /**
     * Retrieves the output flow at a specific time incoming on conveyor belt without max density restriction.
     *
     * @param tau The time for which to retrieve the output flow rate.
     * @return The output real flow at time {@code tau}.
     */
    public double getOutputRealFlowFromBunker(double tau) {
        return getParameterAtTau(Constants.BUNKER_REAL_OUTPUT_FLOW, tau);
    }

    /**
     * Retrieves the output flow rate at a specific time incoming on conveyor belt using max density restriction.
     *
     * @param tau The time for which to retrieve the output flow rate.
     * @return The output flow at time {@code tau} using max density restriction.
     */
    public double getOutputFlowFromBunkerToConveyorBelt(double tau) {
        return getParameterAtTau(Constants.CONVEYOR_BELT_BUNKER_OUTPUT_FLOW, tau);
    }

    /**
     * Retrieves the bunker capacity at a specific time.
     *
     * @param tau The time for which to retrieve the capacity.
     * @return The capacity at time {@code tau}.
     */
    public double getCapacityAtTau(double tau) {
        log.debug("Retrieving capacity for tau: {}", tau);
        return getParameterAtTau(Constants.BUNKER_CAPACITY, tau);
    }

    /**
     * Retrieves the deviation above the maximum capacity at a specific time.
     *
     * @param tau The time for which to retrieve the deviation.
     * @return The over-capacity deviation at time {@code tau}.
     */
    public double getOverMaxCapacityAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_OVER_MAX_CAPACITY, tau);
    }

    /**
     * Retrieves the deviation below the minimum capacity at a specific time.
     *
     * @param tau The time for which to retrieve the deviation.
     * @return The under-capacity deviation at time {@code tau}.
     */
    public double getDensityOverMaxCapacityAtTau(double tau) {
        return getParameterAtTau(Constants.DENSITY_OVER_MAX_CAPACITY, tau);
    }

    /**
     * Calculates the initial parameters for the bunker at the start of operations.
     */
    private Map<String, Double> calculateInitialParameters(double input, double planedOutput) {
        return getBunkerParameters(
                input,
                planedOutput, 0.0, 0.0,
                calculateLimitedCapacity(capacity),
                calculateOverMaxCapacity(bunkerOverMaxCapacity, maxCapacity, capacity),
                densityOverMaxCapacity
        );
    }

    /**
     * Checked planed output from the bunker. If planed output more than max available output,
     * then limited output from the bunker.
     * @param planedOutput The original planed output from the bunker.
     * @return limited output from the bunker.
     */
    private double calculateLimitedPlanedOutput(double planedOutput) {
        return Math.min(planedOutput, maxAvailableOutput);
    }

    private Double correctedConveyorBeltBunkerOutputFlow(Map<String, Double> lastEntry, double output,double outputForMaxDensity) {
        lastEntry.put(Constants.CONVEYOR_BELT_BUNKER_OUTPUT_FLOW, Math.min(output, outputForMaxDensity));
        return lastEntry.get(Constants.CONVEYOR_BELT_BUNKER_OUTPUT_FLOW);
    }

    /**
     * Calculates bunker parameters for a specific time point based on input and output flow rates.
     */
    private Map<String, Double> getBunkerParameters(
            double tau, double input, double planedOutput, double maxDensityOutputFlow) {
        var lastEntry = this.tauToBunkerParametersMap.lastEntry().getValue();
        var deltaTau = tau - tauToBunkerParametersMap.lastEntry().getKey();

        var lastInputFlow = lastEntry.get(Constants.BUNKER_INPUT_FLOW);
        var lastCapacity = lastEntry.get(Constants.BUNKER_CAPACITY);
        var lastRealOutputFlow
                = correctedLastRealFlow(lastEntry, lastCapacity, lastInputFlow, maxDensityOutputFlow, deltaTau);
        var lastOnConveyorBeltBunkerOutputFlow
                = correctedConveyorBeltBunkerOutputFlow(lastEntry, lastRealOutputFlow, maxDensityOutputFlow);

        var predictCapacity = lastCapacity + (lastInputFlow - lastRealOutputFlow) * deltaTau;
        var capacity = calculateLimitedCapacity(predictCapacity);
        var overMaxCapacity = calculateOverMaxCapacity(
                lastEntry.get(Constants.BUNKER_OVER_MAX_CAPACITY), maxCapacity, predictCapacity
        );
        var densityOverMaxCapacity = calculateDensityOverMaxCapacity(
                lastEntry.get(Constants.DENSITY_OVER_MAX_CAPACITY),
                lastRealOutputFlow,
                lastOnConveyorBeltBunkerOutputFlow,
                deltaTau
        );

        return getBunkerParameters(input, planedOutput, lastRealOutputFlow, lastOnConveyorBeltBunkerOutputFlow,capacity,
                overMaxCapacity, densityOverMaxCapacity);

    }

    private Map<String, Double>getBunkerParameters(
            double input, double planedOutput, double lastRealFlow, double lastOnConveyorBeltBunkerOutputFlow,
            double capacity, double overMaxCapacity, double densityOverMaxCapacity
    ) {
        var bunkerParameters = new HashMap<String, Double>();
        bunkerParameters.put(Constants.BUNKER_INPUT_FLOW, input);
        bunkerParameters.put(Constants.BUNKER_PLANED_OUTPUT_FLOW, planedOutput);
        bunkerParameters.put(Constants.BUNKER_CAPACITY, capacity);
        bunkerParameters.put(Constants.BUNKER_OVER_MAX_CAPACITY, overMaxCapacity);
        bunkerParameters.put(Constants.DENSITY_OVER_MAX_CAPACITY, densityOverMaxCapacity);
        bunkerParameters.put(Constants.BUNKER_REAL_OUTPUT_FLOW, lastRealFlow);
        bunkerParameters.put(Constants.CONVEYOR_BELT_BUNKER_OUTPUT_FLOW, lastOnConveyorBeltBunkerOutputFlow);
        return bunkerParameters;
    }

    private Double correctedLastRealFlow(
            Map<String, Double> lastEntry, double lastCapacity, double lastInputFlow, double maxDensityOutputFlow, double deltaTau) {
        var lastPlanedOutputFlow = lastEntry.get(Constants.BUNKER_PLANED_OUTPUT_FLOW);
        var limitedLastPlanedOutputFlow = calculateLimitedPlanedOutput(lastPlanedOutputFlow);

        var capacity = lastCapacity + (lastInputFlow - limitedLastPlanedOutputFlow) * deltaTau;
        var lastRealOutputFlow = calculateRealOutput(limitedLastPlanedOutputFlow, capacity / deltaTau);
        lastEntry.put(Constants.BUNKER_REAL_OUTPUT_FLOW, lastRealOutputFlow);
        return lastEntry.get(Constants.BUNKER_REAL_OUTPUT_FLOW);
    }

    private double calculateLimitedCapacity(double currentCapacity) {
        return Math.min(Math.max(currentCapacity, minCapacity), maxCapacity);
    }

    /**
     * This method analysed required flow (Fr) and max available flow (Fa).
     * Fa=Fr+deltaOutput.
     * If Fa<Fr (It means, that deltaOutput<0), then real flow will be less (output + deltaOutput < output),
     * because capacity absent.
     * @param output The planed output flow from bunker at time {@code tau}.
     * @param deltaOutput The additional output flow from bunker using predict capacity.
     * @return The real output flow from bunker
     */
    private static double calculateRealOutput(double output, double deltaOutput) {
        return deltaOutput > 0 ? output : output + deltaOutput;
    }

    private double calculateOverMaxCapacity(double previousCapacity, double maxCapacity, double currentCapacity) {
        return previousCapacity + Math.max(0.0, currentCapacity - maxCapacity);
    }

    /**
     * Calculate next value of the density over max capacity value.
     * @param previousCapacity the previous value of the capacity.
     * @param realFlow the real output value regarging of the current capacity.
     *                        If delta capacity less 0.0 then density over max capacity value does not change.
     * @return current density over max capacity value.
     */
    private double calculateDensityOverMaxCapacity(
            double previousCapacity, double realFlow, double onConveyorBeltBunkerOutputFlow, double deltaTau
    ) {
        var deltaCapacity = (realFlow - onConveyorBeltBunkerOutputFlow) * deltaTau;
        return previousCapacity + Math.max(0.0, deltaCapacity);
    }

    private double getParameterAtTau(String param, double tau) {
        return MathUtil.getValueByKey(tauToBunkerParametersMap, tau).get(param);
    }
}
