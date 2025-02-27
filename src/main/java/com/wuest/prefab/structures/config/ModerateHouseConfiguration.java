package com.wuest.prefab.structures.config;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.predefined.StructureModerateHouse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * This class is used for the moderate houses in the mod.
 *
 * @author WuestMan
 */
public class ModerateHouseConfiguration extends StructureConfiguration {
	public static String tagKey = "houseConfig";
	private static String houseStyleTag = "houseStyle";
	private static String addChestTag = "addChests";
	private static String addChestContentsTag = "addChestContents";
	private static String addMineshaftTag = "addMineshaft";
	private static String bedColorTag = "bedColor";

	/**
	 * The house style.
	 */
	public HouseStyle houseStyle;

	/**
	 * Determines if the chests are included in the structure generation.
	 */
	public boolean addChests;

	/**
	 * Determines if the chest items are included in the structure generation.
	 */
	public boolean addChestContents;

	/**
	 * Determines if the mineshaft is generated in the structure generation.
	 */
	public boolean addMineshaft;

	public DyeColor bedColor;

	/**
	 * Initializes a new instance of the {@link ModerateHouseConfiguration} class.
	 */
	public ModerateHouseConfiguration() {
		super();

		this.addChests = true;
		this.addChestContents = true;
		this.addMineshaft = true;
		this.houseStyle = HouseStyle.SPRUCE_HOME;
		this.bedColor = DyeColor.RED;
	}

	@Override
	public void Initialize() {
		super.Initialize();
		this.houseStyle = HouseStyle.SPRUCE_HOME;
		this.bedColor = DyeColor.RED;
	}

	@Override
	protected NbtCompound CustomWriteToCompoundNBT(NbtCompound tag) {
		tag.putInt(ModerateHouseConfiguration.houseStyleTag, this.houseStyle.value);
		tag.putBoolean(ModerateHouseConfiguration.addChestTag, this.addChests);
		tag.putBoolean(ModerateHouseConfiguration.addChestContentsTag, this.addChestContents);
		tag.putBoolean(ModerateHouseConfiguration.addMineshaftTag, this.addMineshaft);
		tag.putString(ModerateHouseConfiguration.bedColorTag, this.bedColor.asString().toUpperCase());

		return tag;
	}

	@Override
	protected void CustomReadFromNBTTag(NbtCompound messageTag, StructureConfiguration config) {
		ModerateHouseConfiguration houseConfiguration = ((ModerateHouseConfiguration) config);

		if (messageTag.contains(ModerateHouseConfiguration.houseStyleTag)) {
			houseConfiguration.houseStyle = HouseStyle.ValueOf(messageTag.getInt(ModerateHouseConfiguration.houseStyleTag));
		}

		if (messageTag.contains(ModerateHouseConfiguration.addChestTag)) {
			houseConfiguration.addChests = messageTag.getBoolean(ModerateHouseConfiguration.addChestTag);
		}

		if (messageTag.contains(ModerateHouseConfiguration.addChestContentsTag)) {
			houseConfiguration.addChestContents = messageTag.getBoolean(ModerateHouseConfiguration.addChestContentsTag);
		}

		if (messageTag.contains(ModerateHouseConfiguration.addMineshaftTag)) {
			houseConfiguration.addMineshaft = messageTag.getBoolean(ModerateHouseConfiguration.addMineshaftTag);
		}

		if (messageTag.contains(ModerateHouseConfiguration.bedColorTag)) {
			houseConfiguration.bedColor = DyeColor.valueOf(messageTag.getString(ModerateHouseConfiguration.bedColorTag));
		}
	}

	/**
	 * Custom method to read the CompoundNBT message.
	 *
	 * @param messageTag The message to create the configuration from.
	 * @return An new configuration object with the values derived from the CompoundNBT.
	 */
	@Override
	public ModerateHouseConfiguration ReadFromCompoundNBT(NbtCompound messageTag) {
		ModerateHouseConfiguration config = new ModerateHouseConfiguration();

		return (ModerateHouseConfiguration) super.ReadFromCompoundNBT(messageTag, config);
	}

	/**
	 * This is used to actually build the structure as it creates the structure instance and calls build structure.
	 *
	 * @param player      The player which requested the build.
	 * @param world       The world instance where the build will occur.
	 * @param hitBlockPos This hit block position.
	 */
	@Override
	protected void ConfigurationSpecificBuildStructure(PlayerEntity player, ServerWorld world, BlockPos hitBlockPos) {
		StructureModerateHouse structure = StructureModerateHouse.CreateInstance(this.houseStyle.getStructureLocation(), StructureModerateHouse.class);

		if (structure.BuildStructure(this, world, hitBlockPos, Direction.NORTH, player)) {
			this.RemoveStructureItemFromPlayer(player, ModRegistry.ModerateHouse);
		}
	}

	/**
	 * This house style is used to determine what type of houses are available to the user.
	 *
	 * @author WuestMan
	 */
	public enum HouseStyle {
		SPRUCE_HOME(0, GuiLangKeys.MODERATE_HOUSE_SPRUCE, new Identifier("prefab", "textures/gui/moderate_house_spruce_topdown.png"),
				"assets/prefab/structures/moderate_house_spruce.zip", 31, 31, 23, 8, 1),
		ACACIA_HOME(1, GuiLangKeys.MODERATE_HOUSE_ACACIA, new Identifier("prefab", "textures/gui/moderate_house_acacia_topdown.png"),
				"assets/prefab/structures/moderate_house_acacia.zip", 31, 31, 21, 12, 6),
		EARTHEN_HOME(2, GuiLangKeys.MODERATE_EARTHEN_HOME, new Identifier("prefab", "textures/gui/moderate_house_earthen_topdown.png"),
				"assets/prefab/structures/moderate_house_earthen.zip", 16, 16, 16, 8, 6),
		JUNGLE_TREE_HOME(3, GuiLangKeys.MODERATE_JUNGLE_HOME, new Identifier("prefab", "textures/gui/moderate_house_jungle_topdown.png"),
				"assets/prefab/structures/moderate_house_jungle.zip", 16, 16, 41, 8, 1),
		NETHER_HOME(4, GuiLangKeys.MODERATE_NETHER_HOME, new Identifier("prefab", "textures/gui/moderate_house_nether_topdown.png"),
				"assets/prefab/structures/moderate_house_nether.zip", 16, 15, 22, 0, 0),
		MOUNTAIN_HOME(5, GuiLangKeys.MODERATE_MOUNTAIN_HOME, new Identifier("prefab", "textures/gui/moderate_house_mountain_topdown.png"),
				"assets/prefab/structures/moderate_house_mountain.zip", 20, 21, 12, 10, 0),
		ACACIA_HOME2(6, GuiLangKeys.MODERATE_HOUSE_ACACIA_2, new Identifier("prefab", "textures/gui/moderate_house_acacia_2_topdown.png"),
				"assets/prefab/structures/moderate_house_acacia_2.zip", 31, 31, 21, 12, 6);

		private final int value;
		private final String displayName;
		private final Identifier housePicture;
		private final String structureLocation;
		private final int width;
		private final int length;
		private final int height;
		private final int eastOffSet;
		private final int downOffSet;

		HouseStyle(int newValue, String displayName, Identifier housePicture, String structureLocation, int width, int length, int height,
				   int eastOffSet, int downOffSet) {
			this.value = newValue;
			this.displayName = displayName;
			this.housePicture = housePicture;
			this.structureLocation = structureLocation;
			this.width = width;
			this.length = length;
			this.height = height;
			this.eastOffSet = eastOffSet;
			this.downOffSet = downOffSet;
		}

		public static HouseStyle ValueOf(int value) {
			for (HouseStyle currentValue : HouseStyle.values()) {
				if (currentValue.value == value) {
					return currentValue;
				}
			}

			return HouseStyle.SPRUCE_HOME;
		}

		public int getValue() {
			return value;
		}

		public String getDisplayName() {
			return GuiLangKeys.translateString(this.displayName);
		}

		public Identifier getHousePicture() {
			return this.housePicture;
		}

		public int getWidth() {
			return this.width;
		}

		public int getLength() {
			return this.length;
		}

		public int getHeight() {
			return this.height;
		}

		public int getEastOffSet() {
			return this.eastOffSet;
		}

		public int getDownOffSet() {
			return this.downOffSet;
		}

		public String getStructureLocation() {
			return this.structureLocation;
		}
	}
}