package lekavar.mazegenerator.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MazeGeneratorBlockItem extends BlockItem {
    public MazeGeneratorBlockItem(Block block, Item.Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.maze_generator.tooltip_1").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.maze_generator.tooltip_2").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.maze_generator.tooltip_3").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.maze_generator.tooltip_4").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.maze_generator.tooltip_5").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.maze_generator.tooltip_6").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.maze_generator.tooltip_7").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.maze_generator.tooltip_8").withStyle(ChatFormatting.BLUE));
    }
}
