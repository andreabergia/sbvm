package com.andreabergia.sbvm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CPUStack {
    private final Deque<Integer> stack = new ArrayDeque<>();

    public void push(int word) {
        stack.push(word);
    }

    public int pop() throws NoSuchElementException {
        return stack.pop();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }

    public String print() {
        return String.format("Stack: %d items: %s", size(), stack);
    }

    public int[] getContentAsArray() {
        int[] array = new int[stack.size()];
        Iterator<Integer> iterator = stack.iterator();
        for (int i = 0; i < array.length; ++i) {
            array[i] = iterator.next();
        }
        return array;
    }
}
