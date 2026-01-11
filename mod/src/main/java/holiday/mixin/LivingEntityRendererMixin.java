package holiday.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import holiday.idkwheretoputthis.WitherEntityRendererExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.WitherEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.dimension.DimensionTypes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @WrapOperation(
        method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;model:Lnet/minecraft/client/render/entity/model/EntityModel;",
            opcode = Opcodes.GETFIELD)
    )
    private M wrapGetModelField(LivingEntityRenderer<T, S, M> instance, Operation<M> original) {

        if (((Object) this) instanceof WitherEntityRenderer witherRenderer) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player != null && client.player.getEntityWorld() != null) {
                if (client.player.getEntityWorld().getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)) {
                    return (M) ((WitherEntityRendererExtension) witherRenderer).fabric_holiday_25$getTatherModel();
                }
            }
        }

        return original.call(instance);
    }

    @WrapOperation(
        method = "getRenderLayer",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;model:Lnet/minecraft/client/render/entity/model/EntityModel;",
            opcode = Opcodes.GETFIELD)
    )
    private M wrapGetModelField1(LivingEntityRenderer<T, S, M> instance, Operation<M> original) {

        if (((Object) this) instanceof WitherEntityRenderer witherRenderer) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player != null && client.player.getEntityWorld() != null) {
                if (client.player.getEntityWorld().getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)) {
                    return (M) ((WitherEntityRendererExtension) witherRenderer).fabric_holiday_25$getTatherModel();
                }
            }
        }

        return original.call(instance);
    }
}
