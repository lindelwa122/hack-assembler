package io.github.lindelwa.parser;

import io.github.lindelwa.Main;
import io.github.lindelwa.exceptions.CommandException;
import io.github.lindelwa.exceptions.SyntaxException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    private String getPathUrl(String fileName) {
        String resourcePath = "/" + fileName;

        URL url = Main.class.getResource(resourcePath);
        System.out.println(url);
        if (url != null) {
            try {
                Path path = Paths.get(url.toURI());
                return path.toString();
            } catch (URISyntaxException e) {
                System.err.println("Error occurred while getting: " + resourcePath);
                return null;
            }
        } else {
            System.err.println("Resource not found: " + resourcePath);
            return null;
        }
    }

    public void testTranslation(String fileName) throws SyntaxException, CommandException {
        String asmPath = this.getPathUrl(fileName + ".asm");
        assert asmPath != null;

        String expectedHackPath = this.getPathUrl(fileName + ".hack");
        assert expectedHackPath != null;

        Parser parser = new Parser(asmPath);
        parser.parse(fileName + ".hack");

        File testFile = new File(fileName + ".hack");

        // Store the resulting machine code from the Parser
        List<String> resultingHack = new ArrayList<>();
        try (Scanner scanner = new Scanner(testFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                resultingHack.add(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        // Clean up: Delete the file (this is not ignored)
        testFile.delete();

        // Read the expected hack machine code for comparison
        File expectedFile = new File(expectedHackPath);
        try (Scanner scanner = new Scanner(expectedFile)) {
            int lineCounter = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                assertEquals(line, resultingHack.get(lineCounter));
                lineCounter++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    @Test
    public void testAddTranslation() throws SyntaxException, CommandException {
        this.testTranslation("Add");
    }

    @Test
    public void testMaxTranslation() throws SyntaxException, CommandException {
        this.testTranslation("Max");
    }

    @Test
    public void testPongTranslation() throws SyntaxException, CommandException {
        this.testTranslation("Pong");
    }

    @Test
    public void testRectTranslation() throws SyntaxException, CommandException {
        this.testTranslation("Rect");
    }
}
