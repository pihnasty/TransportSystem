package org.pom.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;

public class MathUtil {

    public static <T> T getValueByKey(TreeMap<Double, T> treeMap, double key) {
        if (treeMap.containsKey(key)) {
            return treeMap.get(key);
        }

        if (treeMap.firstKey() > key || treeMap.lastKey() < key) {
            throw new IllegalArgumentException(
                    "The specified key value " + key
                            + " must lie between the minimum " + treeMap.firstKey()
                            + "  and maximum "+ treeMap.lastKey() + " keys in the map."
            );
        }

        return treeMap.get(treeMap.ceilingKey(key));
    }

    public static <T> List<List<T>> transposeMatrix(List<List<T>> matrix, Function<Integer, T> defaultValue) {
        if (matrix.isEmpty()) return new ArrayList<>();

        int maxCols = matrix.stream().mapToInt(List::size).max().orElse(0);

        List<List<T>> transposed = new ArrayList<>();
        for (int i = 0; i < maxCols; i++) {
            transposed.add(new ArrayList<>());
        }

        for (List<T> row : matrix) {
            for (int col = 0; col < maxCols; col++) {
                transposed.get(col).add(col < row.size() ? row.get(col) : defaultValue.apply(((String)row.get(0)).length()));
            }
        }
        return transposed;
    }
}
