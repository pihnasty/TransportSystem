package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pom.utils.MathUtil;
import org.pom.utils.io.csv.write.CsvWriterP;
import org.pom.utils.json.ObjectMapperFactory;
import org.pom.utils.yaml.SettingsManager;

import java.io.File;
import java.util.*;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportSystem {

    @Getter
    private List<Conveyor> conveyors= new ArrayList<>();
    /**
     * A list of output conveyors of the transport system.
     * Each conveyor in the list represents a pathway for processed materials or items to exit the system.
     */
    private final List<Conveyor> outputConveyors = new ArrayList<>();
    private final List<Conveyor> inputConveyors = new ArrayList<>();
    @Getter
    private final List<Double> taus = new ArrayList<>();
    @Getter
    private String initDataPath;
    @Getter
    private String outputDataPath;
    private final double researchTau;
    @Getter
    private final double deltaTau;
    @Getter
    private final double deltaLength;

    // Map to store each conveyor and its children (downstream conveyors)
    private final Map<Conveyor, List<Conveyor>> conveyorTree;

    private final String initTransportSystemFile;
    private final String  cellFormat;
    private final Locale locale;

    @JsonCreator
    public TransportSystem(
            @JsonProperty(Constants.JsonParametersNames.CONVEYORS) List<Conveyor> conveyors,
            @JsonProperty(Constants.JsonParametersNames.INIT_DATA_PATH) String initDataPath,
            @JsonProperty(Constants.JsonParametersNames.OUTPUT_DATA_PATH) String outputDataPath) {
        this(conveyors, initDataPath, outputDataPath, 0.0, 0.0, 0.0, new HashMap<>(), "", "", Locale.getDefault());
    }

    public TransportSystem(
            String initTransportSystemFile,
            String cellFormat,
            Locale locale,
            Double researchTau,
            Double deltaTau,
            Double deltaLength
    ) {
        this(null, "", "", researchTau, deltaTau, deltaLength, null, initTransportSystemFile, cellFormat, locale);
    }

    /**
     * This method allows dynamically adding a conveyor to the existing list of output conveyors.
     * The conveyor added represents an additional pathway for processed materials or items to exit the system.
     */
    public void addOutputConveyors() {
        conveyors.stream().filter(
                conveyor -> conveyor.getConveyorNode().getOutputConveyorFlowMap().isEmpty()
        ).forEach(outputConveyors::add);
    }

    /**
     * This method allows dynamically adding a conveyor to the existing list of output conveyors.
     * The conveyor added represents an additional pathway for processed materials or items to exit the system.
     */
    public void addInputConveyors() {
        conveyors.stream().filter(
                conveyor -> conveyor.getConveyorNode().getInputConveyorFlowMap().isEmpty()
        ).forEach(inputConveyors::add);
    }

    public void createTransportSystem() {
        ObjectMapper objectMapper = ObjectMapperFactory.createTransportSystemMapper();
        TransportSystem transportSystem = null;
        try {
            transportSystem = objectMapper.readValue(new File(initTransportSystemFile), TransportSystem.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conveyors = transportSystem.getConveyors();
        initDataPath = transportSystem.getInitDataPath();
        outputDataPath = transportSystem.getOutputDataPath();

        addInputConveyors();
        addOutputConveyors();
        this.taus.addAll(transportSystem.getTaus());
    }

    public void processingTransportSystem(double startTime, double finishTime) {
        taus.stream().filter(t -> t >= startTime && t < finishTime).
                forEach(tau -> conveyors.forEach(conveyor -> calculatedConveyorParameters(tau, conveyor)));

        taus.stream().filter(t -> t == startTime && startTime == finishTime).
                forEach(tau -> conveyors.forEach(conveyor -> calculatedConveyorParameters(tau, conveyor)));

        SettingsManager settingsManager = new SettingsManager();
        var cellFormat = settingsManager.getPrepareDataTableFormat().getCellFormat();
        var locale = settingsManager.getApp().getLocale();
        var table = new ArrayList<List<String>>();
        var tauColumn = CsvWriterP.createColumn(Constants.ColumnsNames.TAU, locale, cellFormat, taus);
        table.add(tauColumn);
        conveyors.forEach(conveyor -> {
                    if (Objects.nonNull(conveyor.getInputFlow())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.INPUT_FLOW), locale, cellFormat, conveyor.getInputFlow().values())
                        );
                    }
                    if (Objects.nonNull(conveyor.getBunker())) {
                        List.of(
                                Constants.ColumnsNames.BUNKER_CAPACITY,
                                Constants.ColumnsNames.BUNKER_OVER_MAX_CAPACITY,
                                Constants.ColumnsNames.DENSITY_OVER_MAX_CAPACITY,
                                Constants.ColumnsNames.BUNKER_INPUT_FLOW,
                                Constants.ColumnsNames.BUNKER_PLANED_OUTPUT_FLOW,
                                Constants.ColumnsNames.BUNKER_REAL_OUTPUT_FLOW,
                                Constants.ColumnsNames.CONVEYOR_BELT_BUNKER_OUTPUT_FLOW
                        ).forEach(key ->
                                table.add(
                                        CsvWriterP.createColumn(
                                                Constants.ColumnsNames.generateHeader(conveyor.getId(), key),
                                                locale,
                                                cellFormat,
                                                conveyor.getBunker().getValues(key)
                                        )
                                )
                        );
                    }
                    if (Objects.nonNull(conveyor.getSpeed())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.SPEED), locale, cellFormat, conveyor.getSpeed().values())
                        );
                    }
                    if (Objects.nonNull(conveyor.getDensity())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.DENSITY), locale, cellFormat, conveyor.getDensity().values())
                        );
                    }
                    if (Objects.nonNull(conveyor.getOutputFlow())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.OUTPUT_FLOW), locale, cellFormat, conveyor.getOutputFlow().values())
                        );
                    }
                    if (Objects.nonNull(conveyor.getBunkerOutputFlow())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.BUNKER_OUTPUT_FLOW), locale, cellFormat, conveyor.getBunkerOutputFlow().values())
                        );
                    }
                    if (Objects.nonNull(conveyor.getConveyorNode())) {
                        var keys = conveyor.getConveyorNode().getOutputConveyorFlowMap().keySet();
                        keys.forEach(key ->
                                table.add(
                                        CsvWriterP.createColumn(
                                                Constants.ColumnsNames.generateHeader(
                                                        conveyor.getId(), Constants.JsonParametersNames.CONVEYOR_NODE, key
                                                ),
                                                locale,
                                                cellFormat,
                                                conveyor.getConveyorNode().values(key))
                                )
                        );
                    }
                    if (Objects.nonNull(conveyor.getTransportDelay())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.DELAY_FOR_CONVEYOR_LENGTH), locale, cellFormat, conveyor.getTransportDelay().values())
                        );
            }
                }
        );

        var transposeTable = MathUtil.transposeMatrix(table," "::repeat);
        File file = new File(outputDataPath);
        var csvWriterP = new CsvWriterP("12.1", ';', file.getParent(), file.getName());
        csvWriterP.writeToFile(transposeTable);
    }

    private void calculatedConveyorParameters(Double tau, Conveyor conveyor) {
        var inputConveyorNumbers
                = Collections.unmodifiableCollection(conveyor.getConveyorNode().getInputConveyorFlowMap().keySet());
        inputConveyorNumbers.forEach(
                inputConveyorNumber -> {
                    var inputConveyor = getConveyorById(inputConveyorNumber);
                    if (inputConveyor.getOutputFlow().keys().isEmpty() || inputConveyor.getOutputFlow().lastKey()< tau) {
                        calculatedConveyorParameters(tau, inputConveyor);
                    }
                }
        );
        if(!conveyor.getOutputFlow().getTauToFlowOutputMap().containsKey(tau)) {
            calculatedParameters(tau, conveyor);
        }
    }

    private Conveyor getConveyorById(Integer id) {
        return conveyors.stream().filter(conveyor -> id == conveyor.getId()).findFirst().orElse(null);
    }

    private void calculatedParameters(Double tau, Conveyor conveyor) {
        var inputFlow = conveyor.getCombinedInputFlow(tau, this::getConveyorById);
        var speed = conveyor.getSpeed().getSpeedAtTau(tau);
        var plannedBunkerOutput = Objects.isNull(conveyor.getBunkerOutputFlow())
                ? conveyor.getBunker().getMaxAvailableOutput() : conveyor.getBunkerOutputFlow().getValueAtTau(tau);
        conveyor.addParametersValues(tau, inputFlow.getValue(tau), plannedBunkerOutput, speed);
    }

    public void addTaus(List<Double> taus) {
        this.taus.addAll(taus);
    }

}

