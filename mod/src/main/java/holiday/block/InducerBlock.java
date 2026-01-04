package holiday.block;

import com.mojang.serialization.MapCodec;

import holiday.block.entity.HolidayServerBlockEntityTypes;
import holiday.block.entity.InducerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;

public class InducerBlock extends BlockWithEntity {
    public static final MapCodec<InducerBlock> CODEC = createCodec(InducerBlock::new);

    // Use same property for compatibility with dispenser behaviors
    public static final EnumProperty<Direction> FACING = DispenserBlock.FACING;
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    public InducerBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ENABLED, true));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InducerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient()) {
            return validateTicker(type, HolidayServerBlockEntityTypes.INDUCER, InducerBlockEntity::tick);
        }

        return null;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState()
                .with(FACING, context.getPlayerLookDirection().getOpposite())
                .with(ENABLED, true);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.updateEnabled(world, pos, state);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, WireOrientation wireOrientation, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(World world, BlockPos pos, BlockState state) {
        boolean enabled = !world.isReceivingRedstonePower(pos);

        if (enabled != state.get(ENABLED)) {
            world.setBlockState(pos, state.with(ENABLED, enabled), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public MapCodec<? extends InducerBlock> getCodec() {
        return CODEC;
    }
}
