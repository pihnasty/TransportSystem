package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pom.utils.MessagesUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Conveyor {
    @Setter
    private ConveyorNode conveyorNode;
    private Bunker bunker;
    private Speed speed;
    private final double length;
    private TransportDelay transportDelay;
    private OutputFlow outputFlow;
    private Density density;
    private InputFlow inputFlow;
    private BunkerOutputFlow bunkerOutputFlow;
    @Setter
    private InitialDensity initialDensity;
    private final int id;
    private final int reversible;
    @Setter
    private boolean isStarted = false;

    @JsonCreator
    public Conveyor(
            @JsonProperty(Constants.JsonParametersNames.ID) int id,
            @JsonProperty(Constants.JsonParametersNames.REVERSIBLE) int reversible,
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
        this.reversible = reversible;
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

    public void copyReversibleMainParameters(Conveyor conveyor, Conveyor reversibleConveyor,
                                             double currentTau,
                                             double previousFinishTime,
                                             double deltaTau,
                                             List<Double> taus) {
        copyBaseParameters(conveyor, currentTau, previousFinishTime, taus);
        conveyor.density.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus);
        this.density = conveyor.density;

        if(!reversibleConveyor.getDensity().keys().isEmpty()) {
            var reversibleInitialDensityMap = getDensityByLength(reversibleConveyor, currentTau -deltaTau);
            TreeMap<Double, Double> initialDensityMap = getReversibleInitialDensityMap(reversibleInitialDensityMap);
            this.initialDensity = new InitialDensity(initialDensityMap);
            this.transportDelay = new TransportDelay(this.length);
            this.outputFlow = new OutputFlow(bunker, speed, initialDensity, transportDelay);
        }
    }

    private TreeMap<Double, Double> getReversibleInitialDensityMap(TreeMap<Double, Double> densityMap) {
        var reversibleDensityMap = new TreeMap<Double, Double>();
        densityMap.keySet().forEach(
                key ->reversibleDensityMap.put(length - key, densityMap.get(key))
        );
        return reversibleDensityMap;
    }

    private TreeMap<Double, Double> getDensityByLength(Conveyor conveyor, double tau) {
        var densityMap = new TreeMap<Double, Double>();
        conveyor.getInitialDensity().getDensityMap().keySet().forEach(
                key -> {
                    var delay = conveyor.getTransportDelay().getDelayByDeltaDistance(key);
                    var reversibleConveyorDensity = conveyor.getDensity();
                    var currentConveyorDensityValue = reversibleConveyorDensity.getDensity(tau - delay);
                    densityMap.put(key, currentConveyorDensityValue);
                }
        );
        return densityMap;
    }

    public void copyMainParameters(Conveyor conveyor, double currentTau, double previousFinishTime, List<Double> taus) {
        copyBaseParameters(conveyor, currentTau, previousFinishTime, taus);

        conveyor.density.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus);
        this.density = conveyor.density;

        conveyor.transportDelay.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus);
        this.transportDelay = conveyor.transportDelay;
    }

    private void copyBaseParameters(Conveyor conveyor, double currentTau, double previousFinishTime, List<Double> taus) {
        conveyor.bunker.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus,  conveyor.bunkerOutputFlow);
        this.bunker = conveyor.bunker;

        conveyor.speed.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus);
        this.speed = conveyor.speed;

        conveyor.inputFlow.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus);
        this.inputFlow = conveyor.inputFlow;

        conveyor.outputFlow.fillEmptyParametersByCurrentTau(currentTau, previousFinishTime, taus);
        this.outputFlow = conveyor.outputFlow;
    }

    public boolean isReversible() {
        return reversible > 0;
    }
}
