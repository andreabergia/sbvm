package com.andreabergia.sbvm;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CPUStackTest {
    private final CPUStack stack = new CPUStack();

    @Test
    public void testNewStackShouldBeEmpty() {
        assertTrue(stack.isEmpty());
    }

    @Test
    public void testEmptyStackHasZeroSize() throws Exception {
        assertEquals(0, stack.size());
    }

    @Test
    public void testPrintEmptyStack() throws Exception {
        assertEquals("Stack: 0 items: []", stack.print());
    }

    @Test
    public void testContentOfEmptyStackIsEmpty() throws Exception {
        assertEquals(0, stack.getContentAsArray().length);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCannotPopFromEmptyStack() throws Exception {
        stack.pop();
    }


    @Test
    public void testStackWithOneItemPushedIsNotEmpty() throws Exception {
        stack.push(42);
        assertFalse(stack.isEmpty());
    }

    @Test
    public void testStackWithOneItemPushedHasSizeOne() throws Exception {
        stack.push(42);
        assertEquals(1, stack.size());
        assertEquals(42, stack.pop());
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    public void testCanPopOnePushedItem() throws Exception {
        stack.push(42);
        assertEquals(42, stack.pop());
    }

    @Test
    public void testAfterPoppingAllItemsStackIsEmpty() throws Exception {
        stack.push(42);
        stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testCannotPopAfterStackEnd() throws Exception {
        stack.push(42);
        assertEquals(42, stack.pop());
        stack.pop();
    }

    @Test
    public void testPrintStackWithOneItem() throws Exception {
        stack.push(10);
        assertEquals("Stack: 1 items: [10]", stack.print());
    }

    @Test
    public void testStackWithOneItemPushedHasCorrectContent() throws Exception {
        stack.push(42);
        assertArrayEquals(new int[]{42}, stack.getContentAsArray());
    }

    @Test
    public void testTwoItemsArePoppedBackInOrder() throws Exception {
        stack.push(42);
        stack.push(43);
        assertEquals(43, stack.pop());
        assertEquals(42, stack.pop());
    }

    @Test
    public void testPrintStackWithTwoItems() throws Exception {
        stack.push(10);
        stack.push(20);
        assertEquals("Stack: 2 items: [20, 10]", stack.print());
    }
}
