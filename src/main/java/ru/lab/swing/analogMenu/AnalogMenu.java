package ru.lab.swing.analogMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class AnalogMenu {
    private final File file = new File("TestDir");
    List<TypeCreate> types = new ArrayList<>(List.of(TypeCreate.ByName, TypeCreate.ByExt,
            TypeCreate.DirTo, TypeCreate.DirFrom));
    private JPanel mainPanel;
    private JMenuBar MenuBar;
    private JMenu mainMenu;
    private JMenu changeDirFromMenu;
    private JMenuItem viewItem;
    private JMenu copyMenu;
    private JLabel supportLabel;
    private JLabel mainDir;
    private JMenu byExtMenu;
    private JMenu byNameMenu;
    private JLabel dirFrom;
    private JLabel dirTo;
    private JLabel dirToLabel;
    private JMenu changeDirToMenu;
    private JTextPane allFileNames;
    private JLabel nameLabel;
    private String dirFromUrl = file.getPath();

    public AnalogMenu() {
        mainDir.setText(dirFromUrl);
        setMenus();
        viewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allFileNames.setText(viewAll());
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("AnalogMenu");
        frame.setContentPane(new AnalogMenu().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1200, 500);
    }

    private void createListeners(TypeCreate type) {// JMenu menu, boolean chose
        int count = 0;
        switch (type) {
            case ByExt -> count = byExtMenu.getItemCount();
            case ByName -> count = byNameMenu.getItemCount();
            case DirTo -> count = changeDirToMenu.getItemCount();
            case DirFrom -> count = changeDirFromMenu.getItemCount();
        }
        for (int i = 0; i < count; i++) {
            switch (type) {
                case ByExt -> createOneByExtListener(i);
                case ByName -> createOneByNameListener(i);
                case DirTo -> createOneListenerDirTo(i);
                case DirFrom -> createOneListenerDirFrom(i);
            }
        }
    }

    private JMenuItem createItem(String name) {
        return new JMenuItem(name);
    }

    private void setMenus() {
        clearAll();
        changeDirFromMenu.add(createItem("Main"));
        for (String str : getDirNames()) {
            changeDirFromMenu.add(createItem(str));
            changeDirToMenu.add(createItem(str));
        }
        for (var str : getFileExtensions()) {
            byExtMenu.add(createItem(str));
        }
        for (var str : getFileNames()) {
            byNameMenu.add(createItem(str));
        }
        for (var menu : types) {
            createListeners(menu);
        }
    }

    private void clearAll() {
        changeDirFromMenu.removeAll();
        changeDirToMenu.removeAll();
        byNameMenu.removeAll();
        byExtMenu.removeAll();
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

    private void changeDirTo(String nameDirTo) {
        dirToLabel.setText(nameDirTo);
    }

    private void changeDirFrom(String newDir) {
        if (newDir.equals("Main")) {
            this.dirFromUrl = file.getPath();
        } else {
            this.dirFromUrl = this.dirFromUrl + "/" + newDir.trim();
        }
        mainDir.setText(dirFromUrl);
        setMenus();
    }

    private void createOneListenerDirTo(int finalI) {
        changeDirToMenu.getItem(finalI).addActionListener(e ->
                changeDirTo(changeDirToMenu.getItem(finalI).getText()));
    }

    private void createOneListenerDirFrom(int finalI) {
        changeDirFromMenu.getItem(finalI).addActionListener(e ->
                changeDirFrom(changeDirFromMenu.getItem(finalI).getText()));
    }

    private void createOneByNameListener(int finalI) {
        byNameMenu.getItem(finalI).addActionListener(e ->
                copyByName(byNameMenu.getItem(finalI).getText()));
    }

    private void createOneByExtListener(int finalI) {
        byExtMenu.getItem(finalI).addActionListener(e ->
                copyByExtension(byExtMenu.getItem(finalI).getText()));
    }

    private String checkDirTo() {
        String dirTo = dirToLabel.getText();
        try {
            if (dirTo.equals("UNKNOWN")) {
                throw new IllegalStateException();
            } else {
                return dirTo;
            }
        } catch (IllegalStateException e) {
            showMessageDialog(null, "Вы не выбрали каталог, в который будет всё скопированно");
        }
        return null;
    }

    private void copyByName(String fileName) {
        String dirToUrl = dirToLabel.getText();
        if (!checkMistakeInCopyLogic(fileName, dirToUrl)) {
        } else {
            File original = new File(dirFromUrl + "/" + fileName);
            Path originalPath = original.toPath();
            Path copied = getPathForCopy(dirToUrl, fileName);
            if (original.isDirectory()) {
                copyOneDirectory(originalPath, copied);
                supportLabel.setText("Успешное копирование каталога");
            } else {
                supportLabel.setText(copyOneFile(originalPath, copied) ? "Успешное копирование файла" : "Произошла ошибка");
            }
        }
    }

    private boolean checkMistakeInCopyLogic(String fileName, String dirToUrl) {
        if (dirToUrl.endsWith(fileName) || (dirToUrl.equals("UNKNOWN"))) {
            showMessageDialog(null, "Невозможное копирование каталога");
            return false;
        }
        return true;
    }

    //Копирование одного файла
    private boolean copyOneFile(Path originalPath, Path copied) {
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            supportLabel.setText("Ошибка при работе с файлом: " + e.getMessage());
        }
        return false;
    }

    //Копирование по расширению
    private void copyByExtension(String extension) {
        String dirToUrl = checkDirTo();
        File folder = new File(dirFromUrl);
        File[] files = folder.listFiles();
        if (extension.equals("dir")) {
            for (File file : Objects.requireNonNull(files)) {
                if (file.isDirectory() && !(dirToUrl.endsWith(file.getName()))) {
                    copyOneDirectory(file.toPath(), getPathForCopy(dirToUrl, file.getName()));
                }
            }
            supportLabel.setText("Успешное копирование!");
        } else if (extension.startsWith(".")) {
            for (File file : Objects.requireNonNull(files)) {
                if (file.getName().endsWith(extension)) {
                    copyOneFile(file.toPath(), getPathForCopy(dirToUrl, file.getName()));
                }
            }
        }
    }

    private Path getPathForCopy(String dirToUrl, String fileName) {
        return Paths.get(dirFromUrl + "/" + dirToUrl + "/" + fileName);
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
            supportLabel.setText("Ошибка при работе с каталогом: " + e.getMessage());
        }

    }
}
