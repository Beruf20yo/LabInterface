package ru.lab.console;

import lombok.AllArgsConstructor;
import ru.lab.enums.TypeAction;
import ru.lab.enums.TypeCopy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class ConsoleApp {

    private final List<TypeAction> typeCommand = new ArrayList<>(
            List.of(TypeAction.VIEW, TypeAction.CD, TypeAction.COPY,
                    TypeAction.HELP, TypeAction.EXIT, TypeAction.COPY_ALG,
                    TypeAction.MAIN));
    private String dirFromUrl;

    public void checkCommand() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print(dirFromUrl + "=> ");
                String fullStr = bufferedReader.readLine().replaceAll("[\\s]{2,}", " ");
                String[] parts = fullStr.trim().split(" ", 2);
                String command = parts[0].replace(" ", "").toLowerCase();
                switch (command) {
                    case "copy" -> checkCopyType(parts[1]);
                    case "view" -> viewAll().forEach(System.out::println);
                    case "cd" -> cdUrl(parts[1]);
                    case "copyalg" -> copyAlgorithm();
                    case "!help" -> {
                        System.out.println(TypeAction.HELP.getTagDescription());
                        showAllCommands();
                    }
                    case "exit" -> {
                        return;
                    }
                    case "main" ->{
                        dirFromUrl = new File("TestDir").getPath();
                    }
                    default -> System.out.println("Ошибка ввода команды " + command + "," +
                            " введите !help, чтобы увидеть список возможных команд");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода команды, введите !help, чтобы увидеть список возможных команд");
        }

    }

    private void copyAlgorithm() {
        System.out.println("""
                Копировать можно как файлы так и каталоги:
                  Для копирования по имени:
                   -Абсолютный путь: copy -byname example.txt D://Example
                   -Путь относительно текущего каталога: copy -byname dirName /example""");
        System.out.println("""
                  Для копирования по расширению:
                   -Абсолютный путь: copy -byext .txt D://Example
                   -Путь относительно текущего каталога: copy -byname dir /example\
                """);
    }

    private void showAllCommands() {
        for (TypeAction type : typeCommand) {
            System.out.println(type.getTag() + " - " + type.getTagDescription());
        }
    }
    //Просмотр файлов
    private List<String> viewAll() {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(dirFromUrl);
        File[] files = folder.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                fileNames.add("[dir] " + file.getName());
            } else {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    //Закрепление новой директории
    private void cdUrl(String dirFromUrl) {
        String newDir = dirFromUrl.replace("/", "");
        if (dirFromUrl.indexOf(':') < 0 && viewAll().contains("[dir] " + newDir)) {
            this.dirFromUrl = this.dirFromUrl + "/" + newDir.trim();
        } else {
            this.dirFromUrl = dirFromUrl;
        }
    }

    private void checkCopyType(String parts) {
        String command = parts.trim();
        if (command.startsWith(TypeCopy.BY_NAME.getTag())) {
            copyByName(command.replace(TypeCopy.BY_NAME.getTag(), ""));
        } else if (command.startsWith(TypeCopy.BY_EXTENSION.getTag())) {
            copyByExtension(command.replace(TypeCopy.BY_EXTENSION.getTag(), ""));
        } else {
            System.out.println("Вы некорректно ввели тип копирования " +
                    "\nВозможные команды копирования:");
            System.out.println(TypeCopy.BY_EXTENSION.getTag() + " - " + TypeCopy.BY_EXTENSION.getTagDescription() +
                    TypeCopy.BY_NAME.getTag() + " - " + TypeCopy.BY_NAME.getTagDescription());
        }
    }

    //Копирование по имени
    private void copyByName(String command) {
        String[] parts = command.trim().split(" ", 2);
        String fileName = parts[0];
        String dirToUrl = parts[1];
        if(dirToUrl.endsWith(fileName)){
            System.out.println("Невозможное копирование каталога");
            return;
        }
        File original = new File(dirFromUrl + "/" + fileName);
        Path originalPath = original.toPath();
        Path copied = getPathForCopy(dirToUrl, fileName);
        if (original.isDirectory()) {
            copyOneDirectory(originalPath, copied);
            System.out.println("Успешное копирование каталога");

        } else {
            System.out.println(copyOneFile(originalPath, copied) ? "Успешное копирование файла" : "Произошла ошибка");
        }

    }

    //Копирование одного файла
    private boolean copyOneFile(Path originalPath, Path copied) {
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Ошибка при работе с файлом: " + e.getMessage());
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
                if (file.isDirectory() && !(dirToUrl.endsWith(file.getName()))) {
                    copyOneDirectory(file.toPath(), getPathForCopy(dirToUrl, file.getName()));
                    System.out.println("Успешное копирование каталога");
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
            Files.  walk(originalPath)
                    .forEach(source -> {
                        try {
                            Files.copy(source, dest.resolve(originalPath.relativize(source)),
                                    StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            System.out.println("Ошибка при работе с каталогом: " + e.getMessage());
        }

    }


}
