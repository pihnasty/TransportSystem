package org.pom;

import org.pom.utils.MathUtil;

import java.util.Map;
import java.util.TreeMap;

public class Bunker {
    private final TreeMap<Double, Map<String, Double>> tauToBunkerParametersMap =new TreeMap<>();
    private final double minCapacity;
    private final double maxCapacity;
    private final double initialCapacity;
    private final double minAvailableCapacity;
    private final double maxAvailableCapacity;


    public Bunker(double initialCapacity, double maxCapacity, double minAvailableCapacity, double maxAvailableCapacity){
        this.maxCapacity = maxCapacity;
        this.initialCapacity = initialCapacity;
        this.minCapacity = 0.0;
        this.minAvailableCapacity = minAvailableCapacity;
        this.maxAvailableCapacity = maxAvailableCapacity;
    }

    public void addParametersValues(double tau, double input, double output) {
        validateInput(tau, input, output);

        if(tauToBunkerParametersMap.isEmpty()) {
            this.tauToBunkerParametersMap.put(tau, calculateInitialParameters(input, output));
        } else {
            this.tauToBunkerParametersMap.put(tau, getBunkerInputFlow(tau, input, output));
        }

    }

    private Map<String, Double> calculateInitialParameters(double input, double output) {
        return Map.of(
                Constants.BUNKER_INPUT_FLOW, input,
                Constants.BUNKER_OUTPUT_FLOW, output,
                Constants.BUNKER_CAPACITY, initialCapacity,
                Constants.BUNKER_OVER_MAX_CAPACITY, 0.0,
                Constants.BUNKER_BELOW_MIN_CAPACITY, 0.0
        );
    }

    private Map<String, Double> getBunkerInputFlow(double tau, double input, double output) {
        var lastEntry = this.tauToBunkerParametersMap.lastEntry().getValue();
        var prevInput = lastEntry.get(Constants.BUNKER_INPUT_FLOW);
        var prevOutput = lastEntry.get( Constants.BUNKER_OUTPUT_FLOW);
        var prevCapacity = lastEntry.get(Constants.BUNKER_CAPACITY);

        var deltaTau = tau - tauToBunkerParametersMap.lastEntry().getKey();
        var calculatedCurrentCapacity = prevCapacity + (prevInput - prevOutput) * deltaTau;
        var predictCalculatedCapacity = calculateCapacity(calculatedCurrentCapacity) + (input - output) * deltaTau;

        return Map.of(
                Constants.BUNKER_INPUT_FLOW, input,
                Constants.BUNKER_OUTPUT_FLOW, calculateOutput(output, predictCalculatedCapacity),
                Constants.BUNKER_CAPACITY, calculateCapacity(calculatedCurrentCapacity),
                Constants.BUNKER_OVER_MAX_CAPACITY,
                calculateDeviation(
                        lastEntry.get(Constants.BUNKER_OVER_MAX_CAPACITY),
                        maxCapacity, predictCalculatedCapacity, true
                ),
                Constants.BUNKER_BELOW_MIN_CAPACITY,
                calculateDeviation(
                        lastEntry.get(Constants.BUNKER_BELOW_MIN_CAPACITY),
                        minCapacity, predictCalculatedCapacity, false
                )
        );
    }

    public double getInputFlowAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_INPUT_FLOW, tau);
    }

    public double getOutputFlowAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_OUTPUT_FLOW, tau);
    }

    public double getCapacityAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_CAPACITY, tau);
    }

    public double getOverMaxCapacityAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_OVER_MAX_CAPACITY, tau);
    }

    public double getBelowMinCapacityAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_BELOW_MIN_CAPACITY, tau);
    }

    private void validateInput(double tau, double input, double output) {
        if (tau < 0 || input < 0 || output < 0) {
            throw new IllegalArgumentException("Input, output, and tau must be non-negative.");
        }
    }

    private double calculateCapacity(double currentCapacity) {
        return Math.min(Math.max(currentCapacity, minCapacity), maxCapacity);
    }

    private static double calculateOutput(double output, double calculatedCurrentCapacity) {
        return calculatedCurrentCapacity > 0 ? output : output + calculatedCurrentCapacity;
    }

    private double calculateDeviation(double previousDeviation, double limit, double currentCapacity,
                                      boolean isOverLimit) {
        double deviation = isOverLimit
                ? Math.max(0.0, currentCapacity - limit)
                : Math.min(0.0, currentCapacity - limit);
        return previousDeviation + deviation;
    }

    private double getParameterAtTau(String param, double tau) {
        return MathUtil.getValueByKey(tauToBunkerParametersMap, tau).get(param);
    }
}
