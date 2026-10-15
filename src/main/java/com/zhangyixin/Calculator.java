package com.zhangyixin;

import org.apache.commons.lang3.StringUtils;

/**
 * A small calculator whose operations can be driven by the CLI or by unit tests.
 */
public class Calculator {

    /**
     * Evaluates a binary expression given as strings.
     *
     * @param a  the first operand, must not be blank
     * @param op the operator: +, -, *, /
     * @param b  the second operand, must not be blank
     * @return the numeric result
     * @throws IllegalArgumentException if an operand is blank or the operator is unknown
     * @throws ArithmeticException      if a division by zero is requested
     */
    public double eval(String a, String op, String b) {
        if (StringUtils.isBlank(a) || StringUtils.isBlank(b) || StringUtils.isBlank(op)) {
            throw new IllegalArgumentException("input is blank");
        }

        double x = Double.parseDouble(a);
        double y = Double.parseDouble(b);

        return switch (op) {
            case "+" -> x + y;
            case "-" -> x - y;
            case "*" -> x * y;
            case "/" -> divide(x, y);
            default -> throw new IllegalArgumentException("unknown operator: " + op);
        };
    }

    private double divide(double x, double y) {
        if (y == 0.0) {
            throw new ArithmeticException("division by zero");
        }
        return x / y;
    }
}