package ru.lab.menu;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class MenuApp extends JFrame {
    private final File file = new File("TestDir");
    private JButton viewButton;
    private JLabel labelDir;
    private JLabel mainDir;
    private JButton CDButton;
    private JComboBox typeBox;
    private JButton copyButton;
    private JLabel exceptionLabel;
    private JLabel sup;
    private JTextPane allFileNames;
    private JLabel labelCopy;
    private JLabel labelDirToCopy;
    private JPanel mainPanel;
    private JButton toMainDirButton;
    private JComboBox fileNamesBox;
    private JLabel copyField;
    private JComboBox changeDirBox;
    private JComboBox changeDirToCopy;
    private String dirFromUrl = file.getPath();

    public MenuApp() {
        mainDir.setText(dirFromUrl);
        changeBoxes();

        CDButton.addActionListener(e -> {
            cdUrl((String) Objects.requireNonNull(changeDirBox.getSelectedItem()));   //То, что ввёл пользователь, отправляется на проверку
            mainDir.setText(dirFromUrl);
            changeBoxes();
        });

        viewButton.addActionListener(e -> allFileNames.setText(viewAll()));


        copyButton.addActionListener(e -> {
            String command = (String) typeBox.getSelectedItem(); //Выбор типа копирования
            String fileToCopyName = (String) fileNamesBox.getSelectedItem(); // Выбор имени файла
            String dirForCopy = (String) changeDirToCopy.getSelectedItem(); // Выбор директории для копирования
            switch (command) {
                case "By name" -> copyByName(new String[]{fileToCopyName, dirForCopy});
                case "By extension" -> copyByExtension(new String[]{fileToCopyName, dirForCopy});
            }
        });
        toMainDirButton.addActionListener(e -> {
            dirFromUrl = file.getPath();
            mainDir.setText(dirFromUrl);
            changeBoxes();
        });
        typeBox.addActionListener(e -> {
            String[] commands = {"Name", "Extension"};
            int commandId = typeBox.getSelectedIndex();
            switch (commands[commandId]) {
                case "Name" -> {
                    fileNamesBox.removeAllItems();
                    for (String item : getFileNames()) {
                        fileNamesBox.addItem(item);
                    }
                }
                case "Extension" -> {
                    fileNamesBox.removeAllItems();
                    for (String item : getFileExtensions()) {
                        fileNamesBox.addItem(item);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MenuApp");
        frame.setContentPane(new MenuApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void changeBoxes() {
        changeDirBox.removeAllItems();
        for (String item : getDirNames()) {
            changeDirBox.addItem(item);
        }
        changeDirToCopy.removeAllItems();
        for (String item : getDirNames()) {
            changeDirToCopy.addItem(item);
        }
    }

    private String[] getFileNames() {
        List<String> names = new ArrayList<>();
        File folder = new File(dirFromUrl);
        File[] files = folder.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                names.add(file.getName());
            } else {
                names.add(file.getName());
            }
        }
        return names.toArray(String[]::new);
    }

    private Set<String> getDirNames() {
        Set<String> fileExtensionsSet = new HashSet<>();
        File folder = new File(dirFromUrl);
        File[] files = folder.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                fileExtensionsSet.add(file.getName());
            }
        }
        return fileExtensionsSet;
    }

    private Set<String> getFileExtensions() {
        Set<String> fileExtensionsSet = new HashSet<>();
        File folder = new File(dirFromUrl);
        File[] files = folder.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                fileExtensionsSet.add("dir");
            } else {
                int index = file.getName().lastIndexOf('.');
                if (index > 0) {
                    String extension = file.getName().substring(index + 1);
                    fileExtensionsSet.add(extension);
                }
            }
        }
        return fileExtensionsSet;
    }

    private String viewAll() {
        StringBuilder sb = new StringBuilder();
        File folder = new File(dirFromUrl);
        File[] files = folder.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                sb.append("[dir] ").append(file.getName()).append("\n");
            } else {
                sb.append(file.getName()).append("\n");
            }
        }
        return sb.toString();
    }

    //Закрепление новой директории
    private void cdUrl(String dirFromUrl) {
        if (dirFromUrl.equals("")) {
            return;
        }
        if (dirFromUrl.indexOf(':') < 0) {
            if (dirFromUrl.startsWith("/")) {
                this.dirFromUrl = this.dirFromUrl + dirFromUrl.trim();
            } else {
                this.dirFromUrl = this.dirFromUrl + "/" + dirFromUrl.trim();
            }
        } else {
            this.dirFromUrl = dirFromUrl;
        }
    }

    //Копирование по имени
    private void copyByName(String[] parts) {
        String fileName = parts[0];
        String dirToUrl = parts[1];
        File original = new File(dirFromUrl + "/" + fileName);
        Path originalPath = original.toPath();
        Path copied = getPathForCopy(dirToUrl, fileName);
        if (original.isDirectory()) {
            copyOneDirectory(originalPath, copied);
            exceptionLabel.setText("Успешное копирование каталога");
        } else {
            exceptionLabel.setText(copyOneFile(originalPath, copied) ? "Успешное копирование файла" : "Произошла ошибка");
        }
    }

    //Копирование одного файла
    private boolean copyOneFile(Path originalPath, Path copied) {
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            exceptionLabel.setText("Ошибка при работе с файлом: " + e.getMessage());
        }
        return false;
    }

    //Копирование по расширению
    private void copyByExtension(String[] parts) {
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
        } else {
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
            exceptionLabel.setText("Ошибка при работе с каталогом: " + e.getMessage());
        }

    }
}
