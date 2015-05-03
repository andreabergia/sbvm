package com.andreabergia.sbvm;

import com.google.common.primitives.Ints;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class CPUAssertions {
    public static void assertProgramRunsToHaltAndInstructionAddressIs(CPU cpu, int expectedAddress) {
        cpu.run();
        assertEquals("The CPU should have finished at the expected address",
                expectedAddress, cpu.getInstructionAddress());
        assertTrue("The CPU should be halted", cpu.isHalted());
    }

    public static void assertStackIsEmpty(CPU cpu) {
        assertTrue("The stack should be empty", cpu.getStack().isEmpty());
    }

    public static void assertStackContains(CPU cpu, int... expectedContent) {
        assertEquals("The stack should have the expected length",
                expectedContent.length, cpu.getStack().size());
        assertArrayEquals("The stack content should be as expected",
                expectedContent, Ints.toArray(cpu.getStack()));
    }

    public static void assertVariableValues(CPU cpu, int... expectedVariableValues) {
        Frame frame = cpu.getCurrentFrame();
        for (int varNumber = 0; varNumber < expectedVariableValues.length; varNumber++) {
            int expectedVariableValue = expectedVariableValues[varNumber];
            assertEquals(String.format("Variable %d should have the expected value", varNumber),
                    expectedVariableValue, frame.getVariable(varNumber));
        }
    }
}
