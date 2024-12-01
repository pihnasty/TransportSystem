package org.pom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InputFlow  implements KeysValuesProvider<Double> {
    private TreeMap<Double, Double> inputFlowMap;

    public InputFlow() {
        this.inputFlowMap = new TreeMap<>();
    }

    @Override
    public Collection<Double> values() {
        return Collections.unmodifiableCollection(inputFlowMap.values());
    }

    @Override
    public Collection<Double> keys() {
        return  Collections.unmodifiableCollection(inputFlowMap.keySet());
    }

    public void setValues(TreeMap<Double, Double> inputFlowMap) {
        this.inputFlowMap = inputFlowMap;
    }

    public int size() {
        return  inputFlowMap.values().size();
    }
}
