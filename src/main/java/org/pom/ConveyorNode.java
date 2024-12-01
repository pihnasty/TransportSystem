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
    private final List<Conveyor> inputConveyors;
    private final List<Conveyor> outputConveyors;
    private final Map<Integer, Map<Double, Double>> inputConveyorFlowMap;
    private final Map<Integer, Map<Double, Double>> outputConveyorFlowMap;
    private final Map<Integer, FunctionConfig> outputFunctionConfigMap;

    @JsonCreator
    public ConveyorNode(
            Map<Integer, FunctionConfig> outputFunctionConfigMap
    ) {
        inputConveyors = new ArrayList<>();
        outputConveyors = new ArrayList<>();
        inputConveyorFlowMap = new TreeMap<>();
        outputConveyorFlowMap = new TreeMap<>();
        this.outputFunctionConfigMap = outputFunctionConfigMap;
    }

    public Collection<Double> values(int key) {
        return Collections.unmodifiableCollection(outputConveyorFlowMap.get(key).values());
    }

    public Collection<Double> keys(int key) {
        return Collections.unmodifiableCollection(outputConveyorFlowMap.get(key).keySet());
    }
}
