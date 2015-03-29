package com.andreabergia.sbvm;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.DIV;
import static com.andreabergia.sbvm.Instructions.HALT;
import static com.andreabergia.sbvm.Instructions.MUL;
import static com.andreabergia.sbvm.Instructions.PUSH;
import static com.andreabergia.sbvm.Instructions.SUB;
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

            case ADD:
            case SUB:
            case MUL:
            case DIV: {
                if (stack.size() < 2) {
                    throw new InvalidProgramException("There should be at least two items on the stack to execute an ADD");
                }
                int n2 = stack.pop();
                int n1 = stack.pop();
                stack.push(doBinaryOp(instruction, n1, n2));
                break;
            }
        }
    }

    private Integer doBinaryOp(int instruction, int n1, int n2) {
        switch (instruction) {
            case ADD:
                return n1 + n2;
            case SUB:
                return n1 - n2;
            case MUL:
                return n1 * n2;
            case DIV:
                return n1 / n2;
            default:
                throw new AssertionError();
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
