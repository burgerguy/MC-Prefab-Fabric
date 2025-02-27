package com.wuest.prefab.structures.config.enums;

public class SaloonOptions extends BaseOption {
    public static SaloonOptions Default = new SaloonOptions(
            "item.prefab.saloon",
            "assets/prefab/structures/saloon.zip",
            "textures/gui/saloon_topdown.png",
            false,
            false);

    protected SaloonOptions(String translationString,
                            String assetLocation,
                            String pictureLocation,
                            boolean hasBedColor,
                            boolean hasGlassColor) {
        super(
                translationString,
                assetLocation,
                pictureLocation,
                hasBedColor,
                hasGlassColor);
    }
}
