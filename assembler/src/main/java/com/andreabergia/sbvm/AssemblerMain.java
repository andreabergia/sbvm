package com.andreabergia.sbvm;

import org.antlr.v4.runtime.ANTLRFileStream;

import java.io.IOException;

public class AssemblerMain {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Please give the file to parse as the only argument!");
            System.exit(-1);
        }

        runProgram(args[0]);
    }

    private static void runProgram(String fileName) throws IOException {
        int[] generatedProgram = ProgramVisitor.generateProgram(new ANTLRFileStream(fileName));
        CPU cpu = new CPU(generatedProgram);
        cpu.run();

        System.out.println("After running, the cpu stack contains: " + cpu.getStack());
        System.out.println("After running, the cpu local frame contains: " + cpu.getCurrentFrame().getVariables());
    }
}
