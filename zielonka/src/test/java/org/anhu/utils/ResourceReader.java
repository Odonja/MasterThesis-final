package org.anhu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceReader {

    public static String read(final String resourcename) throws IOException {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(ResourceReader.class.getClassLoader().getResourceAsStream(resourcename)));
        assertNotNull(rdr);
        String line = rdr.readLine();
        rdr.close();
        return line;
    }
}
