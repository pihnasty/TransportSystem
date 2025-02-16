package org.pom.utils;

import java.util.List;
import java.util.TreeMap;

public class ConveyorUtil {

    public static void fillEmptyParametersByCurrentTau(double currentTau, double previousFinishTime, List<Double> taus, TreeMap<Double, Double> treeMap, Double defaultValue) {
        taus.stream().filter(tau -> previousFinishTime <= tau && tau < currentTau).forEach(
                tau -> treeMap.put(tau, defaultValue)
        );
    }
}
