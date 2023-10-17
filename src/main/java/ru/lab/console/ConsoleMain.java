package ru.lab.console;

import java.io.File;

public class ConsoleMain {
    public static void main(String[] args) {
        File file = new File("C://Windows/System32/drivers/etc");
        ConsoleApp consoleApp = new ConsoleApp(file.getPath());
        consoleApp.checkCommand();
    }
}