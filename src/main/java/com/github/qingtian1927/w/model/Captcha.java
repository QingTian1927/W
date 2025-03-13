package com.github.qingtian1927.w.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
public class Captcha {
    public static final String OP_ADD = "+";
    public static final String OP_SUB = "-";
    public static final String OP_MUL = "×";
    public static final int OP_TOTAL = 3;

    public static final String DATA_SEPARATOR = ",";
    public static final int LOWER_BOUND = 0;
    public static final int UPPER_BOUND = 10;

    @Range(min = LOWER_BOUND, max = UPPER_BOUND)
    private int operand1;
    @Range(min = LOWER_BOUND, max = UPPER_BOUND)
    private int operand2;
    private String operation;

    public static Optional<Integer> solve(Captcha captcha) {
        if (captcha == null) {
            throw new NullPointerException("Captcha is null");
        }

        if (captcha.getOperation() == null) {
            throw new NullPointerException("Operation is null");
        }

        Optional<Integer> result = Optional.empty();
        switch (captcha.getOperation()) {
            case OP_ADD -> result = Optional.of(captcha.operand1 + captcha.operand2);
            case OP_SUB -> result = Optional.of(captcha.operand1 - captcha.operand2);
            case OP_MUL -> result = Optional.of(captcha.operand1 * captcha.operand2);
        }

        return result;
    }

    public static Captcha parse(String dataString) {
        String[] tokens = dataString.split(DATA_SEPARATOR);
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid data string");
        }

        try {
            int operand1 = Integer.parseInt(tokens[0]);
            int operand2 = Integer.parseInt(tokens[1]);

            String operation = tokens[2];
            if (!operation.equals(OP_ADD) && !operation.equals(OP_SUB) && !operation.equals(OP_MUL)) {
                throw new IllegalArgumentException("Invalid operation");
            }

            return new Captcha(operand1, operand2, operation);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid operands");
        }
    }

    public Captcha() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        this.operand1 = random.nextInt(LOWER_BOUND, UPPER_BOUND + 1);
        this.operand2 = random.nextInt(LOWER_BOUND, UPPER_BOUND + 1);

        switch (random.nextInt(OP_TOTAL)) {
            case 1 -> this.operation = OP_SUB;
            case 2 -> this.operation = OP_MUL;
            default -> this.operation = OP_ADD;
        }
    }

    public void setOperation(String op) {
        if (!op.equals(OP_ADD) && !op.equals(OP_SUB) && !op.equals(OP_MUL)) {
            throw new IllegalArgumentException("Invalid operation: " + op);
        }
        this.operation = op;
    }

    public String toDataString() {
        return this.operand1 + DATA_SEPARATOR + this.operand2 + DATA_SEPARATOR + this.operation;
    }
}
