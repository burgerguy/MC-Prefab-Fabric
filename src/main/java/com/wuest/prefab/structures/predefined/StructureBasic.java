package com.wuest.prefab.structures.predefined;

import com.wuest.prefab.Tuple;
import com.wuest.prefab.structures.base.BuildBlock;
import com.wuest.prefab.structures.base.BuildingMethods;
import com.wuest.prefab.structures.base.Structure;
import com.wuest.prefab.structures.config.BasicStructureConfiguration;
import com.wuest.prefab.structures.config.BasicStructureConfiguration.EnumBasicStructureName;
import com.wuest.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.config.enums.BaseOption;
import com.wuest.prefab.structures.config.enums.ModerateFarmOptions;
import com.wuest.prefab.structures.config.enums.StarterFarmOptions;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * This is the basic structure to be used for structures which don't need a lot of configuration or a custom player
 * created structures.
 *
 * @author WuestMan
 */
public class StructureBasic extends Structure {
    private BlockPos customBlockPos = null;
    private ArrayList<Tuple<BlockPos, BlockPos>> bedPositions = new ArrayList<>();

    @Override
    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world, BlockPos originalPos,
                                                   Direction assumedNorth, Block foundBlock, BlockState blockState, PlayerEntity player) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;

        String structureName = config.basicStructureName.getName();
        BaseOption chosenOption = config.chosenOption;

        if (foundBlock instanceof HopperBlock && structureName.equals(EnumBasicStructureName.ModerateFarm.getName()) && chosenOption == ModerateFarmOptions.AutomatedChickenCoop) {
            this.customBlockPos = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        } else if (foundBlock instanceof TrapdoorBlock && structureName.equals(EnumBasicStructureName.MineshaftEntrance.getName())) {
            this.customBlockPos = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        } else if (foundBlock == Blocks.SPONGE
                && structureName.equals(BasicStructureConfiguration.EnumBasicStructureName.WorkShop.getName())) {
            // Sponges are sometimes used in-place of trapdoors when trapdoors are used for decoration.
            this.customBlockPos = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing).up();
        } else if (foundBlock instanceof BedBlock && config.chosenOption.getHasBedColor()) {
            // Even if a structure has a bed; we may want to keep a specific color to match what the design of the structure is.
            BlockPos bedHeadPosition = block.getStartingPosition().getRelativePosition(originalPos, this.getClearSpace().getShape().getDirection(), configuration.houseFacing);
            BlockPos bedFootPosition = block.getSubBlock().getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);

            this.bedPositions.add(new Tuple<>(bedHeadPosition, bedFootPosition));

            return true;
        }

        Identifier foundBlockIdentifier = Registry.BLOCK.getId(foundBlock);
        if (foundBlockIdentifier.getNamespace().equals(Registry.BLOCK.getId(Blocks.WHITE_STAINED_GLASS).getNamespace())
                && foundBlockIdentifier.getPath().endsWith("glass")
                && config.chosenOption.getHasGlassColor()) {
            blockState = this.getStainedGlassBlock(config.glassColor);
            block.setBlockState(blockState);
            this.priorityOneBlocks.add(block);

            return true;
        } else if (foundBlockIdentifier.getNamespace().equals(Registry.BLOCK.getId(Blocks.WHITE_STAINED_GLASS_PANE).getNamespace())
                && foundBlockIdentifier.getPath().endsWith("glass_pane")
                && config.chosenOption.getHasGlassColor()) {
            blockState = this.getStainedGlassPaneBlock(config.glassColor);

            BuildBlock.SetBlockState(
                    configuration,
                    world,
                    originalPos,
                    assumedNorth,
                    block,
                    foundBlock,
                    blockState,
                    this);

            this.priorityOneBlocks.add(block);
            return true;
        }

        return false;
    }

    @Override
    protected Boolean BlockShouldBeClearedDuringConstruction(StructureConfiguration configuration, World world, BlockPos originalPos, Direction assumedNorth, BlockPos blockPos) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;

        if (config.basicStructureName.getName().equals(EnumBasicStructureName.AquaBase.getName())
                || config.basicStructureName.getName().equals(EnumBasicStructureName.AdvancedAquaBase.getName())) {
            BlockState blockState = world.getBlockState(blockPos);
            // Don't clear water blocks for this building.
            return blockState.getMaterial() != Material.WATER;
        }

        return true;
    }

    /**
     * This method is used after the main building is build for any additional structures or modifications.
     *
     * @param configuration The structure configuration.
     * @param world         The current world.
     * @param originalPos   The original position clicked on.
     * @param assumedNorth  The assumed northern direction.
     * @param player        The player which initiated the construction.
     */
    @Override
    public void AfterBuilding(StructureConfiguration configuration, ServerWorld world, BlockPos originalPos, Direction assumedNorth, PlayerEntity player) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;
        String structureName = config.basicStructureName.getName();
        BaseOption chosenOption = config.chosenOption;

        if (this.customBlockPos != null) {
            if (structureName.equals(EnumBasicStructureName.ModerateFarm.getName()) && chosenOption == ModerateFarmOptions.AutomatedChickenCoop) {
                // For the advanced chicken coop, spawn 4 chickens above the hopper.
                for (int i = 0; i < 4; i++) {
                    ChickenEntity entity = new ChickenEntity(EntityType.CHICKEN, world);
                    entity.setPos(this.customBlockPos.getX(), this.customBlockPos.up().getY(), this.customBlockPos.getZ());
                    world.spawnEntity(entity);
                }
            }else if (structureName.equals(EnumBasicStructureName.MineshaftEntrance.getName())
                    || structureName.equals(BasicStructureConfiguration.EnumBasicStructureName.WorkShop.getName())) {
                // Build the mineshaft where the trap door exists.
                BuildingMethods.PlaceMineShaft(world, this.customBlockPos.down(), configuration.houseFacing, true);
            }

            this.customBlockPos = null;
        }

        if (this.bedPositions.size() > 0) {
            for (Tuple<BlockPos, BlockPos> bedPosition : this.bedPositions) {
                BuildingMethods.PlaceColoredBed(world, bedPosition.getFirst(), bedPosition.getSecond(), config.bedColor);
            }
        }

        if (structureName.equals(EnumBasicStructureName.AquaBase.getName())
                || structureName.equals(EnumBasicStructureName.AdvancedAquaBase.getName())) {
            // Replace the entrance area with air blocks.
            BlockPos airPos = originalPos.up(4).offset(configuration.houseFacing.getOpposite(), 1);

            // This is the first wall.
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.down();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.down();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.down();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            // Second part of the wall.
            airPos = airPos.offset(configuration.houseFacing.getOpposite()).up();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.up();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.up();
            world.removeBlock(airPos, false);
        }
    }
}
