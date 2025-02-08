package org.pom.preinitialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pom.Constants;
import org.pom.utils.MathUtil;
import org.pom.utils.io.csv.write.CsvWriterP;
import org.pom.utils.yaml.SettingsManager;


import java.io.File;
import java.io.IOException;
import java.util.*;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportSystemDataGenerator {
    private List<ConveyorDataGenerator> conveyors;
    private final String initDataPath;
    private final double researchTau;
    private final double deltaTau;
    @Getter
    private final double deltaLength;

    private final String initTransportSystemFile;
    private final String  cellFormat;
    private final Locale locale;

    @JsonCreator
    public TransportSystemDataGenerator(
            @JsonProperty("conveyors") List<ConveyorDataGenerator> conveyors,
            @JsonProperty("initDataPath") String initDataPath) {
        this(conveyors, initDataPath, 0.0, 0.0, 0.0, "", "", Locale.getDefault());
    }

    public TransportSystemDataGenerator(
            String initTransportSystemFile,
            String cellFormat,
            Locale locale,
            Double researchTau,
            Double deltaTau,
            Double deltaLength
    ) {
        this(null, "", researchTau, deltaTau, deltaLength, initTransportSystemFile, cellFormat, locale);
    }


    private void createTransportSystem() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        TransportSystemDataGenerator transportSystem = objectMapper.readValue(new File(initTransportSystemFile), TransportSystemDataGenerator.class);


        transportSystem.conveyors.forEach(
                conveyor -> {
                    conveyor.createInitialDensity(deltaLength);
                    conveyor.createInputFlow(deltaTau, researchTau);
                    conveyor.createBunkerOutputFlow(deltaTau, researchTau);
                    conveyor.createSpeed(deltaTau, researchTau);
                    conveyor.createConveyorNodes(deltaTau, researchTau);
                }
        );

        var table = new ArrayList<List<String>>();

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
        var csvWriterP = new CsvWriterP(cellFormat, ';', file.getParent(), file.getName());
        csvWriterP.writeToFile(transposeTable);
        System.out.println(transportSystem);
    }

    public static void main(String[] args) throws IOException {

        SettingsManager settingsManager = new SettingsManager();
        var initTransportSystemFiles = settingsManager.getApp().getInitTransportSystemFiles();
        var cellFormat = settingsManager.getPrepareDataTableFormat().getCellFormat();
        var locale = settingsManager.getApp().getLocale();
        var researchTau = settingsManager.getApp().getResearchTau();
        var deltaTau = settingsManager.getApp().getDeltaTau();
        var deltaLength = settingsManager.getApp().getDeltaLength();

        initTransportSystemFiles.keySet().forEach(
                transportSystemFileName -> {
                    TransportSystemDataGenerator transportSystem
                            = new TransportSystemDataGenerator(transportSystemFileName,cellFormat, locale, researchTau, deltaTau, deltaLength);
                    try {
                        transportSystem.createTransportSystem();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
