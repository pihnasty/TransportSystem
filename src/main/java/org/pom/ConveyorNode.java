package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.pom.utils.functions.FunctionConfig;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class ConveyorNode {
    @Setter
    private int id;
    private final Map<Integer, Map<Double, Double>> inputConveyorFlowMap;
    private final Map<Integer, Map<Double, Double>> outputConveyorFlowMap;
    private final Map<Integer, FunctionConfig> outputFunctionConfigMap;

    @JsonCreator
    public ConveyorNode(
            Map<Integer, FunctionConfig> outputFunctionConfigMap
    ) {
        inputConveyorFlowMap = new TreeMap<>();
        outputConveyorFlowMap = new TreeMap<>();
        this.outputFunctionConfigMap = outputFunctionConfigMap;
    }

    /**
     * Retrieves the output flow coefficient for a specific conveyor and time point.
     * The output flow coefficient determines the proportion of the output flow
     * directed to the conveyor identified by the given id. If the coefficient for the specified id and tau
     * is not explicitly defined, a default value of 1.0 is returned.
     *
     * @param id  the unique identifier of the conveyor to retrieve the coefficient for
     * @param tau the time point for which the coefficient is required
     * @return the output flow coefficient.
     */
    public double getOutputFlowCoefficient(int id, double tau) {
        return getOutputConveyorFlowMap().getOrDefault(id, Map.of()).getOrDefault(tau, 1.0);
    }

    public Collection<Double> values(int key) {
        return Collections.unmodifiableCollection(outputConveyorFlowMap.get(key).values());
    }

    public Collection<Double> keys(int key) {
        return Collections.unmodifiableCollection(outputConveyorFlowMap.get(key).keySet());
    }
}
