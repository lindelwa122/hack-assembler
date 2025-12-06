package io.github.lindelwa;

import io.github.lindelwa.exceptions.CommandException;
import io.github.lindelwa.exceptions.SyntaxException;
import io.github.lindelwa.parser.Parser;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String [] args) throws SyntaxException, CommandException {
        if (args == null || args.length < 2) {
            System.out.println("Both the input filepath and output are required");
            System.exit(1);
        }

        String resourcePath = args[0];

        URL url = Main.class.getResource(resourcePath);
        System.out.println(url);
        if (url != null) {
            try {
                Path path = Paths.get(url.toURI());
                System.out.println("Resource Path: " + path.toString());
                Parser parser = new Parser(path.toString());
                parser.parse(args[1]);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Resource not found: " + resourcePath);
        }
    }
}