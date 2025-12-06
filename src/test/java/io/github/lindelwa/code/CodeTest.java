package io.github.lindelwa.code;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeTest {
    @Test
    public void destTest() {
        assertEquals("001", Code.dest("M"));
        assertEquals("010", Code.dest("D"));
        assertEquals("011", Code.dest("MD"));
        assertEquals("100", Code.dest("A"));
        assertEquals("101", Code.dest("AM"));
        assertEquals("110", Code.dest("AD"));
        assertEquals("111", Code.dest("AMD"));
        assertEquals("000", Code.dest("m"));
        assertEquals("000", Code.dest("ad"));
        assertEquals("000", Code.dest(null));
    }

    @Test
    public void jumpTest() {
        assertEquals("001", Code.dest("JGT"));
        assertEquals("010", Code.dest("JEQ"));
        assertEquals("011", Code.dest("JGE"));
        assertEquals("100", Code.dest("JLT"));
        assertEquals("101", Code.dest("JNE"));
        assertEquals("110", Code.dest("JLE"));
        assertEquals("111", Code.dest("JMP"));
        assertEquals("000", Code.dest("JUMP"));
        assertEquals("000", Code.dest("goto"));
        assertEquals("000", Code.dest(null));
    }

    @Test
    public void compTest() {
        assertEquals("0101010", Code.comp("0"));
        assertEquals("0111111", Code.comp("1"));
        assertEquals("0111010", Code.comp("-1"));
        assertEquals("0001100", Code.comp("D"));
        assertEquals("0110000", Code.comp("A"));
        assertEquals("1110000", Code.comp("M"));
        assertEquals("0001111", Code.comp("!D"));
        assertEquals("0110001" ,Code.comp("!A"));
        assertEquals("1110001" ,Code.comp("!M"));
        assertEquals("0001111" ,Code.comp("-D"));
        assertEquals("0110011" ,Code.comp("-A"));
        assertEquals("1110011" ,Code.comp("-M"));
        assertEquals("0011111", Code.comp("D+1"));
        assertEquals("0110111", Code.comp("A+1"));
        assertEquals("1110111", Code.comp("M+1"));
        assertEquals("0001110", Code.comp("D-1"));
        assertEquals("0110010", Code.comp("A-1"));
        assertEquals("1110010", Code.comp("M-1"));
        assertEquals("0000010", Code.comp("D+A"));
        assertEquals("1000010", Code.comp("D+M"));
        assertEquals("0010011", Code.comp("D-A"));
        assertEquals("1010011", Code.comp("D-M"));
        assertEquals("0000111", Code.comp("A-D"));
        assertEquals("1000111", Code.comp("M-D"));
        assertEquals("0000000", Code.comp("D&A"));
        assertEquals("1000000", Code.comp("D&M"));
        assertEquals("0010101", Code.comp("D|A"));
        assertEquals("1010101", Code.comp("D|M"));
        assertEquals("0000000", Code.comp("M+D"));
        assertEquals("0000000", Code.comp("x"));
        assertEquals("0000000", Code.comp("D-2"));
        assertEquals("0000000", Code.comp(null));
    }
}
