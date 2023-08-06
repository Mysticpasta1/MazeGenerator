package lekavar.mazegenerator.block;

import lekavar.mazegenerator.data.Maze;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MazeGeneratorBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("is_active");
    private static final int MAX_MAZE_SIZE = 2;
    public static final IntegerProperty MAZE_SIZE = IntegerProperty.create("maze_size", 0, MAX_MAZE_SIZE);

    public MazeGeneratorBlock(Properties settings) {
        super(settings);
        this.defaultBlockState().setValue(IS_ACTIVE, false)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(MAZE_SIZE, 0);
    }

    private int getSize(int mazeSize) {
        return switch (mazeSize) {
            case 1 -> 51;
            case 2 -> 101;
            default -> 31;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_ACTIVE, MAZE_SIZE, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getClickedFace());
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, world, pos, block, fromPos, notify);
        if (!world.isClientSide()) {
            boolean isReceivingRedstonePower = world.hasNeighborSignal(pos);
            if (isReceivingRedstonePower != state.getValue(IS_ACTIVE)) {
                if (isReceivingRedstonePower) {
                    /////////////////////////////////
                    int size = getSize(state.getValue(MAZE_SIZE));
                    Maze maze = new Maze(size, size);
                    maze.map = maze.init();
                    //maze.printMaze();
                    ////////////////////////////////
                    Random random = new Random();
                    Direction direction = state.getValue(FACING);
                    int stepX, stepZ;
                    stepX = stepZ = getSize(state.getValue(MAZE_SIZE));
                    int startX = pos.getX();
                    int startY = pos.getY();
                    int startZ = pos.getZ();
                    if (direction == Direction.NORTH) {
                        startX = startX + 1;
                        startZ = startZ + 1;
                    } else if (direction == Direction.EAST) {
                        startX = startX - stepX;
                        startZ = startZ + 1;
                        maze.map = maze.rotateMap(90);
                    } else if (direction == Direction.SOUTH) {
                        startX = startX - stepX;
                        startZ = startZ - stepZ;
                    } else if (direction == Direction.WEST) {
                        startX = startX + 1;
                        startZ = startZ - stepZ;
                        maze.map = maze.rotateMap(270);
                    }
                    int hiddenChestNum = 0;
                    List<Container> inventoryList = getNeighborChestAvaliableInventory(world, pos);
                    List<BlockPos> chestPos = new ArrayList<>();
                    hiddenChestNum = inventoryList.size();
                    int managedNum = 0;
                    if (hiddenChestNum != 0) {
                        try {
                            while (managedNum != hiddenChestNum) {
                                int neighborSpaceNum = 0;
                                int posX = 1 + random.nextInt(maze.map.length - 2);
                                int posZ = 1 + random.nextInt(maze.map.length - 2);
                                if (maze.map[posX][posZ] == 1)
                                    continue;
                                if (maze.map[posX - 1][posZ] == 1) {
                                    neighborSpaceNum += 1;
                                }
                                if (maze.map[posX][posZ - 1] == 1) {
                                    neighborSpaceNum += 1;
                                }
                                if (maze.map[posX + 1][posZ] == 1) {
                                    neighborSpaceNum += 1;
                                }
                                if (maze.map[posX][posZ + 1] == 1) {
                                    neighborSpaceNum += 1;
                                }
                                if (neighborSpaceNum > 1 && !chestPos.contains(new BlockPos(posX, startY + 1, posZ))) {
                                    chestPos.add(new BlockPos(startX + posX, startY + 1, startZ + posZ));
                                    managedNum++;
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Calculate chest pos error:" + e.getMessage());
                            hiddenChestNum = managedNum;
                        }
                    }
                    for (int i = 0; i < maze.map.length; i++) {
                        for (int j = 0; j < maze.map[0].length; j++) {
                            BlockPos currentPos1 = new BlockPos(startX + i, startY, startZ + j);
                            BlockPos currentPos2 = new BlockPos(startX + i, startY + 1, startZ + j);
                            BlockPos currentPos3 = new BlockPos(startX + i, startY + 2, startZ + j);
                            world.setBlock(currentPos1, Blocks.AIR.defaultBlockState(), 3);
                            if (world.getBlockState(currentPos2).getBlock().equals(Blocks.CHEST)) {
                                Container blockEntity = (Container) world.getBlockEntity(currentPos2);
                                for (int m = 0; m < Objects.requireNonNull(blockEntity).getContainerSize(); m++) {
                                    blockEntity.setItem(m, ItemStack.EMPTY);
                                }
                            }
                            world.setBlock(currentPos2, Blocks.AIR.defaultBlockState(), 3);
                            world.setBlock(currentPos3, Blocks.AIR.defaultBlockState(), 3);
                            if (maze.map[i][j] == 0) {
                                BlockState currentBlock = random.nextInt(5) == 0 ? Blocks.CRACKED_STONE_BRICKS.defaultBlockState() : Blocks.STONE_BRICKS.defaultBlockState();
                                world.setBlock(currentPos1, currentBlock, 3);

                                currentBlock = random.nextInt(5) == 0 ? Blocks.CRACKED_STONE_BRICKS.defaultBlockState() : Blocks.STONE_BRICKS.defaultBlockState();
                                world.setBlock(currentPos2, currentBlock, 3);

                                currentBlock = random.nextInt(5) == 0 ? Blocks.CRACKED_STONE_BRICKS.defaultBlockState() : Blocks.STONE_BRICKS.defaultBlockState();
                                world.setBlock(currentPos3, currentBlock, 3);
                            }
                        }
                    }
                    if (hiddenChestNum != 0) {
                        for (int k = 0; k < chestPos.size(); k++) {
                            world.setBlock(chestPos.get(k), Blocks.CHEST.defaultBlockState(), 2);
                            world.setBlock(new BlockPos(chestPos.get(k).getX(), chestPos.get(k).getY() + 1, chestPos.get(k).getZ()), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 2);
                            try {
                                BlockEntity blockEntity = world.getBlockEntity(chestPos.get(k));
                                if (blockEntity instanceof Container) {
                                    for (int n = 0; n < inventoryList.get(k).getContainerSize(); n++) {
                                        ((Container) blockEntity).setItem(n, inventoryList.get(k).getItem(n));
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("MazeGenerator, copy chest inventory error:" + e.getMessage());
                            }
                        }
                    }
                }
            }
            world.setBlock(pos, state.setValue(IS_ACTIVE, isReceivingRedstonePower), 3);
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!world.isClientSide()) {
            if (hand.equals(InteractionHand.MAIN_HAND)) {
                int currentMazeSize = state.getValue(MAZE_SIZE);
                int newMazeSize = (currentMazeSize + 1) % (MAX_MAZE_SIZE + 1);
                state = state.setValue(MAZE_SIZE, newMazeSize);
                world.setBlock(pos, state, 2);
                world.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.AMBIENT, 0.5f, 0.5f);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private List<Container> getNeighborChestAvaliableInventory(Level world, BlockPos pos) {
        List<Container> inventoryList = new ArrayList<>();

        BlockPos pos1 = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
        BlockPos pos2 = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
        BlockPos pos3 = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockPos pos4 = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        List<BlockPos> posList = Arrays.asList(pos1, pos2, pos3, pos4);

        for (BlockPos blockpos : posList) {
            Block block = world.getBlockState(blockpos).getBlock();
            if (block.equals(Blocks.CHEST)) {
                inventoryList.add(ChestBlock.getContainer((ChestBlock) block, world.getBlockState(blockpos), world, blockpos, true));
            }
        }
        return inventoryList;
    }
}
