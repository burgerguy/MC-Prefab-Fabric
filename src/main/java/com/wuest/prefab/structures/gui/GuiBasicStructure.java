package com.wuest.prefab.structures.gui;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.blocks.FullDyeColor;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.GuiUtils;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.structures.config.BasicStructureConfiguration;
import com.wuest.prefab.structures.config.enums.BaseOption;
import com.wuest.prefab.structures.items.ItemBasicStructure;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructureBasic;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;

/**
 * This class is used as the gui for all basic structures.
 *
 * @author WuestMan
 */
@SuppressWarnings({"SpellCheckingInspection"})
public class GuiBasicStructure extends GuiStructure {
    public BasicStructureConfiguration configuration;
    private ExtendedButton btnBedColor = null;
    private ExtendedButton btnGlassColor = null;
    private ExtendedButton btnStructureOptions = null;
    private ArrayList<BaseOption> availableOptions;
    private boolean showConfigurationOptions;

    public GuiBasicStructure() {
        super("Basic Structure");
        this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.Basic;
        this.showConfigurationOptions = false;
    }

    @Override
    protected void Initialize() {
        super.Initialize();

        // Get the structure configuration for this itemstack.
        ItemStack stack = ItemBasicStructure.getBasicStructureItemInHand(this.player);

        if (stack != null) {
            ItemBasicStructure item = (ItemBasicStructure) stack.getItem();
            this.configuration = ClientModRegistry.playerConfig.getClientConfig(item.structureType.getName(), BasicStructureConfiguration.class);
            this.configuration.basicStructureName = item.structureType;

            if (this.configuration.chosenOption.getClass() != item.structureType.getBaseOption().getClass()) {
                this.availableOptions = item.structureType.getBaseOption().getSpecificOptions(true);
                this.configuration.chosenOption = this.availableOptions.get(0);
            } else {
                this.availableOptions = this.configuration.chosenOption.getSpecificOptions(true);
            }
            
            this.structureImageLocation = this.configuration.chosenOption.getPictureLocation();

        }

        this.configuration.pos = this.pos;

        if (this.availableOptions.size() > 1
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.MineshaftEntrance
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.WatchTower
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.WelcomeCenter
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.VillagerHouses
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.StarterFarm
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.AdvancedWarehouse
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.ModerateFarm
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.AdvancedFarm
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.Warehouse
                || this.configuration.basicStructureName == BasicStructureConfiguration.EnumBasicStructureName.WorkShop) {
            this.showConfigurationOptions = true;
        }

        if (!this.showConfigurationOptions) {
            this.InitializeStandardButtons();
        } else {
            this.modifiedInitialXAxis = 212;
            this.modifiedInitialYAxis = 117;
            this.imagePanelWidth = 285;

            // Get the upper left hand corner of the GUI box.
            Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
            int grayBoxX = adjustedXYValue.getFirst();
            int grayBoxY = adjustedXYValue.getSecond();

            // Create the buttons.
            int x = grayBoxX + 15;
            int y = grayBoxY + 45;

            if (this.availableOptions.size() > 1) {
                this.btnStructureOptions = this.createAndAddButton(x, y, 100, 20, this.configuration.chosenOption.getTranslationString());
                this.btnStructureOptions.visible = true;
                y += 45;
            } else if (this.btnStructureOptions != null) {
                this.btnStructureOptions.visible = false;
            }

            this.btnBedColor = this.createAndAddDyeButton(x, y, 90, 20, this.configuration.bedColor);
            this.btnBedColor.visible = false;

            this.btnGlassColor = this.createAndAddFullDyeButton(x, y, 90, 20, this.configuration.glassColor);
            this.btnGlassColor.visible = false;

            // Create the standard buttons.
            this.btnVisualize = this.createAndAddCustomButton(grayBoxX + 25, grayBoxY + 175, 90, 20, GuiLangKeys.GUI_BUTTON_PREVIEW);
            this.btnBuild = this.createAndAddCustomButton(grayBoxX + 310, grayBoxY + 175, 90, 20, GuiLangKeys.GUI_BUTTON_BUILD);
            this.btnCancel = this.createAndAddButton(grayBoxX + 150, grayBoxY + 175, 90, 20, GuiLangKeys.GUI_BUTTON_CANCEL);
        }
    }

    @Override
    protected void preButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (!this.showConfigurationOptions) {
            super.preButtonRender(matrixStack, x, y, mouseX, mouseY, partialTicks);
        } else {
            // Draw the control background.
            int imagePanelUpperLeft = x + 132;
            int imagePanelMiddle = this.imagePanelWidth / 2;

            this.renderBackground(matrixStack);

            this.drawControlLeftPanel(matrixStack, x + 10, y + 10, 175, 190);
            this.drawControlRightPanel(matrixStack, imagePanelUpperLeft, y + 10, this.imagePanelWidth, 190);

            int middleOfImage = this.shownImageWidth / 2;
            int imageLocation = imagePanelUpperLeft + (imagePanelMiddle - middleOfImage);

            // Draw the picture.
            GuiUtils.bindAndDrawScaledTexture(
                    this.structureImageLocation,
                    matrixStack,
                    imageLocation,
                    y + 15,
                    this.shownImageWidth,
                    this.shownImageHeight,
                    this.shownImageWidth,
                    this.shownImageHeight,
                    this.shownImageWidth,
                    this.shownImageHeight);
        }

        if (this.btnBedColor != null) {
            this.btnBedColor.visible = false;
        }

        if (this.btnGlassColor != null) {
            this.btnGlassColor.visible = false;
        }

        int yValue = y + 45;

        if (this.availableOptions.size() > 1) {
            yValue = yValue + 45;
        }

        if (this.configuration.chosenOption.getHasBedColor()) {
            this.btnBedColor.visible = true;
            this.btnBedColor.y = yValue;

            yValue = yValue + 45;
        }

        if (this.configuration.chosenOption.getHasGlassColor()) {
            this.btnGlassColor.visible = true;
            this.btnGlassColor.y = yValue;
        }
    }

    @Override
    protected void postButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (this.showConfigurationOptions) {
            this.drawSplitString(GuiLangKeys.translateString(this.configuration.basicStructureName.getItemTranslationString()), x + 15, y + 17, 100, this.textColor);

            int yValue = y + 35;

            if (this.availableOptions.size() > 1) {
                this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.BUILDING_OPTIONS), x + 15, yValue, this.textColor);
                yValue += 45;
            }

            // Draw the text here.
            if (this.configuration.chosenOption.getHasBedColor()) {
                this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_BED_COLOR), x + 15, yValue, this.textColor);
                yValue += 45;
            }

            if (this.configuration.chosenOption.getHasGlassColor()) {
                this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_GLASS), x + 15, yValue, this.textColor);
                yValue += 45;
            }
        }
    }

    @Override
    public void buttonClicked(PressableWidget button) {
        this.performCancelOrBuildOrHouseFacing(this.configuration, button);

        if (button == this.btnVisualize) {
            StructureBasic structure = StructureBasic.CreateInstance(this.configuration.chosenOption.getAssetLocation(), StructureBasic.class);
            this.performPreview(structure, this.configuration);
        } else if (button == this.btnBedColor) {
            this.configuration.bedColor = DyeColor.byId(this.configuration.bedColor.getId() + 1);
            GuiUtils.setButtonText(this.btnBedColor, GuiLangKeys.translateDye(this.configuration.bedColor));
        } else if (button == this.btnGlassColor) {
            this.configuration.glassColor = FullDyeColor.byId(this.configuration.glassColor.getId() + 1);
            GuiUtils.setButtonText(this.btnGlassColor, GuiLangKeys.translateFullDye(this.configuration.glassColor));
        } else if (button == this.btnStructureOptions) {
            for (int i = 0; i < this.availableOptions.size(); i++) {
                BaseOption option = this.availableOptions.get(i);
                BaseOption chosenOption = null;

                if (this.configuration.chosenOption.getTranslationString().equals(option.getTranslationString())) {
                    if (i == this.availableOptions.size() - 1) {
                        // This is the last option, set the text to the first option.
                        chosenOption = this.availableOptions.get(0);
                    } else {
                        chosenOption = this.availableOptions.get(i + 1);
                    }
                }

                if (chosenOption != null) {
                    this.configuration.chosenOption = chosenOption;
                    this.structureImageLocation = this.configuration.chosenOption.getPictureLocation();
                    GuiUtils.setButtonText(btnStructureOptions, GuiLangKeys.translateString(chosenOption.getTranslationString()));
                    break;
                }
            }
        }
    }
}
