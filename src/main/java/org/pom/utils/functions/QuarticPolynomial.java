package org.pom.utils.functions;

import java.util.List;
import java.util.Map;

public class QuarticPolynomial implements Func {

    private final List<Double> a;
    public QuarticPolynomial(Map<String, Double> parameters) {
        a = getA(parameters);
    }

    @Override
    public double valueOf(double x) {
        var sum = a.get(0);
        var xN = x;
        for (int i=1; i<a.size(); i++) {
            sum += a.get(i) * xN;
            xN *= x;
        }
        return sum;
    }
}
