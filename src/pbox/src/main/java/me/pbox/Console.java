package me.pbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Console {
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public static void println(String line) {
        System.out.println(line);
        System.out.flush();
    }

    public static String readln() {
        try {
            return READER.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
