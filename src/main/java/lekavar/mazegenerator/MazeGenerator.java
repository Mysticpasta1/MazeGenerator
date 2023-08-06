package lekavar.mazegenerator;

import lekavar.mazegenerator.block.MazeGeneratorBlock;
import lekavar.mazegenerator.item.MazeGeneratorBlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("mazegenerator")
public class MazeGenerator {
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS, "mazegenerator");
    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(ForgeRegistries.ITEMS, "mazegenerator");
    public static final RegistryObject<Block> MAZE_GENERATOR = BLOCK.register("maze_generator", () -> new MazeGeneratorBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(1.0f)));
    public static final RegistryObject<Item> MAZE_GENERATOR_ITEM = ITEM.register("maze_generator", () -> new MazeGeneratorBlockItem(MAZE_GENERATOR.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));

    public MazeGenerator() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCK.register(bus);
        ITEM.register(bus);
    }
}