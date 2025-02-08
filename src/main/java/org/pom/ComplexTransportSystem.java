package org.pom;

import org.pom.utils.yaml.SettingsManager;

import java.util.Map;
import java.util.TreeMap;

public class ComplexTransportSystem {
    private final SettingsManager settingsManager;
    public ComplexTransportSystem() {
        this.settingsManager = new SettingsManager();
    }

    private void start() {
        Map<String, TransportSystem> transportSystems = createTransportSystems();
        TreeMap<Double, String> sortedByStartTimeTransportSystems = sortedByStartTimeTransportSystems();

        var researchTau = settingsManager.getApp().getResearchTau();
        sortedByStartTimeTransportSystems.keySet().forEach(
                startCoefficientTime -> {
                    var finishCoefficientTime = getFinishCoefficientTime(startCoefficientTime, sortedByStartTimeTransportSystems);
                    TransportSystem transportSystem
                            = transportSystems.get(sortedByStartTimeTransportSystems.get(startCoefficientTime));
                    transportSystem.processingTransportSystem(startCoefficientTime * researchTau, finishCoefficientTime *researchTau);
                    System.out.println();
                }
        );


    }

    private static Double getFinishCoefficientTime(Double startTime, TreeMap<Double, String> sortedByStartTimeTransportSystems) {
        var finishTime = sortedByStartTimeTransportSystems.higherKey(startTime);
        return (finishTime == null) ? 1.0 : finishTime;
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
        System.out.println("transportSystem");
    }
}
