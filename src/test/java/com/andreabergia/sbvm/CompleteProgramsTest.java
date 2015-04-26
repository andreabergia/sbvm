package com.andreabergia.sbvm;

import org.junit.Test;

import static com.andreabergia.sbvm.CPUAssertions.assertProgramRunsToHaltAndInstructionAddressIs;
import static com.andreabergia.sbvm.CPUAssertions.assertStackContains;
import static com.andreabergia.sbvm.CPUAssertions.assertStackIsEmpty;
import static com.andreabergia.sbvm.CPUAssertions.assertVariableValues;
import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.CALL;
import static com.andreabergia.sbvm.Instructions.HALT;
import static com.andreabergia.sbvm.Instructions.ISGE;
import static com.andreabergia.sbvm.Instructions.ISGT;
import static com.andreabergia.sbvm.Instructions.JIF;
import static com.andreabergia.sbvm.Instructions.JMP;
import static com.andreabergia.sbvm.Instructions.LOAD;
import static com.andreabergia.sbvm.Instructions.NOT;
import static com.andreabergia.sbvm.Instructions.PUSH;
import static com.andreabergia.sbvm.Instructions.RET;
import static com.andreabergia.sbvm.Instructions.STORE;
import static com.andreabergia.sbvm.Instructions.SUB;

public class CompleteProgramsTest {
    @Test
    public void testIfInstruction() throws Exception {
        /**
         * The code is:
         * if (a > b) {
         *     c = a;
         * } else {
         *     c = b;
         * }
         *
         * We're going to use variable 0 as "a", variable 1 as "b", variable 2 as "c".
         */
        CPU cpu = new CPU(
                // Init a with "6"
                PUSH, 6,
                STORE, 0,
                // Init b with "4"
                PUSH, 4,
                STORE, 1,
                // Load a and b into the stack
                LOAD, 0,            // Stack contains a
                LOAD, 1,            // Stack contains a, b
                ISGT,               // Stack contains a > b
                JIF, 21,
                // This is the "else" path
                LOAD, 1,            // Stack contains b
                STORE, 2,           // Set c to the stack head, meaning c = b
                JMP, 25,
                // This is the "if" path, and this is the address 21
                LOAD, 0,            // Stack contains a
                STORE, 2,           // Set c to the stack head, meaning c = a
                // Done; this is address 25
                HALT
        );
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 26);
        assertStackIsEmpty(cpu);
        assertVariableValues(cpu, 6, 4, 6);
    }

    @Test
    public void testMultiplication() throws Exception {
        /**
         * We're going to multiply two numbers (a, b) without using the MUL instruction.
         *
         * The algorithm is:
         *
         * int total = 0;
         * while (b >= 1) {
         *     total += a;
         *     --b;
         * }
         *
         * We're going to use variable 0 as "a", variable 1 as "b", variable 2 as total.
         */
        CPU cpu = new CPU(
                // Init a with "6"
                PUSH, 6,
                STORE, 0,
                // Init b with "4"
                PUSH, 4,
                STORE, 1,
                // Init total to 0
                PUSH, 0,
                STORE, 2,
                // While part
                // Here is address 12
                LOAD, 1,            // Stack contains b
                PUSH, 1,            // Stack contains b, 1
                ISGE,               // Stack contains b >= 1
                NOT,                // Stack contains b < 1
                JIF, 36,            // 36 is the address of the HALT label
                // Inner loop part
                LOAD, 0,            // Stack contains a
                LOAD, 2,            // Stack contains a, total
                ADD,                // Stack contains a + total
                STORE, 2,           // Save in total, meaning total = a + total
                LOAD, 1,            // Stack contains b
                PUSH, 1,            // Stack contains b, 1
                SUB,                // Stack contains b - 1
                STORE, 1,           // Save in b, meaning b = b - 1
                JMP, 12,            // Go back to the start of the loop
                HALT
        );
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 37);
        assertStackIsEmpty(cpu);
        assertVariableValues(cpu, 6, 0, 24);
    }

    @Test
    public void testMaxAB() throws Exception {
        /**
         * We're going to create a function that returns the maximum of its two arguments.
         *
         * The algorithm is obviously:
         *
         * int max(int a, int b) {
         *     if (a > b) {
         *         return a;
         *     } else {
         *         return b;
         *     }
         * }
         *
         *
         */
        CPU cpu = new CPU(
                PUSH, 6,        // Push the first argument
                PUSH, 4,        // Push the second argument
                CALL, 7,        // Call "max"
                HALT,
                // Here is address 7, the start of "max" function
                STORE, 1,       // Store b in local variable 1; the stack now contains [a]
                STORE, 0,       // Store a in local variable 0; the stack is now empty
                LOAD, 0,       // The stack now contains [a]
                LOAD, 1,       // The stack now contains [a, b]
                ISGE,           // The stack now contains [a > b]
                JIF, 21,        // If the top of the stack is true (a > b), jump to the "if" path
                LOAD, 1,        // "else" path: load b on the stack
                RET,
                // Here is address 23
                LOAD, 0,        // "if" path: load a on the stack
                RET
        );
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 7);
        assertStackContains(cpu, 6);
    }
}
