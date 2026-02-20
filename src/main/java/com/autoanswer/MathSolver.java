package com.autoanswer;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathSolver {
    // Pattern to detect math equations like "5+3", "12*4", "100-25", "20/4"
    private static final Pattern MATH_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(\\d+(?:\\.\\d+)?)");
    
    public static String solveMathEquation(String message) {
        Matcher matcher = MATH_PATTERN.matcher(message);
        
        if (matcher.find()) {
            try {
                double num1 = Double.parseDouble(matcher.group(1));
                String operator = matcher.group(2);
                double num2 = Double.parseDouble(matcher.group(3));
                
                double result = 0;
                switch (operator) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "*":
                        result = num1 * num2;
                        break;
                    case "/":
                        if (num2 == 0) {
                            return null; // Division by zero
                        }
                        result = num1 / num2;
                        break;
                    default:
                        return null;
                }
                
                // Return integer if result is a whole number, otherwise decimal
                if (result == (long) result) {
                    return String.valueOf((long) result);
                } else {
                    return String.format("%.2f", result);
                }
            } catch (NumberFormatException e) {
                AutoAnswerMod.LOGGER.error("Failed to parse math equation", e);
                return null;
            }
        }
        
        return null;
    }
    
    // More advanced expression evaluator for complex equations
    public static String solveComplexExpression(String expression) {
        try {
            // First, try to find a math expression pattern with spaces allowed
            // This matches things like "39 + 95" or "12 * 4" even in text
            Pattern spacedPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(\\d+(?:\\.\\d+)?)");
            Matcher spacedMatcher = spacedPattern.matcher(expression);
            
            if (spacedMatcher.find()) {
                // Extract the full matched expression and remove spaces
                String mathExpr = spacedMatcher.group(0).replaceAll("\\s+", "");
                
                double result = evaluateExpression(mathExpr);
                
                // Return integer if result is a whole number
                if (result == (long) result) {
                    return String.valueOf((long) result);
                } else {
                    return String.format("%.2f", result);
                }
            }
            
            // If no spaced pattern found, try extracting continuous math expression
            String noSpaces = expression.replaceAll("\\s+", "");
            Pattern complexPattern = Pattern.compile("([\\d+\\-*/().]+)");
            Matcher matcher = complexPattern.matcher(noSpaces);
            
            if (matcher.find()) {
                String mathExpr = matcher.group(1);
                
                // Check if the expression actually contains at least one operator
                // This prevents matching plain numbers like "5" or "123"
                if (!mathExpr.matches(".*[+\\-*/].*")) {
                    return null; // It's just a number, not an equation
                }
                
                double result = evaluateExpression(mathExpr);
                
                // Return integer if result is a whole number
                if (result == (long) result) {
                    return String.valueOf((long) result);
                } else {
                    return String.format("%.2f", result);
                }
            }
        } catch (Exception e) {
            // If complex evaluation fails, try simple pattern
            return solveMathEquation(expression);
        }
        
        return null;
    }
    
    private static double evaluateExpression(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }
}
