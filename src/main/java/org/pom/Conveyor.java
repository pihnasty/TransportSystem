package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pom.utils.MessagesUtil;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Conveyor {
    @Setter
    private ConveyorNode conveyorNode;
    private final Bunker bunker;
    private final Speed speed;
    private final double length;
    private final TransportDelay transportDelay;
    private final OutputFlow outputFlow;
    private final Density density;
    private final InputFlow inputFlow;
    private final BunkerOutputFlow bunkerOutputFlow;
    @Setter
    private InitialDensity initialDensity;
    private final int id;

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
        this.transportDelay = new TransportDelay(length);
        this.inputFlow = Objects.isNull(inputFlow) ? new InputFlow() : inputFlow;
        this.bunkerOutputFlow = bunkerOutputFlow;
        this.initialDensity = initialDensity;
        this.outputFlow = new OutputFlow(bunker, speed, initialDensity, transportDelay);
        this.density = density;
        this.conveyorNode = conveyorNode;
    }

    public void addParametersValues(double tau,double bunkerInput,double planedBunkerOutput,double speed) {
        this.bunker.addParametersValues(tau, bunkerInput, planedBunkerOutput, density.getMaxAvailableDensity() * speed);
        this.speed.addParametersValues(tau, speed);
        this.density.addParametersValues(tau, calculateDensity(tau, speed));
        this.transportDelay.addParametersValues(tau, speed);
        this.outputFlow.addOutputFlowValue(tau, this.transportDelay.getDelayForConveyorLength());
    }

    /**
     * Computes the combined input flow for the current conveyor at a specific time point.
     * This method calculates the input flow by iterating through all connected input conveyors,
     * retrieving their output flows, and applying the appropriate flow coefficients. The resulting
     * input flow value is updated in the inputFlow object for the given time point tau.
     *
     * @param tau             the time point for which the combined input flow is calculated
     * @param getConveyorById a function to retrieve a conveyor instance by its unique identifier
     * @return the updated InputFlow object containing the combined input flow values
     */
    public InputFlow getCombinedInputFlow(Double tau, Function<Integer, Conveyor> getConveyorById) {
        getConveyorNode().getInputConveyorFlowMap().keySet().forEach(
                inputConveyorNumber -> {
                    var inputConveyor = getConveyorById.apply(inputConveyorNumber);
                    double outputFlowCoefficient
                            = inputConveyor.getConveyorNode().getOutputFlowCoefficient(getId(), tau);
                    var currentInputFlowValue
                            = Objects.isNull(inputFlow.getValue(tau)) ? 0.0 : inputFlow.getValue(tau);
                    var additionalInputCurrentFlowValue
                            = inputConveyor.getOutputFlow().getOutputFlowAtTau(tau) * outputFlowCoefficient;
                    inputFlow.setValue(tau, currentInputFlowValue + additionalInputCurrentFlowValue);
                }
        );
        return inputFlow;
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
