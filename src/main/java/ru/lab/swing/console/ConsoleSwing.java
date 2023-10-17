package ru.lab.swing.console;

import ru.lab.enums.TypeAction;
import ru.lab.enums.TypeCopy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConsoleSwing {
    private final List<TypeAction> typeCommand = new ArrayList<>(
            List.of(TypeAction.VIEW, TypeAction.CD, TypeAction.COPY,
                    TypeAction.HELP, TypeAction.EXIT, TypeAction.COPY_ALG));
    private JTextField command;
    private JLabel dirName;
    private JButton makeCommand;
    private JPanel MainPanel;
    private JTextArea dirFiles;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ConsoleSwing");
        frame.setContentPane(new ConsoleSwing().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(700,500);
    }

    private final File file = new File("TestDir");
    private String dirFromUrl = file.getPath();


public ConsoleSwing() {
    dirName.setText(dirFromUrl);

    makeCommand.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fullStr = command.getText();
            String[] parts = fullStr.trim().split(" ", 2);
            String command = parts[0].replace(" ", "").toLowerCase();
            switch (command) {
                case "copy" -> checkCopyType(parts[1]);
                case "view" -> viewAll();
                case "cd" -> cdUrl(parts[1]);
                case "copyalg" -> copyAlgorithm();
                case "!help" -> {
                    showAllCommands();
                }
                case "exit" -> {
                    return;
                }
                default -> dirFiles.setText("Ошибка ввода команды " + command + "," +
                        " введите !help, чтобы увидеть список возможных команд");
            }
        }
    });
}
    private void copyAlgorithm() {
    StringBuilder sb = new StringBuilder();
    sb.append("""
                Копировать можно как файлы так и каталоги:
                  Для копирования по имени:
                   -Абсолютный путь: copy -byname example.txt D://Example
                   -Путь относительно текущего каталога: copy -byname dirName /example""")
            .append("""
                  Для копирования по расширению:
                   -Абсолютный путь: copy -byext .txt D://Example
                   -Путь относительно текущего каталога: copy -byname dir /example\
                """);

    }

    private void showAllCommands() {
    StringBuilder sb = new StringBuilder();
    sb.append("Все команды:\n");
        for (TypeAction type : typeCommand) {
            sb.append(type.getTag()).append(" - ").append(type.getTagDescription()).append("\n");
        }
        dirFiles.setText(sb.toString());
    }
    //Просмотр файлов
    private void viewAll() {
        File folder = new File(dirFromUrl);
        File[] files = folder.listFiles();
        StringBuilder sb = new StringBuilder();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                sb.append("[dir] ").append(file.getName()).append("\n");
            } else {
                sb.append(file.getName()).append("\n");
            }
        }
        dirFiles.setText(sb.toString());
    }

    //Закрепление новой директории
    private void cdUrl(String dirFromUrl) {
        if (dirFromUrl.indexOf(':') < 0) {
            if (dirFromUrl.startsWith("/")) {
                this.dirFromUrl = this.dirFromUrl + dirFromUrl.trim();
            } else {
                this.dirFromUrl = this.dirFromUrl + "/" + dirFromUrl.trim();
            }
        } else {
            this.dirFromUrl = dirFromUrl;
        }
        dirName.setText(this.dirFromUrl);
    }

    private void checkCopyType(String parts) {
        String command = parts.trim();
        if (command.startsWith(TypeCopy.BY_NAME.getTag())) {
            copyByName(command.replace(TypeCopy.BY_NAME.getTag(), ""));
        } else if (command.startsWith(TypeCopy.BY_EXTENSION.getTag())) {
            copyByExtension(command.replace(TypeCopy.BY_EXTENSION.getTag(), ""));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Вы некорректно ввели тип копирования " +
                    "\nВозможные команды копирования:").append(TypeCopy.BY_EXTENSION.getTag()).append(" - ")
                    .append(TypeCopy.BY_EXTENSION.getTagDescription()).append(TypeCopy.BY_NAME.getTag())
                    .append(" - ").append(TypeCopy.BY_NAME.getTagDescription());
            dirFiles.setText(sb.toString());
        }
    }

    //Копирование по имени
    private void copyByName(String command) {
        String[] parts = command.trim().split(" ", 2);
        String fileName = parts[0];
        String dirToUrl = parts[1];
        File original = new File(dirFromUrl + "/" + fileName);
        Path originalPath = original.toPath();
        Path copied = getPathForCopy(dirToUrl, fileName);
        if (original.isDirectory()) {
            copyOneDirectory(originalPath, copied);
            dirFiles.setText("Успешное копирование каталога");
        } else {
            dirFiles.setText(copyOneFile(originalPath, copied) ? "Успешное копирование файла" : "Произошла ошибка");
        }

    }

    //Копирование одного файла
    private boolean copyOneFile(Path originalPath, Path copied) {
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            dirFiles.setText("Ошибка при работе с файлом: " + e.getMessage());
        }
        return false;
    }

    //Копирование по расширению
    private void copyByExtension(String command) {
        String[] parts = command.trim().split(" ", 2);
        String extension = parts[0];
        String dirToUrl = parts[1];
        File folder = new File(dirFromUrl);
        File[] files = folder.listFiles();
        if (extension.equals("dir")) {
            for (File file : Objects.requireNonNull(files)) {
                if (file.isDirectory()) {
                    copyOneDirectory(file.toPath(), getPathForCopy(dirToUrl, file.getName()));
                }
            }
        } else if (extension.startsWith(".")) {
            for (File file : Objects.requireNonNull(files)) {
                if (file.getName().endsWith(extension)) {
                    copyOneFile(file.toPath(), getPathForCopy(dirToUrl, file.getName()));
                }
            }
        }

    }

    private Path getPathForCopy(String dirToUrl, String fileName) {
        Path copied;
        if (dirToUrl.indexOf(':') < 0) {
            if (dirToUrl.startsWith("/")) {
                copied = Paths.get(dirFromUrl + dirToUrl + "/" + fileName);
            } else {
                copied = Paths.get(dirFromUrl + "/" + dirToUrl + "/" + fileName);
            }
        } else {
            copied = Paths.get(dirToUrl + "/" + fileName);
        }
        return copied;
    }

    //Копирование директории
    private void copyOneDirectory(Path originalPath, Path dest) {
        try {
            Files.walk(originalPath)
                    .forEach(source -> {
                        try {
                            Files.copy(source, dest.resolve(originalPath.relativize(source)),
                                    StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            dirFiles.setText("Ошибка при работе с каталогом: " + e.getMessage());
        }

    }
}
