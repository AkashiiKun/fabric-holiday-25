package holiday.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * A dispenser used for temporary storage of remainder items from dispenser behaviors used by inducers.
 */
public class RemainderDispenserBlockEntity extends DispenserBlockEntity {
    public RemainderDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean supports(BlockState state) {
        return true;
    }
}
