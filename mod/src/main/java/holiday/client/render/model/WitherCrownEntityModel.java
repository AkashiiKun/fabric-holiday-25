package holiday.client.render.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class WitherCrownEntityModel extends PlayerEntityModel {
    public WitherCrownEntityModel(ModelPart root) {
        super(root, false);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = PlayerEntityModel.getTexturedModelData(Dilation.NONE, false);
        ModelPartData modelPartData = modelData.getRoot().resetChildrenParts();
        ModelPartData modelPartData2 = modelPartData.getChild(EntityModelPartNames.HEAD);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 0).cuboid(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, new Dilation(1.0F));
        modelPartData2.addChild(EntityModelPartNames.LEFT_EAR, modelPartBuilder, ModelTransform.origin(-6.0F, -6.0F, 0.0F));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_EAR, modelPartBuilder, ModelTransform.origin(6.0F, -6.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
