package com.andreabergia.sbvm;

import com.google.common.primitives.Ints;
import org.junit.Test;

import static com.andreabergia.sbvm.Instructions.ADD;
import static com.andreabergia.sbvm.Instructions.AND;
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
import static com.andreabergia.sbvm.Instructions.STORE;
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

    @Test
    public void testPop() {
        CPU cpu = new CPU(PUSH, 42, POP, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 4);
        assertStackIsEmpty(cpu);
    }

    @Test(expected = InvalidProgramException.class)
    public void testPopNeedsAnItemOnTheStack() {
        CPU cpu = new CPU(POP);
        cpu.step();
    }

    @Test
    public void testDup() {
        CPU cpu = new CPU(PUSH, 42, DUP, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 4);
        assertStackContains(cpu, 42, 42);
    }

    @Test(expected = InvalidProgramException.class)
    public void testDupNeedsAnItemOnTheStack() {
        CPU cpu = new CPU(DUP);
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

    // Comparison instructions

    @Test
    public void testIsEqualsFalse() {
        CPU cpu = new CPU(PUSH, 8, PUSH, 2, ISEQ, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 0);
    }

    @Test
    public void testIsEqualsTrue() {
        CPU cpu = new CPU(PUSH, 2, PUSH, 2, ISEQ, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 1);
    }

    @Test(expected = InvalidProgramException.class)
    public void testIsEqualsNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(ISEQ, HALT);
        cpu.run();
    }

    @Test
    public void testIsGreaterEqualsTrue() {
        CPU cpu = new CPU(PUSH, 3, PUSH, 2, ISGE, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 1);
    }

    @Test
    public void testIsGreaterEqualsFalse() {
        CPU cpu = new CPU(PUSH, 1, PUSH, 2, ISGE, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 0);
    }

    @Test(expected = InvalidProgramException.class)
    public void testIsGreaterEqualsNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(ISGE, HALT);
        cpu.run();
    }

    @Test
    public void testIsGreaterFalse() {
        CPU cpu = new CPU(PUSH, 1, PUSH, 2, ISGT, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 0);
    }

    @Test
    public void testIsGreaterTrue() {
        CPU cpu = new CPU(PUSH, 3, PUSH, 2, ISGT, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 6);
        assertStackContains(cpu, 1);
    }

    @Test(expected = InvalidProgramException.class)
    public void testIsGreaterNeedsTwoItemsOnTheStack() {
        CPU cpu = new CPU(ISGT, HALT);
        cpu.run();
    }

    // Jumps

    @Test
    public void testUnconditionalJump() {
        // address:       0    1  2     3    4
        CPU cpu = new CPU(JMP, 3, HALT, JMP, 2);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 3);
    }

    @Test(expected = InvalidProgramException.class)
    public void testJumpNeedsOneArgument() {
        CPU cpu = new CPU(JMP);
        cpu.run();
    }

    @Test
    public void testConditionalJump() {
        // address:       0     1  2    3  4    5     6  7    8  9
        CPU cpu = new CPU(PUSH, 1, JIF, 5, POP, PUSH, 0, JIF, 4, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 10);
        // If the program hit the POP, we'd have an error
    }

    @Test(expected = InvalidProgramException.class)
    public void testConditionalJumpNeedsOneArgument() {
        CPU cpu = new CPU(PUSH, 1, JIF);
        cpu.run();
    }

    @Test(expected = InvalidProgramException.class)
    public void testJumpNeedsOneItemOnTheStack() {
        CPU cpu = new CPU(JIF, 0, HALT);
        cpu.run();
    }

    // Load and Store

    @Test
    public void testLoadVariableNotInitialized() {
        CPU cpu = new CPU(LOAD, 0, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 3);
        assertStackContains(cpu, 0);
    }

    @Test
    public void testStoreVariable() {
        CPU cpu = new CPU(PUSH, 42, STORE, 0, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 5);
        assertStackIsEmpty(cpu);
        assertVariableValues(cpu, 42);
    }

    @Test
    public void testStoreAndLoadVariable() {
        CPU cpu = new CPU(PUSH, 42, STORE, 0, LOAD, 0, HALT);
        assertProgramRunsToHaltAndInstructionAddressIs(cpu, 7);
        assertStackContains(cpu, 42);
        assertVariableValues(cpu, 42);
    }

    @Test(expected = InvalidProgramException.class)
    public void testLoadNeedsOneArgument() {
        CPU cpu = new CPU(LOAD);
        cpu.run();
    }

    @Test(expected = InvalidProgramException.class)
    public void testStoreNeedsOneArgument() {
        CPU cpu = new CPU(STORE);
        cpu.run();
    }

    @Test(expected = InvalidProgramException.class)
    public void testStoreNeedsOneItemOnTheStack() {
        CPU cpu = new CPU(STORE, 0, HALT);
        cpu.run();
    }

    // Utility methods

    private void assertProgramRunsToHaltAndInstructionAddressIs(CPU cpu, int expectedAddress) {
        cpu.run();
        assertEquals(expectedAddress, cpu.getInstructionAddress());
        assertTrue(cpu.isHalted());
    }

    private void assertStackIsEmpty(CPU cpu) {
        assertTrue(cpu.getStack().isEmpty());
    }

    private void assertStackContains(CPU cpu, int... expectedContent) {
        assertEquals(expectedContent.length, cpu.getStack().size());
        assertArrayEquals(expectedContent, Ints.toArray(cpu.getStack()));
    }

    private void assertVariableValues(CPU cpu, int... expectedVariableValues) {
        Frame frame = cpu.getCurrentFrame();
        for (int varNumber = 0; varNumber < expectedVariableValues.length; varNumber++) {
            int expectedVariableValue = expectedVariableValues[varNumber];
            assertEquals("Checking variable #" + varNumber, expectedVariableValue, frame.getVariable(varNumber));
        }
    }
}
