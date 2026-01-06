package holiday.screen;

import holiday.CommonEntrypoint;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AttributeTableScreen extends HandledScreen<AttributeTableScreenHandler> {

    private static final Identifier TEXTURE = CommonEntrypoint.identifier("textures/gui/container/attribute_table.png");
    private static final Identifier REMOVE_TEXTURE = CommonEntrypoint.identifier("textures/gui/container/attribute_table_remove.png");
    private static final List<RegistryEntry<EntityAttribute>> ATTRIBUTES =
        Registries.ATTRIBUTE.stream()
            .map(RegistryEntry::of)
            .toList();

    private CyclingButtonWidget<RegistryEntry<EntityAttribute>> attributeButton;

    public AttributeTableScreen(AttributeTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        int modeButtonWidth = 20;
        int modeButtonHeight = 20;
        int modeButtonX = this.x + this.backgroundWidth - modeButtonWidth - 8;
        int modeButtonY = this.y + 4;

        addDrawableChild(
            CyclingButtonWidget.builder(
                    (Function<AttributeTableScreenHandler.Mode, Text>) (m) -> Text.literal(m.name().substring(0, 1)),
                    (Supplier<AttributeTableScreenHandler.Mode>) handler::getMode
                ).values(AttributeTableScreenHandler.Mode.values())
                .build(modeButtonX, modeButtonY, modeButtonWidth, modeButtonHeight, Text.literal(""), (btn, mode) -> handler.setMode(mode))
        );

        int attrButtonWidth = 110;
        int attrButtonHeight = 18;
        int attrButtonX = this.x + this.backgroundWidth - attrButtonWidth - 2;
        int attrButtonY = this.y + 64;


        attributeButton = new CyclingButtonWidget.Builder<>(
            attr -> Text.translatable(attr.value().getTranslationKey()),
            handler::getSelectedAttribute
        ).values(ATTRIBUTES)
            .build(
                attrButtonX,
                attrButtonY,
                attrButtonWidth,
                attrButtonHeight,
                Text.literal("Attribute"),
                (btn, attr) -> sendSelectedAttribute(attr)
            );

        addDrawableChild(attributeButton);
        attributeButton.visible = handler.getMode() == AttributeTableScreenHandler.Mode.REMOVE;
    }



    private void sendSelectedAttribute(RegistryEntry<EntityAttribute> attr) {
        ClientPlayNetworking.send(new CommonEntrypoint.SelectAttributePayload(handler.syncId, Registries.ATTRIBUTE.getId(attr.value())));
        handler.setSelectedAttribute(attr);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, handler.getMode() == AttributeTableScreenHandler.Mode.MERGE ? TEXTURE : REMOVE_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        if (attributeButton != null) attributeButton.visible = handler.getMode() == AttributeTableScreenHandler.Mode.REMOVE;
    }
}
