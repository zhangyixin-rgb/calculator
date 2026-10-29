package com.zhangyixin;

import java.math.BigDecimal;
import java.math.MathContext;

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
     * @throws ArithmeticException      if a division by zero is requested, or the result overflows
     */
    public double eval(String a, String op, String b) {
        if (StringUtils.isBlank(a) || StringUtils.isBlank(b) || StringUtils.isBlank(op)) {
            throw new IllegalArgumentException("input is blank");
        }

        BigDecimal x = new BigDecimal(a.trim());
        BigDecimal y = new BigDecimal(b.trim());

        BigDecimal result = switch (op) {
            case "+" -> x.add(y);
            case "-" -> x.subtract(y);
            case "*" -> x.multiply(y);
            case "/" -> divide(x, y);
            default -> throw new IllegalArgumentException("unknown operator: " + op);
        };

        double value = result.doubleValue();
        if (Double.isInfinite(value)) {
            throw new ArithmeticException("overflow: result exceeds double range");
        }
        return value == 0.0 ? 0.0 : value;
    }

    private BigDecimal divide(BigDecimal x, BigDecimal y) {
        if (y.signum() == 0) {
            throw new ArithmeticException("division by zero");
        }
        return x.divide(y, MathContext.DECIMAL64);
    }
}
