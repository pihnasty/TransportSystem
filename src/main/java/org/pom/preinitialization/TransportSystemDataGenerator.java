package org.pom.preinitialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.pom.Constants;
import org.pom.utils.MathUtil;
import org.pom.utils.io.csv.write.CsvWriterP;
import org.pom.utils.yaml.SettingsManager;


import java.io.File;
import java.io.IOException;
import java.util.*;


public class TransportSystemDataGenerator {
    private List<ConveyorDataGenerator> conveyors;
    private final String initDataPath;
    private final double researchTau;
    private final double deltaTau;
    @Getter
    private final double deltaLength;

    @JsonCreator
    public TransportSystemDataGenerator(
            @JsonProperty("conveyors") List<ConveyorDataGenerator> conveyors,
            @JsonProperty("initDataPath") String initDataPath,
            @JsonProperty("researchTau") double researchTau,
            @JsonProperty("deltaTau") double deltaTau,
            @JsonProperty("deltaLength") double deltaLength) {
        this.initDataPath = initDataPath;
        this.researchTau = researchTau;
        this.deltaTau = deltaTau;
        this.deltaLength = deltaLength;
        this.conveyors = conveyors;
    }

    private static void createTransportSystem() throws IOException {

        SettingsManager settingsManager = new SettingsManager();
        ObjectMapper objectMapper = new ObjectMapper();
        var initTransportSystemFile = settingsManager.getApp().getInitTransportSystemFile();
        TransportSystemDataGenerator transportSystem = objectMapper.readValue(new File(initTransportSystemFile), TransportSystemDataGenerator.class);

        transportSystem.conveyors.forEach(
                conveyor -> {
                    conveyor.createInitialDensity(transportSystem.getDeltaLength());
                    conveyor.createInputFlow(transportSystem.deltaTau, transportSystem.researchTau);
                    conveyor.createBunkerOutputFlow(transportSystem.deltaTau, transportSystem.researchTau);
                    conveyor.createSpeed(transportSystem.deltaTau, transportSystem.researchTau);
                    conveyor.createConveyorNodes(transportSystem.deltaTau, transportSystem.researchTau);
                }
        );

        var table = new ArrayList<List<String>>();

        var cellFormat = settingsManager.getPrepareDataTableFormat().getCellFormat();
        var locale = settingsManager.getApp().getLocale();

        var maxLengthConveyor = transportSystem.conveyors.stream()
                .max(Comparator.comparingDouble(ConveyorDataGenerator::getLength))
                .orElse(null);
        var ksiColumn = CsvWriterP.createColumn(Constants.ColumnsNames.KSI, locale, cellFormat, maxLengthConveyor.getInitialDensity().keys());
        table.add(ksiColumn);

        transportSystem.conveyors.forEach(conveyor ->
                table.add(
                        CsvWriterP.createColumn(
                                Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.INITIAL_DENSITY),
                                locale, cellFormat, conveyor.getInitialDensity().values())
                )
        );

        var maxTauConveyor = transportSystem.conveyors.stream()
                .max(Comparator.comparingDouble(conveyor ->
                        (conveyor.getInputFlow() != null) ? conveyor.getInputFlow().size() : 0
                ))
                .orElse(null);
        var tauColumn = CsvWriterP.createColumn(Constants.ColumnsNames.TAU, locale, cellFormat, maxTauConveyor.getInputFlow().keys());
        table.add(tauColumn);

        transportSystem.conveyors.forEach(conveyor -> {
                    if (Objects.nonNull(conveyor.getInputFlow())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.JsonParametersNames.INPUT_FLOW), locale, cellFormat, conveyor.getInputFlow().values())
                        );
                    }
                    if (Objects.nonNull(conveyor.getBunkerOutputFlow())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.BUNKER_OUTPUT_FLOW), locale, cellFormat, conveyor.getBunkerOutputFlow().values())
                        );
                    }
                    if (Objects.nonNull(conveyor.getSpeed())) {
                        table.add(
                                CsvWriterP.createColumn(Constants.ColumnsNames.generateHeader(conveyor.getId(), Constants.ColumnsNames.SPEED), locale, cellFormat, conveyor.getSpeed().values())
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
                }
        );

        var transposeTable = MathUtil.transposeMatrix(table," "::repeat);
        File file = new File(transportSystem.initDataPath);
        var csvWriterP = new CsvWriterP("12.1", ';', file.getParent(), file.getName());
        csvWriterP.writeToFile(transposeTable);
        System.out.println(transportSystem);
    }

    public static void main(String[] args) throws IOException {
        createTransportSystem();
    }
}
