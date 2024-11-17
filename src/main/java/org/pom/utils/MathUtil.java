package org.pom.utils;

import java.util.TreeMap;

public class MathUtil {

    public static <T> T getValueByKey(TreeMap<Double, T> treeMap, double tau) {
        if (treeMap.size() < 2) {
            throw new IllegalArgumentException("TreeMap must contain at least two values. Size is " + treeMap.size());
        }
        if (treeMap.firstKey() > tau || treeMap.lastKey() < tau) {
            throw new IllegalArgumentException("The specified value of tau must lie between the minimum and maximum keys in the map.");
        }
        return treeMap.get(treeMap.ceilingKey(tau));
    }
}
