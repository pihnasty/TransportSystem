package org.pom.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.SneakyThrows;
import org.pom.Constants;
import org.pom.Conveyor;
import org.pom.TransportSystem;
import org.pom.utils.MathUtil;
import org.pom.utils.io.csv.read.CsvReaderP;
import org.pom.utils.yaml.SettingsManager;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TransportSystemDeserializer extends StdDeserializer<TransportSystem> {

    private final Locale locale;
    public TransportSystemDeserializer() {
        super(TransportSystem.class);
        SettingsManager settingsManager = new SettingsManager();
        this.locale = settingsManager.getLocale();
    }

    @SneakyThrows
    @Override
    public TransportSystem deserialize(JsonParser p, DeserializationContext ctxt) {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);
        String initDataPath = root.get("initDataPath").asText();

        File file = new File(initDataPath);
        CsvReaderP csvReaderP = new CsvReaderP("%8.3f ", ';',file.getParent(), file.getName());
        List<List<String>> table = csvReaderP.readFromFile();
        var transposeTable = MathUtil.transposeMatrix(table," "::repeat);

        var ksis = extractRowByName(Constants.ColumnsNames.KSI, transposeTable);
        var taus = extractRowByName(Constants.ColumnsNames.TAU, transposeTable);


        Map<Integer, Conveyor> conveyors = new TreeMap<>();
        JsonNode conveyorsNode = root.get(Constants.JsonParametersNames.CONVEYORS);
        if (conveyorsNode != null) {
            for (JsonNode conveyorNode : conveyorsNode) {
                Conveyor conveyor = mapper.treeToValue(conveyorNode, Conveyor.class);  // Deserialize conveyor node to Conveyor object
                var conveyorId = conveyor.getId();
                var initialDensityValues = extractRowByName(
                        Constants.ColumnsNames.generateHeader(conveyorId, Constants.ColumnsNames.INITIAL_DENSITY),
                        transposeTable);
                if (initialDensityValues != null) {
                    conveyor.getInitialDensity().setValues(convertToTreeMap(ksis, initialDensityValues));
                }

                var inputFlowValues = extractRowByName(
                        Constants.ColumnsNames.generateHeader(conveyorId, Constants.JsonParametersNames.INPUT_FLOW),
                        transposeTable);
                if (inputFlowValues != null) {
                    conveyor.getInputFlow().setValues(convertToTreeMap(taus, inputFlowValues));
                }

                var bunkerOutputFlowValues = extractRowByName(
                        Constants.ColumnsNames.generateHeader(conveyorId, Constants.ColumnsNames.BUNKER_OUTPUT_FLOW),
                        transposeTable);
                if (bunkerOutputFlowValues != null) {
                    conveyor.getBunkerOutputFlow().setValues(convertToTreeMap(taus, bunkerOutputFlowValues));
                }

                var speedValues = extractRowByName(
                        Constants.ColumnsNames.generateHeader(conveyorId, Constants.ColumnsNames.SPEED),
                        transposeTable);
                if (speedValues != null) {
                    conveyor.getSpeed().setValues(convertToTreeMap(taus, speedValues));
                }
                conveyors.put(conveyorId, conveyor);
            }
        }

        for (Integer conveyorId : conveyors.keySet()) {
            var currentConveyor = conveyors.get(conveyorId);
            currentConveyor.addId(conveyorId);
            currentConveyor.getConveyorNode().getOutputFunctionConfigMap().keySet().forEach(
                    functionConfigKey -> {
                        var outputConveyor = conveyors.get(functionConfigKey);

                        var inputFlowValues = extractRowByName(
                                Constants.ColumnsNames.generateHeader(conveyorId, Constants.JsonParametersNames.CONVEYOR_NODE, functionConfigKey),
                                transposeTable);
                        var inputFlowMap
                                = Objects.nonNull(inputFlowValues)
                                ? convertToTreeMap(taus, inputFlowValues) : new TreeMap<Double, Double>();
                        currentConveyor.addOutputConveyorFlow(functionConfigKey, inputFlowMap);
                        outputConveyor.addInputConveyorFlow(conveyorId, inputFlowMap);
                    }
            );
        }

        TransportSystem transportSystem
                = new TransportSystem(
                new ArrayList<>(conveyors.values()),
                        initDataPath,
                root.get("researchTau").asDouble(),
                root.get("deltaTau").asDouble(),
                root.get("deltaLength").asDouble()
        );
        return transportSystem;
    }

    private List<Double> extractRowByName(String rowName, List<List<String>> transposeTable) {
        NumberFormat format = NumberFormat.getInstance(locale);
        var stringsRow = transposeTable.stream().filter(column -> rowName.equals(column.get(0).trim())).findFirst().orElse(null);
        if (stringsRow == null) {
            return null;
        }
        stringsRow.remove(0);
        var withoutStringsRow = stringsRow.stream().map(String::trim).filter(cell -> !cell.isEmpty()).collect(Collectors.toList());

        List<Double> correctRow = withoutStringsRow.stream().map(cell-> {
            try {
                return format.parse(cell).doubleValue();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return correctRow;
    }

    public static TreeMap<Double, Double> convertToTreeMap(List<Double> keys, List<Double> values) {
        TreeMap<Double, Double> result = new TreeMap<>();
        IntStream.range(0, values.size()).forEach(i -> result.put(keys.get(i), values.get(i)));
        return result;
    }
}
