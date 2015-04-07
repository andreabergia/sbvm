package com.andreabergia.sbvm;

import java.util.HashMap;
import java.util.Map;

public class Frame {
    private final Map<Integer, Integer> variables = new HashMap<>();

    public int getVariable(int varNumber) {
        return variables.getOrDefault(varNumber, 0);
    }

    public void setVariable(int varNumber, int value) {
        variables.put(varNumber, value);
    }
}
