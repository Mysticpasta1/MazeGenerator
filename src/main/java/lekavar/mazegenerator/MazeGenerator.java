package lekavar.mazegenerator;

import lekavar.mazegenerator.block.MazeGeneratorBlock;
import lekavar.mazegenerator.item.MazeGeneratorBlockItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MazeGenerator implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("maze_generator");

	//maze generator
	public static final Block MAZE_GENERATOR = new MazeGeneratorBlock(FabricBlockSettings.of(Material.WOOD).hardness(1.0f));

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("mazegenerator", "maze_generator"), MAZE_GENERATOR);
		Registry.register(Registry.ITEM, new Identifier("mazegenerator", "maze_generator"), new MazeGeneratorBlockItem(MAZE_GENERATOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	}
}