package com.wuest.prefab.blocks;

import com.google.common.base.Strings;
import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;

public enum FullDyeColor implements StringIdentifiable {
    WHITE(0, "white", DyeColor.WHITE),
    ORANGE(1, "orange", DyeColor.ORANGE),
    MAGENTA(2, "magenta", DyeColor.MAGENTA),
    LIGHT_BLUE(3, "light_blue", DyeColor.LIGHT_BLUE),
    YELLOW(4, "yellow", DyeColor.YELLOW),
    LIME(5, "lime", DyeColor.LIME),
    PINK(6, "pink", DyeColor.PINK),
    GRAY(7, "gray", DyeColor.GRAY),
    LIGHT_GRAY(8, "light_gray", DyeColor.LIGHT_GRAY),
    CYAN(9, "cyan", DyeColor.CYAN),
    PURPLE(10, "purple", DyeColor.PURPLE),
    BLUE(11, "blue", DyeColor.BLUE),
    BROWN(12, "brown", DyeColor.BROWN),
    GREEN(13, "green", DyeColor.GREEN),
    RED(14, "red", DyeColor.RED),
    BLACK(15, "black", DyeColor.BLACK),
    CLEAR(16, "clear", null);

    private final int id;
    private final String name;
    private final DyeColor linkedColor;

    FullDyeColor(int id, String name, DyeColor linkedColor) {
        this.id = id;
        this.name = name;
        this.linkedColor = linkedColor;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public DyeColor getLinkedColor() {
        return this.linkedColor;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }

    public static FullDyeColor byId(int id) {
        FullDyeColor returnValue = FullDyeColor.WHITE;

        for (FullDyeColor value : FullDyeColor.values()) {
            if (value.id == id) {
                returnValue = value;
                break;
            }
        }

        return returnValue;
    }

    public static FullDyeColor byName(String name) {
        FullDyeColor returnValue = FullDyeColor.CLEAR;

        if (!Strings.isNullOrEmpty(name)) {
            for (FullDyeColor value : FullDyeColor.values()) {
                if (value.name.equalsIgnoreCase(name)) {
                    returnValue = value;
                    break;
                }
            }
        }

        return returnValue;
    }
}
