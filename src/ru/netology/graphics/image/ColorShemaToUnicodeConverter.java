package ru.netology.graphics.image;

public class ColorShemaToUnicodeConverter implements TextColorSchema {
    char[] symbols = {'#', '$', '@', '%', '*', '+', '-', '\\'};

    @Override
    public char convert(int color) {
        return symbols[(int) Math.floor(color / 256. * symbols.length)];
    }
}