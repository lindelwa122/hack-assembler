package io.github.lindelwa.code;


import java.util.HashMap;
import java.util.Map;

/**
 * Translates Hack assembly language mnemonics into binary code
 */
public class Code {
    /**
     * Returns the binary code of the dest mnemonic
     * @param mnemonic
     * @return binary code of the dest mnemonic
     */
    public static String dest(String mnemonic) {
        if (mnemonic == null) return "000";

        Map<String, String> mnemonicsBinaryPairs = Map.of(
                "M", "001",
                "D", "010",
                "MD", "011",
                "A", "100",
                "AM", "101",
                "AD", "110",
                "AMD", "111"
        );

        return mnemonicsBinaryPairs.getOrDefault(mnemonic, "000");
    }

    /**
     *  Returns the binary code of the jump mnemonic
     * @param mnemonic
     * @return the binary code of the jump mnemonic
     */
    public static String jump(String mnemonic) {
        if (mnemonic == null) return "000";

        Map<String, String> mnemonicsBinaryPairs = Map.of(
                "JGT", "001",
                "JEQ", "010",
                "JGE", "011",
                "JLT", "100",
                "JNE", "101",
                "JLE", "110",
                "JMP", "111"
        );

        return mnemonicsBinaryPairs.getOrDefault(mnemonic, "000");
    }

    /**
     * Returns the binary code of the comp mnemonic
     * @param mnemonic
     * @return the binary code of the comp mnemonic
     */
    public static String comp(String mnemonic) {
        if (mnemonic == null) return "0000000";

        Map<String, String> mnemonicsBinaryPairs = new HashMap<>();

        mnemonicsBinaryPairs.put("0", "0101010");
        mnemonicsBinaryPairs.put("1", "0111111");
        mnemonicsBinaryPairs.put("-1", "0111010");
        mnemonicsBinaryPairs.put("D", "0001100");
        mnemonicsBinaryPairs.put("A", "0110000");
        mnemonicsBinaryPairs.put("M", "1110000");
        mnemonicsBinaryPairs.put("!D", "0001111");
        mnemonicsBinaryPairs.put("!A", "0110001");
        mnemonicsBinaryPairs.put("!M", "1110001");
        mnemonicsBinaryPairs.put("-D", "0001111");
        mnemonicsBinaryPairs.put("-A", "0110011");
        mnemonicsBinaryPairs.put("-M", "1110011");
        mnemonicsBinaryPairs.put("D+1", "0011111");
        mnemonicsBinaryPairs.put("A+1", "0110111");
        mnemonicsBinaryPairs.put("M+1", "1110111");
        mnemonicsBinaryPairs.put("D-1", "0001110");
        mnemonicsBinaryPairs.put("A-1", "0110010");
        mnemonicsBinaryPairs.put("M-1", "1110010");
        mnemonicsBinaryPairs.put("D+A", "0000010");
        mnemonicsBinaryPairs.put("D+M", "1000010");
        mnemonicsBinaryPairs.put("D-A", "0010011");
        mnemonicsBinaryPairs.put("D-M", "1010011");
        mnemonicsBinaryPairs.put("A-D", "0000111");
        mnemonicsBinaryPairs.put("M-D", "1000111");
        mnemonicsBinaryPairs.put("D&A", "0000000");
        mnemonicsBinaryPairs.put("D&M", "1000000");
        mnemonicsBinaryPairs.put("D|A", "0010101");
        mnemonicsBinaryPairs.put("D|M", "1010101");

        return mnemonicsBinaryPairs.getOrDefault(mnemonic, "0000000");
    }
}
