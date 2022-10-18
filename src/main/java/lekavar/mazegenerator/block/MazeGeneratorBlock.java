package lekavar.mazegenerator.block;

import lekavar.mazegenerator.data.Maze;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MazeGeneratorBlock extends HorizontalFacingBlock {
    public static final BooleanProperty IS_ACTIVE = BooleanProperty.of("is_active");
    private static final int MAX_MAZE_SIZE = 2;
    public static final IntProperty MAZE_SIZE = IntProperty.of("maze_size", 0, MAX_MAZE_SIZE);

    public MazeGeneratorBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(IS_ACTIVE, false)
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(MAZE_SIZE, 0));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(IS_ACTIVE, MAZE_SIZE, Properties.HORIZONTAL_FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getPlayerFacing());
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (!world.isClient) {
            boolean isReceivingRedstonePower = world.isReceivingRedstonePower(pos);
            if (isReceivingRedstonePower != state.get(IS_ACTIVE)) {
                if (isReceivingRedstonePower) {
                    /////////////////////////////////
                    int size = getSize(state.get(MAZE_SIZE));
                    Maze maze = new Maze(size, size);
                    maze.map = maze.init();
                    //maze.printMaze();
                    ////////////////////////////////
                    Random random = new Random();
                    Direction direction = state.get(FACING);
                    int stepX, stepZ;
                    stepX = stepZ = getSize(state.get(MAZE_SIZE));
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
                    List<Inventory> inventoryList = getNeighborChestAvaliableInventory(world, pos);
                    List<BlockPos> chestPos = new ArrayList<>();
                    hiddenChestNum = inventoryList.size();
                    int managedNum = 0;
                    if (hiddenChestNum != 0) {
                        try {
                            while (managedNum != hiddenChestNum) {
                                int neighborSpaceNum = 0;
                                int posX = 1 + random.nextInt(maze.map.length - 2);
                                int posZ = 1 + random.nextInt(maze.map.length - 2);
                                if(maze.map[posX][posZ] == 1)
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
                            world.setBlockState(currentPos1, Blocks.AIR.getDefaultState());
                            if(world.getBlockState(currentPos2).getBlock().equals(Blocks.CHEST)){
                                Inventory blockEntity = (Inventory) world.getBlockEntity(currentPos2);
                                for (int m = 0; m < blockEntity.size(); m++) {
                                    blockEntity.setStack(m, ItemStack.EMPTY);
                                }
                            }
                            world.setBlockState(currentPos2, Blocks.AIR.getDefaultState());
                            world.setBlockState(currentPos3, Blocks.AIR.getDefaultState());
                            if (maze.map[i][j] == 0) {
                                BlockState currentBlock = random.nextInt(5) == 0 ? Blocks.CRACKED_STONE_BRICKS.getDefaultState() : Blocks.STONE_BRICKS.getDefaultState();
                                world.setBlockState(currentPos1, currentBlock);

                                currentBlock = random.nextInt(5) == 0 ? Blocks.CRACKED_STONE_BRICKS.getDefaultState() : Blocks.STONE_BRICKS.getDefaultState();
                                world.setBlockState(currentPos2, currentBlock);

                                currentBlock = random.nextInt(5) == 0 ? Blocks.CRACKED_STONE_BRICKS.getDefaultState() : Blocks.STONE_BRICKS.getDefaultState();
                                world.setBlockState(currentPos3, currentBlock);
                            }
                        }
                    }
                    if (hiddenChestNum != 0) {
                        for (int k = 0; k < chestPos.size(); k++) {
                            world.setBlockState(chestPos.get(k), Blocks.CHEST.getDefaultState());
                            world.setBlockState(new BlockPos(chestPos.get(k).getX(), chestPos.get(k).getY() + 1, chestPos.get(k).getZ()), Blocks.STONE_BRICK_STAIRS.getDefaultState());
                            try {
                                BlockEntity blockEntity = world.getBlockEntity(chestPos.get(k));
                                if (blockEntity instanceof Inventory) {
                                    for (int n = 0; n < inventoryList.get(k).size(); n++) {
                                        ((Inventory) blockEntity).setStack(n, inventoryList.get(k).getStack(n));
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("MazeGenerator, copy chest inventory error:" + e.getMessage());
                            }
                        }
                    }
                } else {
                }
            }
            world.setBlockState(pos, (BlockState) state.with(IS_ACTIVE, isReceivingRedstonePower), 3);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            if (hand.equals(Hand.MAIN_HAND)) {
                int currentMazeSize = state.get(MAZE_SIZE);
                int newMazeSize = (currentMazeSize + 1) % (MAX_MAZE_SIZE + 1);
                state = (BlockState) state.with(MAZE_SIZE, newMazeSize);
                world.setBlockState(pos, state, 2);
                world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.AMBIENT, 0.5f, 0.5f);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private int getSize(int mazeSize) {
        switch (mazeSize) {
            case 0:
                return 21;
            case 1:
                return 31;
            case 2:
                return 51;
            default:
                return 21;
        }
    }

    private List<Inventory> getNeighborChestAvaliableInventory(World world, BlockPos pos) {
        List<Inventory> inventoryList = new ArrayList<>();

        BlockPos pos1 = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
        BlockPos pos2 = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
        BlockPos pos3 = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockPos pos4 = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        List<BlockPos> posList = Arrays.asList(new BlockPos[]{pos1, pos2, pos3, pos4});

        for (int i = 0; i < posList.size(); i++) {
            Block block = world.getBlockState(posList.get(i)).getBlock();
            if (block.equals(Blocks.CHEST)) {
                inventoryList.add(ChestBlock.getInventory((ChestBlock) block, world.getBlockState(posList.get(i)), world, posList.get(i), true));
            }
        }
        return inventoryList;
    }
}
