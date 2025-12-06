package io.github.lindelwa.parser;

import io.github.lindelwa.code.Code;
import io.github.lindelwa.exceptions.CommandException;
import io.github.lindelwa.exceptions.SyntaxException;

import java.io.*;
import java.util.*;

/**
 * Reads an assembly language command, parses it, and provides convenient
 * access to the command's components (fields & registers)
 */
public class Parser {
    private final List<String> program = new ArrayList<>();
    private int programCounter = 0;
    private String currentCommand = "";
    private final Map<String, Integer> symbolTable = new HashMap<>();

    public enum Command {
        A_COMMAND, C_COMMAND, L_COMMAND;
    }

    /**
     * Opens the input file and get ready to parse it.
     * @param filePath
     */
    public Parser(String filePath) {
        File file = new File(filePath);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                program.add(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        // Fill in the symbolTable with define symbols
        this.symbolTable.put("SP", 0);
        this.symbolTable.put("LCL", 1);
        this.symbolTable.put("ARG", 2);
        this.symbolTable.put("THIS", 3);
        this.symbolTable.put("THAT", 4);
        this.symbolTable.put("R0", 0);
        this.symbolTable.put("R1", 1);
        this.symbolTable.put("R2", 2);
        this.symbolTable.put("R3", 3);
        this.symbolTable.put("R4", 4);
        this.symbolTable.put("R5", 5);
        this.symbolTable.put("R6", 6);
        this.symbolTable.put("R7", 7);
        this.symbolTable.put("R8", 8);
        this.symbolTable.put("R9", 9);
        this.symbolTable.put("R10", 10);
        this.symbolTable.put("R11", 11);
        this.symbolTable.put("R12", 12);
        this.symbolTable.put("R13", 13);
        this.symbolTable.put("R14", 14);
        this.symbolTable.put("R15", 15);
        this.symbolTable.put("SCREEN", 16384);
        this.symbolTable.put("KBD", 24576);
    }

    private void writeOutput(String filePath, String binaryCode) {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(binaryCode + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reset() {
        this.currentCommand = "";
        this.programCounter = 0;
    }

    public void parse(String filePath) throws SyntaxException, CommandException {
        // FIRST PASS: Go through the entire program and build the symbol
        // table without generating any code.
        int romAddress = 0;
        while (this.hasMoreCommands()) {
            this.advance();

            Command commandType = this.commandType();
            if (commandType == Command.A_COMMAND
                    || commandType == Command.C_COMMAND)
            {
                romAddress++;
            }

            else if (commandType == Command.L_COMMAND) {
                String label = symbol();
                this.symbolTable.put(label, romAddress+1);
            }
        }

        this.reset();

        // SECOND PASS: Now go again through the entire program, and parse
        // each line. Each time a symbolic A-instruction is encountered,
        // namely, @Xxx where Xxx is a symbol and not a number, look up
        // Xxx in the symbol table. If the symbol is found in the table,
        // then replace its numeric meaning and complete the command's
        // translation. If the symbol is not found in the table, then it
        // must represent a new variable. To handle it, add the pair
        // (Xxx, n) to the symbol table, where n is the next available
        // RAM address, and complete the command's translation. The
        // allocated RAM addresses are consecutive numbers, starting
        // at address 16
        int ramAddress = 16;
        while (this.hasMoreCommands()) {
            this.advance();

            Command commandType = this.commandType();
            switch (commandType) {
                case A_COMMAND -> {
                    String symbol = this.symbol();
                    if (!this.isStringNumeric(symbol)
                            && !this.symbolTable.containsKey(symbol))
                    {
                        this.symbolTable.put(symbol, ramAddress);
                        symbol = Integer.toString(ramAddress);
                        ramAddress++;
                    }

                    else if (!this.isStringNumeric(symbol)) {
                        symbol = Integer.toString(ramAddress);
                    }

                    // Convert symbol (numeric) to binary string and write to output
                    int symbolAsInt = Integer.parseInt(symbol);
                    if (symbolAsInt < 0) throw new SyntaxException(
                            "the A-instruction must refer to address from 0"
                    );

                    String binaryString = Integer.toBinaryString(symbolAsInt);
                    binaryString = "0".repeat(15 - binaryString.length()) + binaryString;
                    this.writeOutput(filePath, "0" + binaryString);
                }

                case C_COMMAND -> {
                    String compBin = Code.comp(this.comp());
                    String destBin = Code.dest(this.dest());
                    String jumpBin = Code.jump(this.jump());

                    String binaryString = "111" + compBin + destBin + jumpBin;
                    this.writeOutput(filePath, binaryString);
                }
            }
        }
    }

    private boolean isStringNumeric(String s) {
        if (s == null || s.isEmpty()) return false;

        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }

        return true;
    }

    /**
     * Returns true if there any more command to execute, otherwise false.
     * @return boolean
     */
    public boolean hasMoreCommands() {
        return this.programCounter < program.size();
    }

    /**
     * Reads the next command from the input and makes it the current
     * command. Should be called only if hasMoreCommands() is true.
     * Initially, there's no current command.
     */
    public void advance() {
        this.currentCommand = this.program.get(this.programCounter).trim();
        this.programCounter++;
    }

    private String constructErrorMsg(String msg) {
        return "Instruction " + this.programCounter + ": " + msg;
    }

    /**
     * Returns the type of the current command A_COMMAND: for @Xxx where
     * Xxx is either a decimal number or a symbol
     * @return Command
     * @throws SyntaxException
     */
    public Command commandType() throws SyntaxException {
        // A instructions always start with the @ symbol
        if (this.currentCommand.startsWith("@")) {
            return Command.A_COMMAND;
        }

        // L commands are always enclosed by round brackets
        if (this.currentCommand.startsWith("(")
                && this.currentCommand.endsWith(")"))
        {
            return Command.L_COMMAND;
        }

        // C instructions always contain either a semicolon (;)
        // or an equal sign (=)
        if (this.currentCommand.contains(";")
                || this.currentCommand.contains("="))
        {
            return Command.C_COMMAND;
        }

        // Comment starts with double forward backslashes (//)
        if (this.currentCommand.startsWith("//")) return null;

        throw new SyntaxException(
                this.constructErrorMsg("instruction not recognized")
        );
    }

    /**
     * Returns the symbol or decimal number of the current command @Xxx
     * or (Xxx). Should be called only when commandType is A_COMMAND or
     * L_COMMAND
     */
    public String symbol() throws SyntaxException, CommandException {
        Command commandType = this.commandType();

        if (commandType.equals(Command.A_COMMAND)) {
            return this.currentCommand.substring(1).trim();
        }

        else if (commandType.equals(Command.L_COMMAND)) {
            return this.currentCommand.substring(
                    1,
                    this.currentCommand.length()-1
            );
        }

        else {
            throw new CommandException(
                    "symbol should invoked only when commandType() "
                            + "is A_COMMAND or L_COMMAND"
            );
        }
    }

    /**
     * Returns the dest mnemonic in current C_COMMAND (8 possibilities).
     * Should only be called when commandType() is C_COMMAND
     */
    public String dest() throws SyntaxException {
        if (!this.currentCommand.contains("=")) return null;

        String mnemonic = this.currentCommand.split("=")[0].trim();

        List<String> acceptableMnemonics = List.of(
                "M", "D", "MD", "A", "AM", "AD", "AMD"
        );

        if (!acceptableMnemonics.contains(mnemonic)) {
            throw new SyntaxException(
                    constructErrorMsg(
                            mnemonic
                                    + " is not a recognized dest."
                                    + "Did you mean to try these: "
                                    + String.join(", ", acceptableMnemonics)
                    )
            );
        }

        return mnemonic;
    }

    /**
     * Returns the comp mnemonic in the current C_COMMAND
     * (28 possibilities). Should be called only when commandType() is
     * C_COMMAND.
     */
    public String comp() throws SyntaxException {
        String mnemonic;
        if (this.currentCommand.contains("=")) {
            mnemonic = this.currentCommand.split("=")[1].trim();
        }

        else {
            mnemonic = this.currentCommand.split(";")[0].trim();
        }

        List<String> acceptableMnemonics = List.of(
                "0", "1", "-1", "D", "A", "!D", "!A", "-D", "-A", "D+1",
                "A+1", "D-1", "A-1", "D+A", "D-A", "A-D", "D&A", "D|A",
                "M", "!M", "-M", "M+1", "M-1", "D+M", "D-M", "M-D", "D&M",
                "D|M"
        );

        if (!acceptableMnemonics.contains(mnemonic)) {
            throw new SyntaxException(
                    constructErrorMsg(
                            mnemonic
                                    + " is not a recognized comp."
                                    + "Did you mean to try these: "
                                    + String.join(", ", acceptableMnemonics)
                    )
            );
        }

        return mnemonic;
    }

    /**
     * Returns the jump mnemonic in the current C_COMMAND
     * (8 possibilities). Should be called when commandType() is C_COMMAND.
     */
    public String jump() throws SyntaxException {
        if (!this.currentCommand.contains(";")) return null;

        String mnemonic = this.currentCommand.split(";")[1].trim();

        List<String> acceptableMnemonics = List.of(
                "JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP"
        );

        if (!acceptableMnemonics.contains(mnemonic)) {
            throw new SyntaxException(
                    constructErrorMsg(
                            mnemonic
                                    + " is not a recognized jump."
                                    + "Did you mean to try these: "
                                    + String.join(", ", acceptableMnemonics)
                    )
            );
        }

        return mnemonic;
    }
}
