package holiday.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import holiday.block.entity.RemainderDispenserBlockEntity;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

@Mixin(ItemDispenserBehavior.class)
public abstract class ItemDispenserBehaviorMixin {
    @Shadow
    protected abstract ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack);

    @Shadow
    protected abstract void addStackOrSpawn(BlockPointer pointer, ItemStack stack);

    @Inject(
            method = "dispense",
            at = @At("HEAD"),
            cancellable = true
    )
    private void allowInducingSilently(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) {
        if (pointer.blockEntity() instanceof RemainderDispenserBlockEntity) {
            ci.setReturnValue(this.dispenseSilently(pointer, stack));
        }
    }

    @Inject(
            method = "dispenseSilently",
            at = @At("HEAD"),
            cancellable = true
    )
    private void avoidDispensingItemsFromInducer(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) {
        if (pointer.blockEntity() instanceof RemainderDispenserBlockEntity) {
            this.addStackOrSpawn(pointer, stack);
            ci.setReturnValue(ItemStack.EMPTY);
        }
    }
}
