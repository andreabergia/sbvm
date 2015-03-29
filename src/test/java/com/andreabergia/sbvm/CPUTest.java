package com.andreabergia.sbvm;

import com.google.common.primitives.Ints;
import org.junit.Test;

import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.DIV;
import static com.andreabergia.sbvm.Instructions.HALT;
import static com.andreabergia.sbvm.Instructions.MUL;
import static com.andreabergia.sbvm.Instructions.PUSH;
import static com.andreabergia.sbvm.Instructions.SUB;
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
        cpu.step();
        assertTrue(cpu.isHalted());
    }

    @Test
    public void testPushPushAndThenHalt() {
        CPU cpu = new CPU(PUSH, 42, PUSH, 68, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 5);
        assertStackContains(cpu, 68, 42);
    }

    @Test(expected = InvalidProgramException.class)
    public void testPushShouldBeFollowedByAWord() {
        CPU cpu = new CPU(PUSH);
        cpu.step();
    }

    @Test
    public void testAddTwoNumbers() {
        CPU cpu = new CPU(PUSH, 1, PUSH, 2, ADD, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 3);
    }

    @Test(expected = InvalidProgramException.class)
    public void testAddNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(ADD, HALT);
        cpu.run();
    }

    @Test
    public void testSubTwoNumbers() {
        CPU cpu = new CPU(PUSH, 1, PUSH, 2, SUB, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, -1);
    }

    @Test(expected = InvalidProgramException.class)
    public void testSubNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(SUB, HALT);
        cpu.run();
    }

    @Test
    public void testMulTwoNumbers() {
        CPU cpu = new CPU(PUSH, 2, PUSH, 5, MUL, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 10);
    }

    @Test(expected = InvalidProgramException.class)
    public void testMulNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(MUL, HALT);
        cpu.run();
    }

    @Test
    public void testDivTwoNumbers() {
        CPU cpu = new CPU(PUSH, 8, PUSH, 2, DIV, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 4);
    }

    @Test(expected = InvalidProgramException.class)
    public void testDivNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(DIV, HALT);
        cpu.run();
    }

    private void assertProgramRunsToHaltAndInstructionAddressIs(CPU cpu, int address) {
        cpu.run();
        assertEquals(address, cpu.getInstructionAddress());
        assertTrue(cpu.isHalted());
    }

    private void assertStackIsEmpty(CPU cpu) {
        assertTrue(cpu.getStack().isEmpty());
    }

    private void assertStackContains(CPU cpu, int... expectedContent) {
        assertEquals(expectedContent.length, cpu.getStack().size());
        assertArrayEquals(expectedContent, Ints.toArray(cpu.getStack()));
    }
}
