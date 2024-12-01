package org.pom.utils.functions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionConfig {
    private final String functionName;
    private final Map<String, Double> parameters;
    private final Func func;

    @JsonCreator
    public FunctionConfig(
            @JsonProperty("function") String functionName,
            @JsonProperty("parameters") Map<String, Double> parameters
    ) {
        this.functionName = functionName;
        this.parameters = parameters;
        if (Objects.nonNull(functionName) && !functionName.isBlank()) {
            this.func = FunctionFactory.createFunction(functionName, parameters);
        } else {
            this.func = null;
        }
    }
}
