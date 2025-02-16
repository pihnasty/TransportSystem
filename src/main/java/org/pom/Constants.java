package org.pom;

public class Constants {
    /*
    the out flow from bunker to the conveyor belt
     */
    public final static String SPEED = "speed";
    public final static String TAU = "tau";
    public final static String NON_NEGATIVE_COMMON_MESSAGE = "Parameters for [%s] must be positive:";
    public final static String MAX_VALUE_COMMON_MESSAGE = "Parameters for [%s] must be less or equal max value %s:";
    public final static String KEY_VALUE_MESSAGE = "(%s : %s)";

    public static class ColumnsNames{
        public final static String BUNKER_CAPACITY = "bunker-capacity";
        public final static String BUNKER_INPUT_FLOW = "bunker-input-flow";
        public final static String BUNKER_OUTPUT_FLOW = "bunkerOutputFlow";
        public final static String BUNKER_OVER_MAX_CAPACITY = "bunker-over-max-capacity";
        public final static String BUNKER_PLANED_OUTPUT_FLOW = "bunker-planed-output-flow";
        public final static String BUNKER_REAL_OUTPUT_FLOW = "bunker-real-output-flow";
        public final static String CONVEYOR_BELT_BUNKER_OUTPUT_FLOW = "conveyor-belt-bunker-output-flow";
        public final static String DENSITY = "density";
        public final static String DENSITY_OVER_MAX_CAPACITY = "density-over-max-capacity";
        public final static String DELAY_FOR_CONVEYOR_LENGTH = "delayForConveyorLength";
        public final static String INITIAL_DENSITY = "initialDensity";
        public final static String INPUT_FLOW = JsonParametersNames.INPUT_FLOW;
        public final static String KSI = "ksi";
        public final static String OUTPUT_FLOW = "outputFlow";
        public final static String SPEED = "speed";
        public final static String TAU = "tau";

        public static String generateHeader(int conveyorId, String columnName) {
            return conveyorId + "." + columnName;
        }

        public static String generateHeader(int conveyorId, String columnName, int secondConveyorId) {
            return generateHeader(conveyorId, columnName) + "." + secondConveyorId;
        }
    }

    public static class JsonParametersNames {
        public final static String ID = "id";
        public final static String CONVEYOR_NODE = "connections";
        public final static String CONVEYORS = "conveyors";
        public final static String DELTA_LENGTH = "deltaLength";
        public final static String DELTA_TAU = "deltaTau";
        public final static String INIT_DATA_PATH = "initDataPath";
        public final static String INPUT_FLOW = "inputFlow";
        public final static String RESEARCH_TAU = "researchTau";
        public final static String REVERSIBLE = "reversible";
        public final static String OUTPUT_DATA_PATH = "outputDataPath";
    }
}
