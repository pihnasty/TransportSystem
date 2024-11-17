package org.pom.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.TreeMap;

public class MathUtilTest {

    @Test
    public void testTreeMapSizeLessThanTwo() {
        TreeMap<Double, Double> treeMap = new TreeMap<>();
        treeMap.put(1.0, 1.0);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> MathUtil.getValueByKey(treeMap, 1.5),
                "Expected getClosestValueRecursive() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("TreeMap must contain at least two values"));
    }

    @Test
    public void testTauOutOfRangeLower() {
        TreeMap<Double, Double> treeMap = new TreeMap<>();
        treeMap.put(1.0, 1.0);
        treeMap.put(3.0, 3.0);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> MathUtil.getValueByKey(treeMap, 0.5),
                "Expected getClosestValueRecursive() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("must lie between the minimum and maximum keys"));
    }

    @Test
    public void testTauOutOfRangeUpper() {
        TreeMap<Double, Double> treeMap = new TreeMap<>();
        treeMap.put(1.0, 1.0);
        treeMap.put(3.0, 3.0);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> MathUtil.getValueByKey(treeMap, 4.0),
                "Expected getClosestValueRecursive() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("must lie between the minimum and maximum keys"));
    }

    @Test
    public void testTauWithinRange() {
        TreeMap<Double, Double> treeMap = new TreeMap<>();
        treeMap.put(1.0, 1.0);
        treeMap.put(2.0, 2.0);
        treeMap.put(3.0, 4.0);

        // Test a tau value that is within range
        Double result = MathUtil.getValueByKey(treeMap, 2.5);
        assertEquals(4.0, result, "Expected closest key to 2.5 to be 3.0");
    }
}
