package org.pom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OutputFlowTest {

    @Mock
    private Bunker bunker;

    @Mock
    private Speed speed;

    @Mock
    private InitialDensity initialDensity;

    @Mock
    private TransportDelay transportDelay;

    private OutputFlow outputFlow;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        outputFlow = new OutputFlow(bunker, speed, initialDensity, transportDelay);
    }

    @Test
    void addOutputFlowValue() {
        double tau = 10.0;
        double delayTau = 5.0;
        double bunkerFlow = 100.0;
        double speedAtTauMinusDelay = 20.0;

        when(bunker.getOutputFlowFromBunkerToConveyorBelt(tau - delayTau)).thenReturn(bunkerFlow);
        when(speed.getSpeedAtTau(tau - delayTau)).thenReturn(speedAtTauMinusDelay);
        when(speed.getSpeedAtTau(tau)).thenReturn(TestConstant.DEFAULT_SPEED_VALUE);

        outputFlow.addOutputFlowValue(tau, delayTau);

        double expectedFlow = (bunkerFlow / speedAtTauMinusDelay) * TestConstant.DEFAULT_SPEED_VALUE;
        assertEquals(expectedFlow, outputFlow.getOutputFlowAtTau(tau));

        verify(bunker).getOutputFlowFromBunkerToConveyorBelt(tau - delayTau);
        verify(speed).getSpeedAtTau(tau - delayTau);
        verify(speed).getSpeedAtTau(tau);
    }

    @Test
    void getOutputFlowAtTau() {
        double tau = 10.0;
        double delayTau = 5.0;
        double bunkerFlow = 100.0;
        double speedAtTauMinusDelay = 20.0;

        when(bunker.getOutputFlowFromBunkerToConveyorBelt(tau - delayTau)).thenReturn(bunkerFlow);
        when(speed.getSpeedAtTau(tau - delayTau)).thenReturn(speedAtTauMinusDelay);
        when(speed.getSpeedAtTau(tau)).thenReturn(TestConstant.DEFAULT_SPEED_VALUE);

        outputFlow.addOutputFlowValue(tau, delayTau);

        double expectedFlow = (bunkerFlow / speedAtTauMinusDelay) * TestConstant.DEFAULT_SPEED_VALUE;
        assertEquals(expectedFlow, outputFlow.getOutputFlowAtTau(tau));
    }
}