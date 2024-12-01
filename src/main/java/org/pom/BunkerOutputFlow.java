package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.pom.utils.io.csv.read.CsvReaderP;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BunkerOutputFlow implements KeysValuesProvider<Double> {
    private TreeMap<Double, Double> outputFlowMap;

    @JsonCreator
    public BunkerOutputFlow(@JsonProperty("bunkerOutputFlow") TreeMap<Double, Double> outputFlowMap) {
        this.outputFlowMap = outputFlowMap;
    }

    public void setValues(TreeMap<Double, Double> outputFlowMap) {
        this.outputFlowMap = outputFlowMap;
    }

    @Override
    public Collection<Double> values() {
        return Collections.unmodifiableCollection(outputFlowMap.values());
    }

    @Override
    public Collection<Double> keys() {
        return  Collections.unmodifiableCollection(outputFlowMap.keySet());
    }
}
