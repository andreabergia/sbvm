package com.andreabergia.sbvm;

import org.junit.Test;

import static com.andreabergia.sbvm.CPUAssertions.assertProgramRunsToHaltAndInstructionAddressIs;
import static com.andreabergia.sbvm.CPUAssertions.assertStackIsEmpty;
import static com.andreabergia.sbvm.CPUAssertions.assertVariableValues;
import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.HALT;
import static com.andreabergia.sbvm.Instructions.ISGE;
import static com.andreabergia.sbvm.Instructions.JIF;
import static com.andreabergia.sbvm.Instructions.JMP;
import static com.andreabergia.sbvm.Instructions.LOAD;
import static com.andreabergia.sbvm.Instructions.NOT;
import static com.andreabergia.sbvm.Instructions.PUSH;
import static com.andreabergia.sbvm.Instructions.STORE;
import static com.andreabergia.sbvm.Instructions.SUB;

public class CompleteProgramsTest {
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
                STORE, 2,           // Save in total
                LOAD, 1,            // Stack contains b
                PUSH, 1,            // Stack contains b, 1
                SUB,                // Stack contains b - 1
                STORE, 1,           // Save in b
                JMP, 12,
                HALT
        );
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 37);
        assertStackIsEmpty(cpu);
        assertVariableValues(cpu, 6, 0, 24);
    }
}
