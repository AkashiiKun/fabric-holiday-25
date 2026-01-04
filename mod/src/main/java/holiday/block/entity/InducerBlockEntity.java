package holiday.block.entity;

import holiday.block.InducerBlock;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class InducerBlockEntity extends BlockEntity {
    public InducerBlockEntity(BlockPos pos, BlockState state) {
        super(HolidayServerBlockEntityTypes.INDUCER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, InducerBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (!state.get(InducerBlock.ENABLED)) return;

        Direction facing = state.get(InducerBlock.FACING);
        Direction storageDirection = facing.getOpposite();

        BlockPos storagePos = pos.offset(storageDirection);
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(serverWorld, storagePos, storageDirection);

        if (storage == null) return;

        DispenserBlockEntity dispenser = new RemainderDispenserBlockEntity(pos, state);

        try (Transaction transaction = Transaction.openOuter()) {
            // Take an item to determine the dispenser behavior
            ResourceAmount<ItemVariant> item = StorageUtil.extractAny(storage, 1, transaction);

            if (item != null && item.amount() == 1) {
                ItemStack stack = item.resource().toStack();
                dispenser.setStack(0, stack);

                DispenserBehavior behavior = getBehaviorForItem(serverWorld, stack);

                if (behavior != null && behavior != DispenserBehavior.NOOP) {
                    BlockPointer pointer = new BlockPointer(serverWorld, pos, state, dispenser);
                    dispenser.setStack(0, behavior.dispense(pointer, stack));
                }
            }

            // Attempt to move remainders back into the storage
            Storage<ItemVariant> remainderStorage = InventoryStorage.of(dispenser, null);
            StorageUtil.move(remainderStorage, storage, itemx -> true, Long.MAX_VALUE, transaction);

            transaction.commit();
        }

        // If remainders somehow fail to be extracted, drop them in the world
        ItemScatterer.spawn(serverWorld, pos, dispenser);
    }

    private static DispenserBehavior getBehaviorForItem(World world, ItemStack stack) {
        if (!stack.isItemEnabled(world.getEnabledFeatures())) {
            return DispenserBlock.DEFAULT_BEHAVIOR;
        }

        DispenserBehavior behavior = DispenserBlock.BEHAVIORS.get(stack.getItem());
        return behavior != null ? behavior : DispenserBlock.getBehaviorForItem(stack);
    }
}
