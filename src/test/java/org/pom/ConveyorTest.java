package org.pom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.*;

class ConveyorTest {

    private Bunker bunkerMock;
    private Density densityMock;
    private InputFlow inputFlowMock;
    private BunkerOutputFlow bunkerOutputFlowMock;
    private InitialDensity initialDensityMock;
    private Speed speedMock;
    private Conveyor conveyor;
    private double planedBunkerOutput;
    private double bunkerInput;
    private double tau;

    @BeforeEach
    void setUp() {
        tau = 1.0;
        bunkerInput = 50.0;
        planedBunkerOutput = 30.0;
        // Mock dependencies
        bunkerMock = mock(Bunker.class);
        inputFlowMock = mock(InputFlow.class);
        bunkerOutputFlowMock = mock(BunkerOutputFlow.class);
        densityMock = mock(Density.class);
        initialDensityMock = mock(InitialDensity.class);
        speedMock = mock(Speed.class);

        // Define behavior for mocked methods
        when(densityMock.getMaxAvailableDensity()).thenReturn(TestConstant.MAX_AVAILABLE_DENSITY);

        // Create the Conveyor instance with mocks
        conveyor = new Conveyor(1, bunkerMock, densityMock, speedMock,
                inputFlowMock, bunkerOutputFlowMock, initialDensityMock, null, TestConstant.CONVEYOR_LENGTH);
    }

    @Test
    void testAddParametersValues() {
        // Mocking behavior of Bunker
        when(bunkerMock.getOutputFlowFromBunkerToConveyorBelt(tau)).thenReturn(25.0);

        // Act
        conveyor.addParametersValues(tau, bunkerInput, planedBunkerOutput, TestConstant.DEFAULT_SPEED_VALUE);

        verify(bunkerMock).addParametersValues(tau, bunkerInput, planedBunkerOutput, TestConstant.MAX_AVAILABLE_DENSITY * TestConstant.DEFAULT_SPEED_VALUE);
        verify(speedMock).addParametersValues(tau, TestConstant.DEFAULT_SPEED_VALUE);
        verify(densityMock).addParametersValues(tau, 25.0 / TestConstant.DEFAULT_SPEED_VALUE);
        verify(densityMock).getMaxAvailableDensity();
    }

    /**
     * Verify density calculations for zero speed
     */
    @Test
    void testAddParametersValuesWithZeroSpeed() {
        double speedValue = 0.0;
        conveyor.addParametersValues(tau, bunkerInput, planedBunkerOutput, speedValue);
        verify(densityMock, times(1)).addParametersValues(tau, TestConstant.MAX_AVAILABLE_DENSITY);
        verify(densityMock, times(2)).getMaxAvailableDensity();
    }
}
