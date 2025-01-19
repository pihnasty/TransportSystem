package org.pom.preinitialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pom.InitialDensity;
import org.pom.utils.functions.Func;
import org.pom.utils.functions.FunctionFactory;

import java.util.Map;
import java.util.TreeMap;


public class InitialDensityDataGenerator extends InitialDensity {
    private final String functionName;
    private final Map<String, Double> parameters;
    private final Func func;

    @JsonCreator
    public InitialDensityDataGenerator(
            @JsonProperty("function") String functionName,
            @JsonProperty("parameters") Map<String, Double> parameters
    ) {
        super(new TreeMap<>());
        this.functionName = functionName;
        this.parameters = parameters;
        this.func = FunctionFactory.createFunction(functionName, parameters);
    }

    public void init(double deltaLength, double length) {
        var densityMap = super.getDensityMap();
        for (double x = 0.0; x< length + deltaLength; x+=deltaLength) {
            densityMap.put(x, func.valueOf(x));
        }
    }
}
