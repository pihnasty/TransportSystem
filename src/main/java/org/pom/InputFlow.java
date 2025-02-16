package org.pom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.pom.utils.ConveyorUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

@Getter
@Setter
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

    public Double getValue(Double tau) {
        return this.inputFlowMap.get(tau);
    }

    public void setValue(Double tau, Double value) {
        this.inputFlowMap.put(tau, value);
    }

    public void setValues(TreeMap<Double, Double> inputFlowMap) {
        this.inputFlowMap = inputFlowMap;
    }

    public int size() {
        return  inputFlowMap.values().size();
    }

    public void fillEmptyParametersByCurrentTau(double currentTau, double previousFinishTime, List<Double> taus) {
        ConveyorUtil.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus, inputFlowMap, 0.0);
    }
}
