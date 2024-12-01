package org.pom.utils.functions;

import java.util.List;
import java.util.Map;

public class Sin implements Func {

    private final List<Double> a;
    public Sin(Map<String, Double> parameters) {
        a = getA(parameters);
    }

    @Override
    public double valueOf(double x) {
        return a.get(0) + a.get(1) * Math.sin(2*Math.PI * a.get(2) * x + 2 * Math.PI * a.get(3));
    }
}
