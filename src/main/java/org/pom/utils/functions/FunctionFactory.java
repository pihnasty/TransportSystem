package org.pom.utils.functions;

import java.util.Map;

public class FunctionFactory {

    // Creates a function evaluator based on the provided function name and parameters
    public static Func createFunction(String functionName, Map<String, Double> parameters) {
        if ( "y(x) = a0+a1*x+a2*x*x+a3*x*x*x+a4*x*x*x*x".equals(functionName)) {
            return new QuarticPolynomial(parameters);
        }
        if ("y(x) = a0+a1*sin(2*Pi*a2*x+2*Pi*a3)".equals(functionName)) {
            return new Sin(parameters);
        } else {
            throw new IllegalArgumentException("Unsupported function: " + functionName);
        }
    }
}
