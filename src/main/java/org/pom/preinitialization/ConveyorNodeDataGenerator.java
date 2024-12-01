package org.pom.preinitialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.pom.Constants;
import org.pom.ConveyorNode;
import org.pom.utils.functions.Func;
import org.pom.utils.functions.FunctionConfig;
import org.pom.utils.functions.FunctionFactory;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;


public class ConveyorNodeDataGenerator extends ConveyorNode {
    //private final Map<Integer, FunctionConfig> functionConfigs;

    @JsonCreator
    public ConveyorNodeDataGenerator(Map<Integer, FunctionConfig> functionConfigs) {
        super(functionConfigs);
        // this.functionConfigs = functionConfigs;
    }

    public void init(double deltaLTau, double researchTau) {
        var functionConfigs = getOutputFunctionConfigMap();
        var keys = functionConfigs.keySet();
        if (keys.size() < 2) {
            return;
        }
        var tempOutputConveyorFlowMap = new TreeMap<Integer, Map<Double, Double>>();
        var sumOutputConveyorFlowMap = new TreeMap<Double, Double>();
        keys.forEach(key -> {
                    var functionName = functionConfigs.get(key).getFunctionName();
                    var parameters = functionConfigs.get(key).getParameters();
                    Func func;
                    if (Objects.nonNull(functionName) && !functionName.isBlank()) {
                        func = FunctionFactory.createFunction(functionName, parameters);
                    } else {
                        return;
                    }
                    var map = new TreeMap<Double, Double>();
                    for (double x = 0.0; x < researchTau; x += deltaLTau) {
                        map.put(x, func.valueOf(x));
                        if(sumOutputConveyorFlowMap.containsKey(x)) {
                            sumOutputConveyorFlowMap.put(x,sumOutputConveyorFlowMap.get(x)+func.valueOf(x));
                        } else {
                            sumOutputConveyorFlowMap.put(x, func.valueOf(x));
                        }
                    }
                    tempOutputConveyorFlowMap.put(key, map);

                }
        );

        keys.forEach(key -> {
            var map = tempOutputConveyorFlowMap.get(key);
            var normalizeMap = new TreeMap<Double, Double>();

            map.keySet().forEach(doubleKey -> {
                        if(sumOutputConveyorFlowMap.get(doubleKey)>0.0 && map.get(doubleKey) >= 0.0) {
                            normalizeMap.put(doubleKey, map.get(doubleKey) / sumOutputConveyorFlowMap.get(doubleKey));
                        } else {
                            throw new IllegalArgumentException(String.format(Constants.NON_NEGATIVE_COMMON_MESSAGE, "ConveyorNode"));
                        }
                    }
            );
            getOutputConveyorFlowMap().put(key, normalizeMap);
        });
    }
}

