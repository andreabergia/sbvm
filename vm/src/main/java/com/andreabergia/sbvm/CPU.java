package com.andreabergia.sbvm;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Stack;

import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.AND;
import static com.andreabergia.sbvm.Instructions.CALL;
import static com.andreabergia.sbvm.Instructions.DIV;
import static com.andreabergia.sbvm.Instructions.DUP;
import static com.andreabergia.sbvm.Instructions.HALT;
import static com.andreabergia.sbvm.Instructions.ISEQ;
import static com.andreabergia.sbvm.Instructions.ISGE;
import static com.andreabergia.sbvm.Instructions.ISGT;
import static com.andreabergia.sbvm.Instructions.JIF;
import static com.andreabergia.sbvm.Instructions.JMP;
import static com.andreabergia.sbvm.Instructions.LOAD;
import static com.andreabergia.sbvm.Instructions.MUL;
import static com.andreabergia.sbvm.Instructions.NOT;
import static com.andreabergia.sbvm.Instructions.OR;
import static com.andreabergia.sbvm.Instructions.POP;
import static com.andreabergia.sbvm.Instructions.PUSH;
import static com.andreabergia.sbvm.Instructions.RET;
import static com.andreabergia.sbvm.Instructions.STORE;
import static com.andreabergia.sbvm.Instructions.SUB;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;


public class CPU {
    private final int[] program;
    private int instructionAddress = 0;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private boolean halted = false;
    private Stack<Frame> frames = new Stack<>();

    public CPU(int... instructions) {
        checkArgument(instructions.length > 0, "A program should have at least an instruction");
        this.program = instructions;
        this.frames.push(new Frame(0)); // Prepare the initial frame
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

            case POP: {
                checkStackHasAtLeastOneItem("POP");
                stack.pop();
                break;
            }

            case DUP: {
                checkStackHasAtLeastOneItem("DUP");
                int n = stack.peek();
                stack.push(n);
                break;
            }

            case LOAD: {
                int varNumber = getNextWordFromProgram("Should have the variable number after the LOAD instruction");
                stack.push(getCurrentFrame().getVariable(varNumber));
                break;
            }

            case STORE: {
                int varNumber = getNextWordFromProgram("Should have the variable number after the STORE instruction");
                checkStackHasAtLeastOneItem("STORE");
                getCurrentFrame().setVariable(varNumber, stack.pop());
                break;
            }

            case NOT: {
                checkStackHasAtLeastOneItem("NOT");
                stack.push(toInt(!toBool(stack.pop())));
                break;
            }

            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case AND:
            case OR:
            case ISEQ:
            case ISGE:
            case ISGT: {
                if (stack.size() < 2) {
                    throw new InvalidProgramException("There should be at least two items on the stack to execute a binary instruction");
                }
                int n2 = stack.pop();
                int n1 = stack.pop();
                stack.push(doBinaryOp(instruction, n1, n2));
                break;
            }

            case JMP: {
                // The word after the instruction will contain the address to jump to
                int address = getNextWordFromProgram("Should have the address after the JMP instruction");
                checkJumpAddress(address);
                this.instructionAddress = address;
                break;
            }

            case JIF: {
                // The word after the instruction will contain the address to jump to
                int address = getNextWordFromProgram("Should have the address after the JIF instruction");
                checkJumpAddress(address);
                checkStackHasAtLeastOneItem("JIF");
                if (toBool(stack.pop())) {
                    this.instructionAddress = address;
                }
                break;
            }

            case CALL: {
                // The word after the instruction will contain the function address
                int address = getNextWordFromProgram("Should have the address after the CALL instruction");
                checkJumpAddress(address);
                this.frames.push(new Frame(this.instructionAddress)); // Push a new stack frame
                this.instructionAddress = address;                    // and jump!
                break;
            }

            case RET: {
                // Pop the stack frame and return to the previous address
                checkThereIsAReturnAddress();
                int returnAddress = getCurrentFrame().getReturnAddress();
                this.frames.pop();
                this.instructionAddress = returnAddress;
                break;
            }
        }
    }

    private void checkJumpAddress(int address) {
        if (address < 0 || address >= program.length) {
            throw new InvalidProgramException(String.format("Invalid jump address %d at %d", address, instructionAddress));
        }
    }

    private void checkThereIsAReturnAddress() {
        if (this.frames.size() == 1) {
            throw new InvalidProgramException(String.format("Invalid RET instruction: no current function call %d", instructionAddress));
        }
    }

    private void checkStackHasAtLeastOneItem(String instruction) {
        if (stack.size() < 1) {
            throw new InvalidProgramException("There should be at least one item on the stack to execute an " + instruction + " instruction");
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
            case AND:
                return toInt(toBool(n1) && toBool(n2));
            case OR:
                return toInt(toBool(n1) || toBool(n2));
            case ISEQ:
                return toInt(n1 == n2);
            case ISGE:
                return toInt(n1 >= n2);
            case ISGT:
                return toInt(n1 > n2);
            default:
                throw new AssertionError();
        }
    }

    private boolean toBool(int n) {
        return n != 0;
    }

    private int toInt(boolean b) {
        return b ? 1 : 0;
    }

    private int getNextWordFromProgram(String errorMessage) {
        if (instructionAddress >= program.length) {
            throw new InvalidProgramException(errorMessage);
        }
        int nextWord = program[instructionAddress];
        ++instructionAddress;
        return nextWord;
    }

    public Frame getCurrentFrame() {
        return frames.peek();
    }
}
