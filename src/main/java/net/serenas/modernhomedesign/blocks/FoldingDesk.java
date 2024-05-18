package net.serenas.modernhomedesign.blocks;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.Explosion.DestructionType;
import net.minecraft.util.math.Direction;

public class FoldingDesk extends HorizontalFacingBlock implements Waterloggable {
    public static final MapCodec<FoldingDesk> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((block) -> {
         return block.blockSetType;
      }), createSettingsCodec()).apply(instance, FoldingDesk::new);
   });
   public static final BooleanProperty OPEN;
   public static final EnumProperty<BlockHalf> HALF;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty WATERLOGGED;
   protected static final int field_31266 = 3;
   protected static final VoxelShape EAST_SHAPE;
   protected static final VoxelShape WEST_SHAPE;
   protected static final VoxelShape SOUTH_SHAPE;
   protected static final VoxelShape NORTH_SHAPE;
   protected static final VoxelShape OPEN_BOTTOM_SHAPE;
   protected static final VoxelShape OPEN_TOP_SHAPE;
   private final BlockSetType blockSetType;

   @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

   public FoldingDesk(BlockSetType type, AbstractBlock.Settings settings) {
      super(settings.sounds(type.soundType()));
      this.blockSetType = type;
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(OPEN, false)).with(HALF, BlockHalf.BOTTOM)).with(POWERED, false)).with(WATERLOGGED, false));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      if (!(Boolean)state.get(OPEN)) {
         return state.get(HALF) == BlockHalf.TOP ? OPEN_TOP_SHAPE : OPEN_BOTTOM_SHAPE;
      } else {
         switch (((Direction)state.get(FACING)).ordinal()) {
            case 1:
            default:
               return NORTH_SHAPE;
            case 2:
               return SOUTH_SHAPE;
            case 3:
               return WEST_SHAPE;
            case 4:
               return EAST_SHAPE;
         }
      }
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      switch (type.ordinal()) {
         case 1:
            return (Boolean)state.get(OPEN);
         case 2:
            return (Boolean)state.get(WATERLOGGED);
         case 3:
            return (Boolean)state.get(OPEN);
         default:
            return false;
      }
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!this.blockSetType.canOpenByHand()) {
         return ActionResult.PASS;
      } else {
         this.flip(state, world, pos, player);
         return ActionResult.success(world.isClient);
      }
   }

   protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
      if (explosion.getDestructionType() == DestructionType.TRIGGER_BLOCK && !world.isClient() && this.blockSetType.canOpenByWindCharge() && !(Boolean)state.get(POWERED)) {
         this.flip(state, world, pos, (PlayerEntity)null);
      }

      super.onExploded(state, world, pos, explosion, stackMerger);
   }

   private void flip(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
      BlockState blockState = (BlockState)state.cycle(OPEN);
      world.setBlockState(pos, blockState, 2);
      if ((Boolean)blockState.get(WATERLOGGED)) {
         world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      this.playToggleSound(player, world, pos, (Boolean)blockState.get(OPEN));
   }

   protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
      world.playSound(player, pos, open ? this.blockSetType.trapdoorOpen() : this.blockSetType.trapdoorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
      world.emitGameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    if (!world.isClient) {
       boolean isPowered = world.isReceivingRedstonePower(pos);
       if (isPowered != (Boolean)state.get(POWERED)) {
          if ((Boolean)state.get(OPEN) != isPowered) {
             state = (BlockState)state.with(OPEN, isPowered);
             this.playToggleSound((PlayerEntity)null, world, pos, isPowered);
          }

          world.setBlockState(pos, (BlockState)state.with(POWERED, isPowered), 2);
          if ((Boolean)state.get(WATERLOGGED)) {
             world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
          }
       }

        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = this.getDefaultState();
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      Direction direction = ctx.getSide();
      if (!ctx.canReplaceExisting() && direction.getAxis().isHorizontal()) {
         blockState = (BlockState)((BlockState)blockState.with(FACING, direction)).with(HALF, ctx.getHitPos().y - (double)ctx.getBlockPos().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM);
      } else {
         blockState = (BlockState)((BlockState)blockState.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())).with(HALF, direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
      }

      if (ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())) {
         blockState = (BlockState)((BlockState)blockState.with(OPEN, true)).with(POWERED, true);
      }

      return (BlockState)blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, OPEN, HALF, POWERED, WATERLOGGED});
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
      if ((Boolean)state.get(WATERLOGGED)) {
         world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
   }

   protected BlockSetType getBlockSetType() {
      return this.blockSetType;
   }

   static {
      OPEN = Properties.OPEN;
      HALF = Properties.BLOCK_HALF;
      POWERED = Properties.POWERED;
      WATERLOGGED = Properties.WATERLOGGED;
      SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
      EAST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
      WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
      NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
      OPEN_BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
      OPEN_TOP_SHAPE = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
   }

}
