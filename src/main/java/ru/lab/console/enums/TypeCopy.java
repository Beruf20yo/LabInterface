package ru.lab.console.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeCopy {
    BY_NAME("-byname", "Копирование по имени"),
    BY_EXTENSION("-byext", "Копирование по расширению");
    private final String tag;
    private final String tagDescription;
}
