package converter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Converter converter = new Converter();
        for (String file : args) {
            converter.convert(getInput(file));
            System.out.println("Converting result of " + file + ":");
            System.out.println("\n(In document form)\n");
            System.out.println(converter.getDocument());
            System.out.println("\n(In element hierarchy form)\n");
            System.out.println(converter.getNodeInfo());
        }

    }

    static String getInput(String file) {
        StringBuilder text = new StringBuilder();
        try (Scanner reader = new Scanner(new File(file))) {
            while (reader.hasNext()) { text.append(reader.nextLine()); }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return text.toString();
    }
}