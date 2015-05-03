package com.andreabergia.sbvm;

import com.andreabergia.sbvm.antlr.SbvmBaseVisitor;
import com.andreabergia.sbvm.antlr.SbvmLexer;
import com.andreabergia.sbvm.antlr.SbvmParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramVisitor extends SbvmBaseVisitor<Void> {
    private static final class UnresolvedAddress {
        private final int position;
        private final String label;

        public UnresolvedAddress(String label, int position) {
            this.label = label;
            this.position = position;
        }
    }

    private static final int UNRESOLVED_JUMP_ADDRESS = -1;
    private final List<Integer> program = new ArrayList<>();
    private final List<UnresolvedAddress> labelsToResolve = new ArrayList<>();
    private final Map<String, Integer> labelsAddresses = new HashMap<>();


    private int getCurrentAddress() {return program.size();}

    @Override
    public Void visitLabel(SbvmParser.LabelContext ctx) {
        // When a label is found, saves the current address for later
        String labelText = ctx.IDENTIFIER().getText();
        labelsAddresses.put(labelText, getCurrentAddress());
        return null;
    }

    @Override
    public Void visitHalt(SbvmParser.HaltContext ctx) {
        // When a HALT instruction is found, adds a HALT instruction to the program
        program.add(Instructions.HALT);
        return null;
    }

    @Override
    public Void visitPush(SbvmParser.PushContext ctx) {
        visitOneArgumentInstruction(ctx.NUMBER(), Instructions.PUSH);
        return null;
    }

    @Override
    public Void visitAdd(SbvmParser.AddContext ctx) {
        program.add(Instructions.ADD);
        return null;
    }

    @Override
    public Void visitSub(SbvmParser.SubContext ctx) {
        program.add(Instructions.SUB);
        return null;
    }

    @Override
    public Void visitMul(SbvmParser.MulContext ctx) {
        program.add(Instructions.MUL);
        return null;
    }

    @Override
    public Void visitDiv(SbvmParser.DivContext ctx) {
        program.add(Instructions.DIV);
        return null;
    }

    @Override
    public Void visitNot(SbvmParser.NotContext ctx) {
        program.add(Instructions.NOT);
        return null;
    }

    @Override
    public Void visitAnd(SbvmParser.AndContext ctx) {
        program.add(Instructions.AND);
        return null;
    }

    @Override
    public Void visitOr(SbvmParser.OrContext ctx) {
        program.add(Instructions.OR);
        return null;
    }

    @Override
    public Void visitPop(SbvmParser.PopContext ctx) {
        program.add(Instructions.POP);
        return null;
    }

    @Override
    public Void visitDup(SbvmParser.DupContext ctx) {
        program.add(Instructions.DUP);
        return null;
    }

    @Override
    public Void visitIseq(SbvmParser.IseqContext ctx) {
        program.add(Instructions.ISEQ);
        return null;
    }

    @Override
    public Void visitIsge(SbvmParser.IsgeContext ctx) {
        program.add(Instructions.ISGE);
        return null;
    }

    @Override
    public Void visitIsgt(SbvmParser.IsgtContext ctx) {
        program.add(Instructions.ISGT);
        return null;
    }

    @Override
    public Void visitJmp(SbvmParser.JmpContext ctx) {
        visitUnresolvedJump(ctx.IDENTIFIER(), Instructions.JMP);
        return null;
    }

    @Override
    public Void visitJif(SbvmParser.JifContext ctx) {
        visitUnresolvedJump(ctx.IDENTIFIER(), Instructions.JIF);
        return null;
    }

    @Override
    public Void visitLoad(SbvmParser.LoadContext ctx) {
        visitOneArgumentInstruction(ctx.NUMBER(), Instructions.LOAD);
        return null;
    }

    @Override
    public Void visitStore(SbvmParser.StoreContext ctx) {
        visitOneArgumentInstruction(ctx.NUMBER(), Instructions.STORE);
        return null;
    }

    @Override
    public Void visitCall(SbvmParser.CallContext ctx) {
        visitUnresolvedJump(ctx.IDENTIFIER(), Instructions.CALL);
        return null;
    }

    @Override
    public Void visitRet(SbvmParser.RetContext ctx) {
        program.add(Instructions.RET);
        return null;
    }


    private void visitOneArgumentInstruction(TerminalNode numer, int instruction) {
        int value = Integer.valueOf(numer.getText());
        program.add(instruction);
        program.add(value);
    }

    private void visitUnresolvedJump(TerminalNode identifier, int instruction) {
        // Add the given instruction, save the unresolved label and add a placeholder for the jump address
        program.add(instruction);
        String labelText = identifier.getText();
        labelsToResolve.add(new UnresolvedAddress(labelText, getCurrentAddress()));
        program.add(UNRESOLVED_JUMP_ADDRESS);
    }


    /**
     * Returns the current program in a format suitable for the CPU execution.
     */
    public int[] generateProgram() {
        resolveLabels();

        // Horrible code to convert a List<Integer> to an int[]
        int[] result = new int[this.program.size()];
        for (int i = 0; i < this.program.size(); i++) {
            result[i] = this.program.get(i);
        }
        return result;
    }

    /**
     * Transforms all the unresolved labels into correct addresses.
     */
    private void resolveLabels() {
        for (UnresolvedAddress unresolvedAddress : labelsToResolve) {
            // Map the jump to its real address, by checking the label's address
            @Nullable Integer destination = labelsAddresses.get(unresolvedAddress.label);
            if (destination == null) {
                throw new InvalidProgramException("Unresolved label " + unresolvedAddress.label);
            }

            // Replace the placeholder with the jump address
            assert program.get(unresolvedAddress.position) == UNRESOLVED_JUMP_ADDRESS;
            program.set(unresolvedAddress.position, destination);
        }

        // Clean up
        labelsToResolve.clear();
    }


    /**
     * Generates a program from a given parser, or throws an exception if the program is invalid.
     */
    public static int[] generateProgram(SbvmParser parser) throws InvalidProgramException {
        ProgramVisitor programVisitor = new ProgramVisitor();
        programVisitor.visit(parser.program());
        return programVisitor.generateProgram();
    }

    /**
     * Generates a program from a given ANTLR input, or throws an exception if the program is invalid.
     */
    public static int[] generateProgram(CharStream input) throws InvalidProgramException {
        SbvmLexer lexer = new SbvmLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SbvmParser parser = new SbvmParser(tokenStream);
        return generateProgram(parser);
    }
}
