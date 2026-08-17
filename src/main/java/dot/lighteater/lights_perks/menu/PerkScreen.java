package dot.lighteater.lights_perks.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import dot.lighteater.lights_perks.ClientConfig;
import dot.lighteater.lights_perks.ModKeyBindings;
import dot.lighteater.lights_perks.helpers.EquipmentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PerkScreen extends AbstractContainerScreen<PerkMenu> {

    private static final ResourceLocation SLOT_TEXTURE =
            new ResourceLocation(
                    "minecraft",
                    "textures/gui/container/inventory.png"
            );

    private boolean submenuOpen = false;

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
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.render(graphics, mouseX, mouseY, partialTick);

        renderEquipmentItems(graphics);

        renderPerkSubmenu(graphics, mouseX, mouseY);

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (ModKeyBindings.TOGGLE_PERK_SUBMENU.matches(
                keyCode,
                scanCode
        )) {

            if (ClientConfig.TOGGLE_SKILLS_SUBMENU.get()) {

                submenuOpen = !submenuOpen;

            } else {

                submenuOpen = true;
            }

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (ModKeyBindings.TOGGLE_PERK_SUBMENU.matches(
                keyCode,
                scanCode
        )) {

            if (!ClientConfig.TOGGLE_SKILLS_SUBMENU.get()) {
                submenuOpen = false;
            }

            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void renderPerkSubmenu(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        if (!submenuOpen) {
            return;
        }

        int width = 80;
        int height = 120;

        int x = leftPos - width - 4;
        int y = topPos + 20;

        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF202020
        );

        graphics.fill(
                x + 2,
                y + 2,
                x + width - 2,
                y + height - 2,
                0xFF303030
        );

        graphics.drawString(
                font,
                "Perks",
                x + 6,
                y + 6,
                0xFFFFFF
        );
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

        renderPlayerModel(graphics, mouseX, mouseY);

        for (Slot slot : menu.slots) {

            boolean enabled = true;

            if (slot instanceof PerkSlot perkSlot) {
                enabled = perkSlot.getPerkContainer().hasEquipment();
            }

            drawSlotBackground(
                    graphics,
                    leftPos + slot.x - 1,
                    topPos + slot.y - 1,
                    enabled
            );
        }
    }

    private void renderPlayerModel(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        int x = leftPos + 20;
        int y = topPos + 85;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                x,
                y,
                30,
                (float) (x - mouseX),
                (float) (y - mouseY),
                player
        );
    }

    private void drawSlotBackground(
            GuiGraphics graphics,
            int x,
            int y,
            boolean enabled
    ) {
        /*
         * Outer border
         */
        graphics.fill(
                x,
                y,
                x + 18,
                y + 18,
                enabled
                        ? 0xFF101010
                        : 0xFF080808
        );

        /*
         * Inner slot
         */
        graphics.fill(
                x + 1,
                y + 1,
                x + 17,
                y + 17,
                enabled
                        ? 0xFF8B8B8B
                        : 0xFF404040
        );

        /*
         * Inner shadow/highlight
         */
        graphics.fill(
                x + 2,
                y + 2,
                x + 16,
                y + 16,
                enabled
                        ? 0xFF373737
                        : 0xFF202020
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

    private void renderEquipmentItems(GuiGraphics graphics) {

        Inventory inventory = menu.getPlayerInventory();

        ItemStack[] equipment = {
                inventory.armor.get(3),
                inventory.armor.get(2),
                inventory.armor.get(1),
                inventory.armor.get(0),
                inventory.player.getMainHandItem(),
                inventory.player.getOffhandItem()
        };

        for (int i = 0; i < equipment.length; i++) {

            ItemStack stack = equipment[i];

            if (stack.isEmpty()) {
                continue;
            }

            graphics.renderItem(
                    stack,
                    leftPos + PerkMenu.EQUIPMENT_X[i],
                    topPos + PerkMenu.EQUIPMENT_Y
            );
        }
    }

    private ItemStack getEquipmentStack(EquipmentType type) {
        return switch (type) {
            case HELMET -> menu.getPlayerInventory().armor.get(3);
            case CHESTPLATE -> menu.getPlayerInventory().armor.get(2);
            case LEGGINGS -> menu.getPlayerInventory().armor.get(1);
            case BOOTS -> menu.getPlayerInventory().armor.get(0);
            case MAIN_HAND -> menu.getPlayerInventory().player.getMainHandItem();
            case OFF_HAND -> menu.getPlayerInventory().player.getOffhandItem();
        };
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