package org.pom.utils;

import lombok.extern.slf4j.Slf4j;
import org.pom.Constants;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ParametersValidator {
    public static void validateNonNegativeKeyValue(String description, Map<String, Double> params) {
       var pars = params.values().stream().filter(value -> value < 0.0).collect(Collectors.toSet());
        if(!pars.isEmpty()) {
            String message = params.keySet().stream().map(
                    key ->  String.format(Constants.KEY_VALUE_MESSAGE, key, params.get(key))
            ).collect(Collectors.joining(
                    "; ",
                    String.format(Constants.NON_NEGATIVE_COMMON_MESSAGE, description),
                    "."));
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateMaxValue(String description, double value, double maxValue, Map<String, Double> params) {
        if(value > maxValue) {
            String message = params.keySet().stream().map(
                    key ->  String.format(Constants.KEY_VALUE_MESSAGE, key, params.get(key))
            ).collect(Collectors.joining(
                    "; ",
                    String.format(Constants.MAX_VALUE_COMMON_MESSAGE, description, maxValue),
                    "."));
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}
