package org.pom.preinitialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pom.InputFlow;
import org.pom.utils.functions.Func;
import org.pom.utils.functions.FunctionFactory;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class InputFlowDataGenerator extends InputFlow {
    private final String functionName;
    private final Map<String, Double> parameters;
    private final Func func;

    @JsonCreator
    public InputFlowDataGenerator(
            @JsonProperty("function") String functionName,
            @JsonProperty("parameters") Map<String, Double> parameters
    ) {
        this.functionName = functionName;
        this.parameters = parameters;
        if(Objects.nonNull(functionName) && !functionName.isBlank()) {
            this.func = FunctionFactory.createFunction(functionName, parameters);
        } else {
            this.func = null;
        }
    }

    public void init(double deltaLTau, double researchTau) {
        if (Objects.isNull(func)) {
            return;
        }
        var map = super.getInputFlowMap();
        for (double x = 0.0; x< researchTau; x+=deltaLTau) {
            map.put(x, func.valueOf(x));
        }
    }
}
