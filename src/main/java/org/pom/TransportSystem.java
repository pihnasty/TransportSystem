package org.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.pom.utils.json.ObjectMapperFactory;
import org.pom.utils.yaml.SettingsManager;

import java.io.File;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportSystem {

    @Getter
    private List<Conveyor> conveyors= new ArrayList<>();
    private final List<Conveyor> rootConveyors;
    private final String initDataPath;
    private final double researchTau;
    private final double deltaTau;
    private final double deltaLength;

    // Map to store each conveyor and its children (downstream conveyors)
    private final Map<Conveyor, List<Conveyor>> conveyorTree;

    @JsonCreator
    public TransportSystem(
            @JsonProperty(Constants.JsonParametersNames.CONVEYORS) List<Conveyor> conveyors,
            @JsonProperty(Constants.JsonParametersNames.INIT_DATA_PATH) String initDataPath,
            @JsonProperty(Constants.JsonParametersNames.RESEARCH_TAU) double researchTau,
            @JsonProperty(Constants.JsonParametersNames.DELTA_TAU) double deltaTau,
            @JsonProperty(Constants.JsonParametersNames.DELTA_LENGTH) double deltaLength) {
        this.initDataPath = initDataPath;
        this.researchTau = researchTau;
        this.deltaTau = deltaTau;
        this.deltaLength = deltaLength;
        this.conveyors = conveyors;
        this.rootConveyors = new ArrayList<>();
        this.conveyorTree = new HashMap<>();
    }

    // Adds a root conveyor (no parent)
    public void addRootConveyor(Conveyor conveyor) {
        rootConveyors.add(conveyor);
        conveyorTree.putIfAbsent(conveyor, new ArrayList<>());
    }

    // Connects a parent conveyor to a child conveyor
    public void connectConveyors(Conveyor parent, Conveyor child) {
        conveyorTree.putIfAbsent(parent, new ArrayList<>());
        conveyorTree.get(parent).add(child);
        conveyorTree.putIfAbsent(child, new ArrayList<>());
    }

    // Retrieves downstream conveyors for a given conveyor
    public List<Conveyor> getChildren(Conveyor conveyor) {
        return conveyorTree.getOrDefault(conveyor, Collections.emptyList());
    }

    // Updates bunker input for all conveyors in the tree
    public void updateFlow() {
        for (Conveyor root : rootConveyors) {
            updateFlowRecursive(root);
        }
    }

    // Recursive helper to propagate flow in the tree
    private void updateFlowRecursive(Conveyor conveyor) {
        var outputFlow = conveyor.getOutputFlow(); // Get output flow of current conveyor

        for (Conveyor child : conveyorTree.getOrDefault(conveyor, Collections.emptyList())) {
            //child.addParametersValues(child.getCurrentTau(), outputFlow, child.getBunkerOutput(), child.getSpeed());
            updateFlowRecursive(child); // Recursively update children
        }
    }

    // Finds all leaf conveyors (final conveyors with no children)
    public List<Conveyor> getLeafConveyors() {
        List<Conveyor> leaves = new ArrayList<>();
        for (var entry : conveyorTree.entrySet()) {
            if (entry.getValue().isEmpty()) {
                leaves.add(entry.getKey());
            }
        }
        return leaves;
    }

    private static void createTransportSystem() {
        SettingsManager settingsManager = new SettingsManager();
        var initTransportSystemFile = settingsManager.getApp().getInitTransportSystemFile();
        ObjectMapper objectMapper = ObjectMapperFactory.createTransportSystemMapper();
        TransportSystem transportSystem = null;
        try {
            transportSystem = objectMapper.readValue(new File(initTransportSystemFile), TransportSystem.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

      transportSystem.getConveyors().get(0);

        System.out.println(transportSystem);
    }

    public static void main(String[] args) {
        createTransportSystem();
    }
}

