package io.github.lindelwa.parser;

import io.github.lindelwa.exceptions.CommandException;
import io.github.lindelwa.exceptions.SyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Reads an assembly language command, parses it, and provides convenient
 * access to the command's components (fields & registers)
 */
public class Parser {
    private List<String> program = new ArrayList<>();
    private int programCounter = 0;
    private String currentCommand = "";

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
