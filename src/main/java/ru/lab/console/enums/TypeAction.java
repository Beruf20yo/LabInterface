package ru.lab.console.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeAction {
    VIEW("view", "Просмотр"), CD("cd", "Закрепление каталога"),
    COPY("copy", "Копирование"), HELP("!help", "Все команды"),
    EXIT("exit", "Выход из программы"), COPY_ALG("copyalg", "Алгоритм работы с copy"),
    MAIN("main", "Возврат в главный каталог");

    private final String tag;
    private final String tagDescription;
}
