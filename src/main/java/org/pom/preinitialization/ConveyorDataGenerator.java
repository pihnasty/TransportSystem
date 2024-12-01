package org.pom.preinitialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pom.*;

import java.util.Map;
import java.util.Objects;


public class ConveyorDataGenerator extends Conveyor {
    @JsonCreator
    public ConveyorDataGenerator(
            @JsonProperty("id") int id,
            @JsonProperty("bunker") Bunker bunker,
            @JsonProperty("density") Density density,
            @JsonProperty("speed") SpeedDataGenerator speed,
            @JsonProperty("inputFlow") InputFlowDataGenerator inputFlow,
            @JsonProperty("bunkerOutputFlow") BunkerOutputFlowDataGenerator bunkerOutputFlow,
            @JsonProperty("initialDensity") InitialDensityDataGenerator initialDensity,
            @JsonProperty(Constants.JsonParametersNames.CONVEYOR_NODE) ConveyorNodeDataGenerator conveyorNodeDataGenerator,
            @JsonProperty("length") double length) {
        super(id, bunker, density, speed, inputFlow, bunkerOutputFlow, initialDensity, conveyorNodeDataGenerator, length);
    }

    public void createInitialDensity(double deltaLength) {
        ((InitialDensityDataGenerator)this.getInitialDensity()).init(deltaLength, this.getLength());
    }

    public void createInputFlow(double deltaTau, double researchTau) {
        if(Objects.isNull(this.getInputFlow())) {
            return;
        }
        ((InputFlowDataGenerator)this.getInputFlow()).init(deltaTau, researchTau);
    }

    public void createBunkerOutputFlow(double deltaTau, double researchTau) {
        if(Objects.isNull(this.getBunkerOutputFlow())) {
            return;
        }
        ((BunkerOutputFlowDataGenerator)this.getBunkerOutputFlow()).init(deltaTau, researchTau);
    }

    public void createSpeed(double deltaTau, double researchTau) {
        if(Objects.isNull(this.getSpeed())) {
            return;
        }
        ((SpeedDataGenerator)this.getSpeed()).init(deltaTau, researchTau);
    }

    public void createConveyorNodes(double deltaTau, double researchTau) {
        if(Objects.isNull(this.getConveyorNode())) {
            return;
        }
        ((ConveyorNodeDataGenerator)this.getConveyorNode()).init(deltaTau, researchTau);
    }
}
