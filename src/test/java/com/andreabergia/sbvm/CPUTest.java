package com.andreabergia.sbvm;

import org.junit.Test;

import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.HALT;
import static com.andreabergia.sbvm.Instructions.PUSH;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CPUTest {
    @Test
    public void testEmptyProgramDoesNothing() {
        CPU cpu = new CPU(HALT);
        cpu.step();
        assertEquals(1, cpu.getInstructionAddress());
        assertTrue(cpu.isHalted());
        assertStackIsEmpty(cpu);
    }

    @Test
    public void testPushAndThenHalt() {
        CPU cpu = new CPU(PUSH, 42, HALT);
        cpu.step();
        assertEquals(2, cpu.getInstructionAddress());
        assertFalse(cpu.isHalted());
        assertStackContains(cpu, 42);
    }

    @Test
    public void testPushPushAndThenHalt() {
        CPU cpu = new CPU(PUSH, 42, PUSH, 68, HALT);
        cpu.run();
        assertEquals(5, cpu.getInstructionAddress());
        assertTrue(cpu.isHalted());
        assertStackContains(cpu, 68, 42);
    }

    @Test(expected = InvalidProgramException.class)
    public void testInvalidProgram() {
        CPU cpu = new CPU(PUSH);
        cpu.step();
    }

    @Test
    public void testAddTwoNumbers() {
        CPU cpu = new CPU(PUSH, 1, PUSH, 2, ADD, HALT);
        cpu.run();
        assertEquals(6, cpu.getInstructionAddress());
        assertTrue(cpu.isHalted());
        assertStackContains(cpu, 3);
    }

    private void assertStackIsEmpty(CPU cpu) {
        assertTrue(cpu.getStack().isEmpty());
    }

    private void assertStackContains(CPU cpu, int... expectedContent) {
        assertEquals(expectedContent.length, cpu.getStack().size());
        assertArrayEquals(expectedContent, cpu.getStack().getContentAsArray());
    }
}
