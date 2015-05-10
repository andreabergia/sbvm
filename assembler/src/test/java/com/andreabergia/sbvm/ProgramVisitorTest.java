package com.andreabergia.sbvm;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;

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
import static org.junit.Assert.assertArrayEquals;

public class ProgramVisitorTest {
    private int[] parseProgram(String source) {
        return ProgramVisitor.generateProgram(new ANTLRInputStream(source));
    }

    @Test
    public void testTrivialProgram() throws Exception {
        int[] program = parseProgram("HALT\n");
        assertArrayEquals(new int[]{HALT}, program);
    }

    @Test
    public void testAllSimpleInstructions() throws Exception {
        int[] program = parseProgram("" +
                "ADD\n" +
                "SUB\n" +
                "MUL\n" +
                "DIV\n" +
                "NOT\n" +
                "AND\n" +
                "OR\n" +
                "POP\n" +
                "DUP\n" +
                "ISEQ\n" +
                "ISGE\n" +
                "ISGT\n" +
                "RET\n");
        assertArrayEquals(
                new int[]{ADD, SUB, MUL, DIV, NOT, AND, OR, POP, DUP, ISEQ, ISGE, ISGT, RET},
                program);
    }

    @Test
    public void testPushWithArgument() throws Exception {
        int[] program = parseProgram("PUSH   123\n");
        assertArrayEquals(new int[]{PUSH, 123}, program);
    }

    @Test
    public void testLoadAndStore() throws Exception {
        int[] program = parseProgram("" +
                "LOAD  100\n" +
                "STORE 101\n");
        assertArrayEquals(new int[]{LOAD, 100, STORE, 101}, program);
    }

    @Test
    public void testJmpWithLabel() throws Exception {
        int[] program = parseProgram("" +
                "JMP afterEnd\n" +
                "HALT\n" +
                "afterEnd:\n" +
                "PUSH 42\n");
        assertArrayEquals(new int[]{JMP, 3, HALT, PUSH, 42}, program);
    }

    @Test
    public void testJifCallWithLabel() throws Exception {
        int[] program = parseProgram("" +
                "JIF aLabel\n" +
                "CALL anotherLabel\n" +
                "HALT\n" +
                "aLabel:\n" +
                "anotherLabel:\n" +
                "PUSH 43\n");
        assertArrayEquals(new int[]{JIF, 5, CALL, 5, HALT, PUSH, 43}, program);
    }

    @Test(expected = InvalidProgramException.class)
    public void testLabelNotFound() throws Exception {
        parseProgram("JMP noLabel\n");
    }

    @Test
    public void testCommentsAreIgnored() throws Exception {
        int[] program = parseProgram("" +
                "// I am a comment!\n" +
                "HALT // Comment inline\n");
        assertArrayEquals(new int[]{HALT}, program);
    }
}
