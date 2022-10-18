package lekavar.mazegenerator.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MazeGeneratorBlockItem extends BlockItem {
    public MazeGeneratorBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.maze_generator.tooltip_1").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.maze_generator.tooltip_2").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.maze_generator.tooltip_3").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.maze_generator.tooltip_4").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.maze_generator.tooltip_5").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.maze_generator.tooltip_6").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.maze_generator.tooltip_7").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.maze_generator.tooltip_8").formatted(Formatting.BLUE));
    }
}
