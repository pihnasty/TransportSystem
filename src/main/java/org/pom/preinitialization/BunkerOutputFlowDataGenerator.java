package org.pom.preinitialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pom.BunkerOutputFlow;
import org.pom.InputFlow;
import org.pom.utils.functions.Func;
import org.pom.utils.functions.FunctionFactory;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class BunkerOutputFlowDataGenerator extends BunkerOutputFlow {
    private final String functionName;
    private final Map<String, Double> parameters;
    private final Func func;

    @JsonCreator
    public BunkerOutputFlowDataGenerator(
            @JsonProperty("function") String functionName,
            @JsonProperty("parameters") Map<String, Double> parameters
    ) {
        super(new TreeMap<>());
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
        var map = super.getOutputFlowMap();
        for (double tau = 0.0; tau< researchTau; tau+=deltaLTau) {
            map.put(tau, func.valueOf(tau));
        }
    }
}
