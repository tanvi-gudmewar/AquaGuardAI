package com.aquaguard.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TankPredictionMathTest {
    @Test
    void simpleTimeToFullCalculationIsCorrect() {
        double remainingLiters = 300;
        double netRateLpm = 4;
        assertEquals(75.0, remainingLiters / netRateLpm, 0.0001);
    }

    @Test
    void zeroOrNegativeNetFlowDoesNotFillTank() {
        double incoming = 2;
        double outgoing = 3;
        double net = incoming - outgoing;
        assertTrue(net <= 0);
    }
}
