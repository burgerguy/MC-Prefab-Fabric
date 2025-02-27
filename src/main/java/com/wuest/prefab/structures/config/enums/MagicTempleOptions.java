package com.wuest.prefab.structures.config.enums;

public class MagicTempleOptions extends BaseOption {
    public static MagicTempleOptions Default = new MagicTempleOptions(
            "item.prefab.magic_temple",
            "assets/prefab/structures/magic_temple.zip",
            "textures/gui/magic_temple_topdown.png",
            false,
            false);

    protected MagicTempleOptions(String translationString,
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
