package net.serenas.modernhomedesign;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModernHomeDesign implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("modern-home-design");

	//TODO: add expansion screws to stop gravity blocks from falling
	//TODO: add galvanized steel beams in various shapes, should be slightly smaller collisionbox than iron bars and should align with beams
	//TODO: add eco friendly wood veneers, should allow being placed on galvanized steel beams to change
	//TODO: add trapdoor-like merging foldable table desk things (when activated, should check blocks on each side for more of them and should flip all up at once)
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

		LOGGER.info("Ready to design modern homes~");
	}
}