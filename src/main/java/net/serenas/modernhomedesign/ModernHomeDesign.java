package net.serenas.modernhomedesign;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockSetType.ActivationRule;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.serenas.modernhomedesign.blocks.FoldingDesk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModernHomeDesign implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("modern-home-design");

	public static final BlockSetType ECO_FRIENDLY_WOOD_VENEER = new BlockSetType("eco_friendly_wood_veneer", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.WOOD, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON);

	public static final Block FOLDING_DESK = new FoldingDesk(ECO_FRIENDLY_WOOD_VENEER, AbstractBlock.Settings.create().hardness(0));


	//TODO: add expansion screws to stop gravity blocks from falling
	//TODO: add galvanized steel beams in various shapes, should be slightly smaller collisionbox than iron bars and should align with beams
	//TODO: add eco friendly wood veneers, should allow being placed on galvanized steel beams to change
	//TODO: [IN PROGRESS] add trapdoor-like merging foldable table desk things (when activated, should check blocks on each side for more of them and should flip all up at once)
	//TODO: make merging folding tables fold up/down with multiple blocks from wall
	//TODO: wash basins
	//TODO: storage for out-of-season clothing
	//TODO: water tanks in the wall????? probably just use NBT to be honest
	//TODO: add random kid entities


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registries.BLOCK, new Identifier("smhd", "folding_desk"), FOLDING_DESK);
		Registry.register(Registries.ITEM, new Identifier("smhd", "folding_desk"), new BlockItem(FOLDING_DESK, new Settings()));

		LOGGER.info("Ready to design modern homes~");
	}
}