package com.wuest.prefab.structures.base;

import com.google.gson.annotations.Expose;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

/**
 * This class is used to define the necessary properties to describe a tile entity to be generated when a structure is
 * created in the world.
 *
 * @author WustMan
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class BuildTileEntity {
	@Expose
	private String entityDomain;

	@Expose
	private String entityName;

	@Expose
	private PositionOffset startingPosition;

	@Expose
	private String entityNBTData;

	/**
	 * Initializes a new instance of the BuildTileEntity class.
	 */
	public BuildTileEntity() {
		this.Initialize();
	}

	public String getEntityDomain() {
		return this.entityDomain;
	}

	public void setEntityDomain(String value) {
		this.entityDomain = value;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(String value) {
		this.entityName = value;
	}

	public PositionOffset getStartingPosition() {
		return this.startingPosition;
	}

	public void setStartingPosition(PositionOffset value) {
		this.startingPosition = value;
	}

	public String getEntityNBTData() {
		return this.entityNBTData;
	}

	public void setEntityNBTData(String value) {
		this.entityNBTData = value;
	}

	public void setEntityNBTData(NbtCompound tagCompound) {
		this.entityNBTData = tagCompound.toString();
	}

	/**
	 * Initializes any properties to their default properties.
	 */
	public void Initialize() {
		this.entityDomain = "";
		this.entityName = "";
		this.startingPosition = new PositionOffset();
		this.entityNBTData = "";
	}

	public NbtCompound getEntityDataTag() {
		NbtCompound tag = null;

		if (!this.entityNBTData.equals("")) {
			try {
				tag = StringNbtReader.parse(this.entityNBTData);
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
		}

		return tag;
	}
}