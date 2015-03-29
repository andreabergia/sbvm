package com.andreabergia.sbvm;

import com.google.common.primitives.Ints;
import org.junit.Test;

import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.AND;
import static com.andreabergia.sbvm.Instructions.DIV;
import static com.andreabergia.sbvm.Instructions.DUP;
import static com.andreabergia.sbvm.Instructions.HALT;
import static com.andreabergia.sbvm.Instructions.MUL;
import static com.andreabergia.sbvm.Instructions.NOT;
import static com.andreabergia.sbvm.Instructions.OR;
import static com.andreabergia.sbvm.Instructions.PUSH;
import static com.andreabergia.sbvm.Instructions.SUB;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CPUTest {
    // Basic instructions

    @Test
    public void testEmptyProgramDoesNothing() {
        CPU cpu = new CPU(HALT);
        cpu.step();
        assertEquals(1, cpu.getInstructionAddress());
        assertTrue(cpu.isHalted());
        assertStackIsEmpty(cpu);
    }

    // Stack instructions

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

    // Arithmetic instructions

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

    // Boolean instructions

    @Test
    public void testUnaryNotTrue() {
        CPU cpu = new CPU(PUSH, 1, NOT, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 4);
        assertStackContains(cpu, 0);
    }

    @Test
    public void testUnaryNotFalse() {
        CPU cpu = new CPU(PUSH, 0, NOT, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 4);
        assertStackContains(cpu, 1);
    }

    @Test(expected = InvalidProgramException.class)
    public void testNotNeedsOneItemOnTheStack() {
        CPU cpu = new CPU(NOT, HALT);
        cpu.run();
    }

    @Test
    public void testAndTrueTrue() {
        CPU cpu = new CPU(PUSH, 1, PUSH, 1, AND, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 1);
    }

    @Test(expected = InvalidProgramException.class)
    public void testAndNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(AND, HALT);
        cpu.run();
    }

    @Test
    public void testOrTrueFalse() {
        CPU cpu = new CPU(PUSH, 1, PUSH, 0, OR, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 1);
    }

    @Test(expected = InvalidProgramException.class)
    public void testOrNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(OR, HALT);
        cpu.run();
    }

    // Utility methods

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
