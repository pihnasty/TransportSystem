package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pom.preinitialization.ConveyorNodeDataGenerator;
import org.pom.utils.MessagesUtil;

import java.util.Map;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class Conveyor {
    @Getter
    @Setter
    private ConveyorNode conveyorNode;
    @Getter
    private final Bunker bunker;
    @Getter
    private final Speed speed;
    @Getter
    private final double length;
    private final TransportDelay transportDelay;
    @Getter
    private final OutputFlow outputFlow;
    private final Density density;
    @Getter
    private final InputFlow inputFlow;
    @Getter
    private BunkerOutputFlow bunkerOutputFlow;
    @Getter
    @Setter
    private InitialDensity initialDensity;
    @Getter
    private int id;

    @JsonCreator
    public Conveyor(
            @JsonProperty("id") int id,
            @JsonProperty("bunker") Bunker bunker,
            @JsonProperty("density") Density density,
            @JsonProperty("speed") Speed speed,
            @JsonProperty("inputFlow") InputFlow inputFlow,
            @JsonProperty("bunkerOutputFlow") BunkerOutputFlow bunkerOutputFlow,
            @JsonProperty("initialDensity") InitialDensity initialDensity,
            @JsonProperty(Constants.JsonParametersNames.CONVEYOR_NODE) ConveyorNode conveyorNode,
            @JsonProperty("length") double length) {
        this.id = id;
        this.bunker = bunker;
        this.speed = speed;
        this.length = length;
        this.transportDelay = new TransportDelay();
        this.inputFlow = inputFlow;
        this.bunkerOutputFlow = bunkerOutputFlow;
        this.initialDensity = initialDensity;
        this.outputFlow = new OutputFlow(bunker, speed, initialDensity, transportDelay);
        this.density = density;
        this.conveyorNode = conveyorNode;
    }

    public void addParametersValues(double tau,double bunkerInput,double planedBunkerOutput,double speed) {
        this.bunker.addParametersValues(tau, bunkerInput, planedBunkerOutput, density.getMaxAvailableDensity() * speed);
        this.speed.addParametersValues(tau, speed);
        this.transportDelay.addParametersValues(tau, speed);
        this.density.addParametersValues(tau, calculateDensity(tau, speed));
        var delayTau = this.transportDelay.getDelayByDeltaDistance(this.length);
        this.outputFlow.addOutputFlowValue(tau, delayTau);
    }

    private double calculateDensity(double tau, double speed) {
        return speed == 0.0
                ? density.getMaxAvailableDensity() : this.bunker.getOutputFlowFromBunkerToConveyorBelt(tau) / speed;
    }

    public void addOutputConveyorFlow(int key, Map<Double, Double> outputConveyorFlow) {
        this.conveyorNode.getOutputConveyorFlowMap().put(key, outputConveyorFlow);
    }

    public void addInputConveyorFlow(int key, Map<Double, Double> outputConveyorFlow) {
        this.conveyorNode.getInputConveyorFlowMap().put(key, outputConveyorFlow);
    }

    public void addId(int id) {
        if (this.id != id) {
            var message = MessagesUtil.addParametersMessage(
                    "Wrong Conveyor id => ", Map.of("expected", this.id, "actual", id));
            log.debug(message);
            throw new IllegalArgumentException(message);
        }
        this.conveyorNode.setId(id);
    }
}
