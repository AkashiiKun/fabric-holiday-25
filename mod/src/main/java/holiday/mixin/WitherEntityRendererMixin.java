package holiday.mixin;

import holiday.ClientEntrypoint;
import holiday.CommonEntrypoint;
import holiday.idkwheretoputthis.WitherEntityRendererExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.WitherEntityRenderer;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(WitherEntityRenderer.class)
public abstract class WitherEntityRendererMixin extends MobEntityRenderer<WitherEntity, WitherEntityRenderState, WitherEntityModel> implements WitherEntityRendererExtension {

    @Unique
    private WitherEntityModel tatherModel;

    @Unique
    private static final Identifier TINY_TATHER_TEXTURE = CommonEntrypoint.identifier("textures/entity/wither/tiny_tather.png");

    private WitherEntityRendererMixin(EntityRendererFactory.Context context, WitherEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void injectInit(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.tatherModel = new WitherEntityModel(context.getPart(ClientEntrypoint.TATHER_LAYER));
    }

    @Inject(
        method = "getTexture(Lnet/minecraft/client/render/entity/state/WitherEntityRenderState;)Lnet/minecraft/util/Identifier;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectGetTexture(WitherEntityRenderState witherEntityRenderState, CallbackInfoReturnable<Identifier> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.player.getEntityWorld() != null) {
            if (client.player.getEntityWorld().getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)) {
                //int i = MathHelper.floor(witherEntityRenderState.invulnerableTimer);
                //Identifier texture = i > 0 && (i > 80 || i / 5 % 2 != 1) ? FRIENDLY_INVULNERABLE_TEXTURE : FRIENDLY_TEXTURE;
                Identifier texture = TINY_TATHER_TEXTURE;

                cir.setReturnValue(texture);
            }
        }
    }

    @ModifyVariable(
        method = "scale(Lnet/minecraft/client/render/entity/state/WitherEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;)V",
        at = @At(
            value = "STORE",
            ordinal = 0
        ),
        index = 3
    )
    private float modifyScale(float scale) {
        return 1.5f;
    }

    @Override
    public WitherEntityModel fabric_holiday_25$getTatherModel() {
        return this.tatherModel;
    }
}
