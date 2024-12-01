package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.pom.utils.MathUtil;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialDensity implements KeysValuesProvider<Double> {
    @Getter
    private TreeMap<Double, Double> densityMap;

    @JsonCreator
    public InitialDensity(@JsonProperty("density") TreeMap<Double, Double> density) {
        this.densityMap = density;
    }

    public void setValues(TreeMap<Double, Double> densityMap) {
        this.densityMap = densityMap;
    }

    public Double getDensityAtDistance(double deltaDistance) {
        return MathUtil.getValueByKey(densityMap, deltaDistance);
    }

    @Override
    public Collection<Double> values() {
        return Collections.unmodifiableCollection(densityMap.values());
    }

    @Override
    public Collection<Double> keys() {
        return Collections.unmodifiableCollection(densityMap.keySet());
    }
}
