package org.pom;

import org.pom.utils.MathUtil;

import java.util.Map;
import java.util.TreeMap;

public class Bunker {
    private final TreeMap<Double, Map<String, Double>> tauToBunkerCharacteristicsMap =new TreeMap<>();
    private final double minCapacity = 0.0;
    private final double maxCapacity;
    private final double initialCapacity;
    private final double minAvailableCapacity;
    private final double maxAvailableCapacity;


    public Bunker(double initialCapacity, double maxCapacity, double minAvailableCapacity, double maxAvailableCapacity){
        this.maxCapacity = maxCapacity;
        this.initialCapacity = initialCapacity;
        this.minAvailableCapacity = minAvailableCapacity;
        this.maxAvailableCapacity = maxAvailableCapacity;
    }

    public void addCharacteristics(double tau, double input, double output) {
        validateInput(tau, input, output);

        if(tauToBunkerCharacteristicsMap.isEmpty()) {
            this.tauToBunkerCharacteristicsMap.put(
                    tau,
                    Map.of(
                            Constants.BUNKER_INPUT_FLOW, input,
                            Constants.BUNKER_OUTPUT_FLOW, output,
                            Constants.BUNKER_CAPACITY, initialCapacity,
                            Constants.BUNKER_OVER_MAX_CAPACITY, 0.0,
                            Constants.BUNKER_BELOW_MIN_CAPACITY, 0.0,
                            Constants.BUNKER_OVER_MAX_AVAILABLE_CAPACITY, 0.0,
                            Constants.BUNKER_BELOW_MIN_AVAILABLE_CAPACITY, 0.0
                    )
            );
        } else {
            var value = tauToBunkerCharacteristicsMap.lastEntry().getValue();
            var prevInput = value.get(Constants.BUNKER_INPUT_FLOW);
            var prevOutput = value.get( Constants.BUNKER_OUTPUT_FLOW);
            var prevCapacity = value.get(Constants.BUNKER_CAPACITY);
            var prevTau = tauToBunkerCharacteristicsMap.lastEntry().getKey();
            var calculatedCurrentCapacity = prevCapacity + (prevInput - prevOutput) * (tau - prevTau);
            var capacity = getCapacity(calculatedCurrentCapacity);
            var calculatedOutput = getCalculatedOutput(output, calculatedCurrentCapacity);
            var overMaxCapacity = getOverMaxCapacity(
                    value.get(Constants.BUNKER_OVER_MAX_CAPACITY), calculatedCurrentCapacity
            );
            var belowMaxCapacity = getBelowMaxCapacity(
                    value.get(Constants.BUNKER_BELOW_MIN_CAPACITY), calculatedCurrentCapacity
            );
            var overMaxAvailableCapacity= getOverMaxAvailableCapacity(
                    value.get(Constants.BUNKER_OVER_MAX_AVAILABLE_CAPACITY), calculatedCurrentCapacity
            );
            var belowMaxAvailableCapacity = getBelowMaxAvailableCapacity(
                    value.get(Constants.BUNKER_BELOW_MIN_AVAILABLE_CAPACITY), calculatedCurrentCapacity
            );
            this.tauToBunkerCharacteristicsMap.put(
                    tau,
                    Map.of(
                            Constants.BUNKER_INPUT_FLOW, input,
                            Constants.BUNKER_OUTPUT_FLOW, calculatedOutput,
                            Constants.BUNKER_CAPACITY, capacity,
                            Constants.BUNKER_OVER_MAX_CAPACITY, overMaxCapacity,
                            Constants.BUNKER_BELOW_MIN_CAPACITY, belowMaxCapacity,
                            Constants.BUNKER_OVER_MAX_AVAILABLE_CAPACITY, overMaxAvailableCapacity,
                            Constants.BUNKER_BELOW_MIN_AVAILABLE_CAPACITY, belowMaxAvailableCapacity
                    )
            );
        }

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

    public double getOverMaxAvailableCapacityAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_OVER_MAX_AVAILABLE_CAPACITY, tau);
    }

    public double getBelowMinAvailableCapacityAtTau(double tau) {
        return getParameterAtTau(Constants.BUNKER_BELOW_MIN_AVAILABLE_CAPACITY, tau);
    }

    private void validateInput(double tau, double input, double output) {
        if (tau < 0 || input < 0 || output < 0) {
            throw new IllegalArgumentException("Input, output, and tau must be non-negative.");
        }
    }

    private double getCapacity(double currentCapacity) {
        return currentCapacity < minCapacity ? minCapacity : Math.min(currentCapacity, maxCapacity);
    }

    private static double getCalculatedOutput(double output, double calculatedCurrentCapacity) {
        return calculatedCurrentCapacity > 0 ? output : output + calculatedCurrentCapacity;
    }

    private double getOverMaxCapacity(double prevOverMaxCapacity, double calculatedCurrentCapacity) {
        return prevOverMaxCapacity
                + (calculatedCurrentCapacity > maxCapacity ?  calculatedCurrentCapacity - maxCapacity : 0.0);
    }

    private double getBelowMaxCapacity(double prevBelowMaxCapacity, double calculatedCurrentCapacity) {
        return prevBelowMaxCapacity
                + (calculatedCurrentCapacity < minCapacity ?  calculatedCurrentCapacity - minCapacity : 0.0);
    }

    private double getOverMaxAvailableCapacity(double overMaxAvailableCapacity, double calculatedCurrentCapacity) {
        return overMaxAvailableCapacity
                + (calculatedCurrentCapacity > maxAvailableCapacity
                ?  calculatedCurrentCapacity - maxAvailableCapacity : 0.0);
    }

    private double getBelowMaxAvailableCapacity(double belowMaxAvailableCapacity, double calculatedCurrentCapacity) {
        return belowMaxAvailableCapacity
                + (calculatedCurrentCapacity < minAvailableCapacity
                ?  calculatedCurrentCapacity - minAvailableCapacity : 0.0);
    }

    private double getParameterAtTau(String param, double tau) {
        return MathUtil.getValueByKey(tauToBunkerCharacteristicsMap, tau).get(param);
    }
}
