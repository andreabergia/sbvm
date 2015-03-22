package com.andreabergia.sbvm;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.HALT;
import static com.andreabergia.sbvm.Instructions.PUSH;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class CPU {
    private final int[] program;
    private int instructionAddress = 0;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private boolean halted = false;

    public CPU(int... instructions) {
        checkArgument(instructions.length > 0, "A program should have at least an instruction");
        this.program = instructions;
    }

    public int getInstructionAddress() {
        return instructionAddress;
    }

    public Collection<Integer> getStack() {
        return stack;
    }

    public boolean isHalted() {
        return halted;
    }

    public void run() {
        while (!halted) {
            step();
        }
    }

    public void step() {
        checkState(!halted, "An halted CPU cannot execute the program");
        int nextInstruction = getNextWordFromProgram("Should have a next instruction");
        decodeInstruction(nextInstruction);
    }

    private void decodeInstruction(int instruction) {
        switch (instruction) {
            default:
                throw new InvalidProgramException("Unknown instruction: " + instruction);

            case HALT:
                this.halted = true;
                break;

            case PUSH: {
                // The word after the instruction will contain the value to push
                int value = getNextWordFromProgram("Should have the value after the PUSH instruction");
                stack.push(value);
                break;
            }

            case ADD: {
                checkState(stack.size() >= 2);
                int n1 = stack.pop();
                int n2 = stack.pop();
                stack.push(n1 + n2);
                break;
            }
        }
    }

    private int getNextWordFromProgram(String errorMessage) {
        if (instructionAddress >= program.length) {
            throw new InvalidProgramException(errorMessage);
        }
        int nextWord = program[instructionAddress];
        ++instructionAddress;
        return nextWord;
    }
}
