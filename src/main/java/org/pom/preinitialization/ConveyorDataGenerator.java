package org.pom.preinitialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pom.*;

import java.util.Objects;


public class ConveyorDataGenerator extends Conveyor {
    InputFlowDataGenerator inputFlow;
    @JsonCreator
    public ConveyorDataGenerator(
            @JsonProperty(Constants.JsonParametersNames.ID) int id,
            @JsonProperty(Constants.JsonParametersNames.REVERSIBLE) boolean reversible,
            @JsonProperty("bunker") Bunker bunker,
            @JsonProperty("density") Density density,
            @JsonProperty("speed") SpeedDataGenerator speed,
            @JsonProperty("inputFlow") InputFlowDataGenerator inputFlow,
            @JsonProperty("bunkerOutputFlow") BunkerOutputFlowDataGenerator bunkerOutputFlow,
            @JsonProperty("initialDensity") InitialDensityDataGenerator initialDensity,
            @JsonProperty(Constants.JsonParametersNames.CONVEYOR_NODE) ConveyorNodeDataGenerator conveyorNodeDataGenerator,
            @JsonProperty("length") double length) {
        super(id, reversible, bunker, density, speed,
                Objects.isNull(inputFlow) ? new InputFlowDataGenerator() : inputFlow,
                bunkerOutputFlow, initialDensity, conveyorNodeDataGenerator, length);
        this.inputFlow = inputFlow;
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
