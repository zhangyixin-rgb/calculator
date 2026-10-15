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
}