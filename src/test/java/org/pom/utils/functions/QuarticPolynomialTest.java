package org.pom.utils.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuarticPolynomialTest {

    @Test
    public void testValueOf() {
        // Define coefficients a0, a1, a2, a3, a4
        Map<String, Double> parameters = Map.of(
                "a0", 1.0, // Constant term
                "a1", 2.0,     // Coefficient for x
                "a2", 3.0,     // Coefficient for x^2
                "a3", 4.0,     // Coefficient for x^3
                "a4", 5.0          // Coefficient for x^4
        );

        QuarticPolynomial polynomial = new QuarticPolynomial(parameters);

        assertEquals(1.0, polynomial.valueOf(0.0), 1e-9);    // When x = 0
        assertEquals(15.0, polynomial.valueOf(1.0), 1e-9);   // When x = 1
        assertEquals(129.0, polynomial.valueOf(2.0), 1e-9);  // When x = 1
    }

    @Test
    public void testValueOfNegativeX() {
        // Define coefficients a0, a1, a2, a3, a4
        Map<String, Double> parameters = Map.of(
                "a0", 1.0, // Constant term
                "a1", -2.0,    // Coefficient for x
                "a2", 3.0,     // Coefficient for x^2
                "a3", -4.0,    // Coefficient for x^3
                "a4", 5.0          // Coefficient for x^4
        );

        QuarticPolynomial polynomial = new QuarticPolynomial(parameters);
        assertEquals(15.0, polynomial.valueOf(-1.0), 1e-9);   // When x = -1
    }

    @Test
    public void testValueOfAllZeroCoefficients() {
        Map<String, Double> parameters = Map.of(
                "a0", 0.0,
                "a1", 0.0,
                "a2", 0.0,
                "a3", 0.0,
                "a4", 0.0
        );

        QuarticPolynomial polynomial = new QuarticPolynomial(parameters);

        assertEquals(0.0, polynomial.valueOf(0.0), 1e-9);
        assertEquals(0.0, polynomial.valueOf(1.0), 1e-9);
        assertEquals(0.0, polynomial.valueOf(-1.0), 1e-9);
    }
}
