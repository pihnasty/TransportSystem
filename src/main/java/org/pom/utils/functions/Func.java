package org.pom.utils.functions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Func {
    double valueOf(double x);

    default
    List<Double> getA(Map<String, Double> parameters) {
        return Stream.of("a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9")
                .map(key -> parameters.getOrDefault(key, 0.0)).collect(Collectors.toList());
    }
}
