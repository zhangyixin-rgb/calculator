package com.zhangyixin;

/**
 * Command-line entry point for the calculator.
 * Usage: java -cp target/calculator-1.0.jar com.zhangyixin.App <a> <op> <b>
 */
public class App {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -cp calculator-1.0.jar com.zhangyixin.App <a> <op> <b>");
            System.out.println("Example: java -cp calculator-1.0.jar com.zhangyixin.App 2 + 2");
            return;
        }

        Calculator calculator = new Calculator();
        double result = calculator.eval(args[0], args[1], args[2]);
        System.out.println(result);
    }
}