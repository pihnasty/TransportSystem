package org.pom;

import org.pom.utils.yaml.SettingsManager;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class ComplexTransportSystem {
    private final SettingsManager settingsManager;
    public ComplexTransportSystem() {
        this.settingsManager = new SettingsManager();
    }

    private void start() {
        Map<String, TransportSystem> transportSystems = createTransportSystems();
        TreeMap<Double, String> sortedByStartTimeTransportSystems = sortedByStartTimeTransportSystems();

        var researchTau = settingsManager.getApp().getResearchTau();
        var taus = transportSystems.values().stream().findFirst().get().getTaus();

        Map<Integer, Conveyor> conveyors = new TreeMap<>();
        transportSystems.values().forEach(
                transportSystem -> {
                    transportSystem.getConveyors().forEach(
                            conveyor -> conveyors.put(conveyor.getId(), conveyor)
                    );
                }
        );

        AtomicReference<String> lastTransportSystemId = new AtomicReference<>("");

        sortedByStartTimeTransportSystems.keySet().forEach(
                startCoefficientTime -> {
                    var finishCoefficientTime = getFinishCoefficientTime(startCoefficientTime, sortedByStartTimeTransportSystems);
                    var previousCoefficientTime = getPreviousFinishCoefficientTime(startCoefficientTime, sortedByStartTimeTransportSystems);
                    TransportSystem transportSystem
                            = transportSystems.get(sortedByStartTimeTransportSystems.get(startCoefficientTime));
                    transportSystem.getConveyors().forEach(
                            conveyor -> {
                                var previousStateConveyor = conveyors.get(conveyor.getId());
                                    if (conveyor.isReversible()) {
                                        var previousReversibleStateConveyor = conveyors.get(conveyor.getReversible());
                                        conveyor.copyReversibleMainParameters(
                                                previousStateConveyor,
                                                previousReversibleStateConveyor,
                                                startCoefficientTime * researchTau,
                                                previousCoefficientTime * researchTau,
                                                transportSystem.getDeltaTau(),
                                                taus);
                                    } else {
                                        conveyor.copyMainParameters(
                                                previousStateConveyor,
                                                startCoefficientTime * researchTau,
                                                previousCoefficientTime * researchTau,
                                                taus);
                                    }
                            });

                    transportSystem.processingTransportSystem(startCoefficientTime * researchTau, finishCoefficientTime *researchTau);
                    transportSystem.getConveyors().forEach(
                            conveyor -> conveyors.put(conveyor.getId(), conveyor)
                    );
                    lastTransportSystemId.set(sortedByStartTimeTransportSystems.get(startCoefficientTime));
                }
        );
        fillOtherTransportSystems(transportSystems, lastTransportSystemId.get(), sortedByStartTimeTransportSystems, researchTau);
    }

    /**
     * Processes all transport systems except the last one by calling their processingTransportSystem method
     * with the computed finishTau. It is filled in all the values of each transport system up to the final tau.
     *
     * @param transportSystems A map containing transport systems identified by their IDs.
     * @param lastTransportSystemId The ID of the last transport system to be excluded from processing.
     * @param sortedByStartTimeTransportSystems A sorted map where keys represent start times
     *                                          and values represent transport system IDs.
     * @param researchTau A scaling factor used to compute the final processing time.
     */
    private void fillOtherTransportSystems(
            Map<String, TransportSystem> transportSystems,
            String lastTransportSystemId,
            TreeMap<Double, String> sortedByStartTimeTransportSystems,
            Double researchTau
    ) {
        var finishTau = sortedByStartTimeTransportSystems.lastKey() * researchTau;
        transportSystems.keySet().forEach(
                key -> {
                    if (!key.equals(lastTransportSystemId)) {
                        transportSystems.get(key).processingTransportSystem(finishTau, finishTau);
                    }
                }
        );
    }

    private static Double getFinishCoefficientTime(Double startTime, TreeMap<Double, String> sortedByStartTimeTransportSystems) {
        var finishTime = sortedByStartTimeTransportSystems.higherKey(startTime);
        return (finishTime == null) ? startTime : finishTime;
    }

    private static Double getPreviousFinishCoefficientTime(Double startTime, TreeMap<Double, String> sortedByStartTimeTransportSystems) {
        var previousFinishTime = sortedByStartTimeTransportSystems.lowerKey(startTime);
        return (previousFinishTime == null) ? startTime : previousFinishTime;
    }

    private TreeMap<Double, String> sortedByStartTimeTransportSystems() {
        TreeMap<Double, String> sortedByStartTimeTransportSystems = new TreeMap<>();
        var initTransportSystemFiles = settingsManager.getApp().getInitTransportSystemFiles();
        initTransportSystemFiles.keySet().forEach(
                transportSystemFileName -> {
                    var id = initTransportSystemFiles.get(transportSystemFileName).getId();
                    var startTimes = initTransportSystemFiles.get(transportSystemFileName).getStartTimes();
                    startTimes.forEach(
                            startTime -> sortedByStartTimeTransportSystems.put(startTime, id)
                    );
                }
        );
        return sortedByStartTimeTransportSystems;
    }

    private Map<String, TransportSystem> createTransportSystems() {
        var initTransportSystemFiles = settingsManager.getApp().getInitTransportSystemFiles();
        var cellFormat = settingsManager.getPrepareDataTableFormat().getCellFormat();
        var locale = settingsManager.getApp().getLocale();
        var researchTau = settingsManager.getApp().getResearchTau();
        var deltaTau = settingsManager.getApp().getDeltaTau();
        var deltaLength = settingsManager.getApp().getDeltaLength();
        Map<String, TransportSystem> transportSystems = new TreeMap<>();
        initTransportSystemFiles.keySet().forEach(
                transportSystemFileName -> {
                    TransportSystem transportSystem = new TransportSystem(
                            transportSystemFileName,cellFormat, locale, researchTau, deltaTau, deltaLength
                    );
                    transportSystems.put(initTransportSystemFiles.get(transportSystemFileName).getId(), transportSystem);
                    transportSystem.createTransportSystem();
                }
        );
        return transportSystems;
    }

    public static void main(String[] args) {
        var complexTransportSystem = new ComplexTransportSystem();
        complexTransportSystem.start();
    }
}
