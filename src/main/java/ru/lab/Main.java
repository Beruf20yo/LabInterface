package ru.lab;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("TestDir");
        ConsoleApp consoleApp = new ConsoleApp(file.getAbsolutePath());
        consoleApp.checkCommand();
    }
}