package dot.lighteater.upgrade_scrolls.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class PerkScreen extends AbstractContainerScreen<PerkMenu> {

    private static final ResourceLocation SLOT_TEXTURE =
            new ResourceLocation(
                    "minecraft",
                    "textures/gui/container/inventory.png"
            );

    public PerkScreen(
            PerkMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);

        imageWidth = 176;
        imageHeight = 200;
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.renderBackground(graphics);

        /*
         * Main background
         */
        graphics.fill(
                leftPos,
                topPos,
                leftPos + imageWidth,
                topPos + imageHeight,
                0xFF202020
        );

        /*
         * Perk section
         */
        graphics.fill(
                leftPos + 4,
                topPos + 20,
                leftPos + 172,
                topPos + 95,
                0xFF303030
        );

        /*
         * Inventory section
         */
        graphics.fill(
                leftPos + 4,
                topPos + 93,
                leftPos + 172,
                topPos + 196,
                0xFF403030
        );

        /*
         * Draw slot backgrounds.
         *
         * The actual Slot objects are still responsible
         * for the items and interaction.
         */
        for (Slot slot : menu.slots) {

            drawSlotBackground(
                    graphics,
                    leftPos + slot.x - 1,
                    topPos + slot.y - 1
            );
        }
    }

    private void drawSlotBackground(
            GuiGraphics graphics,
            int x,
            int y
    ) {
        /*
         * Outer border
         */
        graphics.fill(
                x,
                y,
                x + 18,
                y + 18,
                0xFF101010
        );

        /*
         * Inner slot
         */
        graphics.fill(
                x + 1,
                y + 1,
                x + 17,
                y + 17,
                0xFF8B8B8B
        );

        /*
         * Inner shadow/highlight
         */
        graphics.fill(
                x + 2,
                y + 2,
                x + 16,
                y + 16,
                0xFF373737
        );
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        /*
         * Title
         */
        graphics.drawString(
                font,
                "Perks",
                8,
                6,
                0xFFFFFF
        );

        /*
         * Perk section
         */
        graphics.drawString(
                font,
                "Equipped Perks",
                8,
                22,
                0xFFFFFF
        );

        /*
         * Draw text next to each perk slot.
         */
        for (int i = 0; i < PerkMenu.PERK_SLOT_COUNT; i++) {

            Slot slot = menu.slots.get(i);

            Component text;

            if (slot.hasItem()) {
                text = slot.getItem().getHoverName();
            } else {
                text = Component.literal("Empty Slot");
            }

            graphics.drawString(
                    font,
                    text,
                    slot.x + 24,
                    slot.y + 5,
                    0xFFFFFF
            );
        }

        /*
         * Inventory section
         */
        graphics.drawString(
                font,
                "Inventory",
                8,
                97,
                0xFFFFFF
        );
    }

    @Override
    protected void renderTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        super.renderTooltip(graphics, mouseX, mouseY);
    }
}