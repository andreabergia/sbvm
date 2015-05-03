package com.andreabergia.sbvm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Frame {
    private final Map<Integer, Integer> variables = new HashMap<>();
    private final int returnAddress;

    public Frame(int returnAddress) {
        this.returnAddress = returnAddress;
    }

    public int getVariable(int varNumber) {
        return variables.getOrDefault(varNumber, 0);
    }

    public void setVariable(int varNumber, int value) {
        variables.put(varNumber, value);
    }

    public int getReturnAddress() {
        return returnAddress;
    }

    public Map<Integer, Integer> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
}
