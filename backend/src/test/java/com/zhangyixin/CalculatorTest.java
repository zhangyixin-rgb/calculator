package com.zhangyixin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addWorks() {
        assertEquals(4.0, calculator.eval("2", "+", "2"));
    }

    @Test
    void subtractWorks() {
        assertEquals(2.0, calculator.eval("5", "-", "3"));
    }

    @Test
    void multiplyWorks() {
        assertEquals(6.0, calculator.eval("3", "*", "2"));
    }

    @Test
    void divideWorks() {
        assertEquals(2.0, calculator.eval("10", "/", "5"));
    }

    @Test
    void blankInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> calculator.eval("", "+", "2"));
    }

    @Test
    void divisionByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> calculator.eval("1", "/", "0"));
    }

    @Test
    void unknownOperatorThrows() {
        assertThrows(IllegalArgumentException.class, () -> calculator.eval("1", "%", "2"));
    }

    @Test
    void boundaryLargeSum() {
        assertEquals(4294967294.0, calculator.eval("2147483647", "+", "2147483647"));
    }

    @Test
    void fractionalDivision() {
        assertEquals(2.5, calculator.eval("10", "/", "4"));
    }

    @Test
    void precisionAddition() {
        assertEquals(0.3, calculator.eval("0.1", "+", "0.2"));
    }

    @Test
    void precisionMultiplication() {
        assertEquals(0.3, calculator.eval("0.1", "*", "3"));
    }

    @Test
    void precisionDivision() {
        assertEquals(3.0, calculator.eval("0.3", "/", "0.1"));
    }

    @Test
    void overflowDetected() {
        assertThrows(ArithmeticException.class, () -> calculator.eval("1e308", "*", "10"));
    }
}
