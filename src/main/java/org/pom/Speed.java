package org.pom;

import org.pom.utils.MathUtil;

import java.util.Map;
import java.util.TreeMap;

public class Speed {

    private final TreeMap<Double, Double> tauToSpeedMap;

    public Speed() {
        this.tauToSpeedMap = new TreeMap<>();
    }

    public void addSpeedValue(double tau, double speed) {
        this.tauToSpeedMap.put(tau, speed);
    }

    public Double getSpeedAtTau(double tau) {
        return MathUtil.getValueByKey(tauToSpeedMap, tau);
    }

    public void clear() {
        tauToSpeedMap.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Double, Double> entry : tauToSpeedMap.entrySet()) {
            sb.append("Tau: ").append(entry.getKey()).append(", Speed: ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
