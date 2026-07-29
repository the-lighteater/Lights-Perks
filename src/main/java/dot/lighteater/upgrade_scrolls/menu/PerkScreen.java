package dot.lighteater.upgrade_scrolls.menu;

import dot.lighteater.upgrade_scrolls.ModNetwork;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.perk.IPerkItem;
import dot.lighteater.upgrade_scrolls.perk.Perk;
import dot.lighteater.upgrade_scrolls.perk.PerkRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerkScreen extends Screen {

    private static final int WIDTH = 470;
    private static final int HEIGHT = 360;

    private int leftPos;
    private int topPos;

    // =========================
    // DRAG STATE MACHINE
    // =========================

    private enum DragType {
        NONE,
        INVENTORY_ITEM,
        SOCKET_ITEM
    }

    private DragType dragType = DragType.NONE;

    private ItemStack draggedItem = ItemStack.EMPTY;
    private int draggedInventorySlot = -1;

    private ItemStack draggedFromStack = ItemStack.EMPTY;
    private int draggedSocketIndex = -1;

    private SocketHit selectedSocket = null;

    private int dragX, dragY;

    // =========================
    // HITBOXES
    // =========================

    private record InventoryHit(ItemStack stack, int slot, int x, int y) {}
    private record SocketHit(ItemStack stack, int index, int x, int y) {}

    private final List<InventoryHit> inventoryHits = new ArrayList<>();
    private final List<SocketHit> socketHits = new ArrayList<>();

    public PerkScreen() {
        super(Component.literal("Perks"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        leftPos = (width - WIDTH) / 2;
        topPos = (height - HEIGHT) / 2;
    }

    // =========================================================
    // RENDER
    // =========================================================

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        renderBackground(graphics);

        drawWindow(graphics);
        drawPlayer(graphics, mouseX, mouseY);

        inventoryHits.clear();
        socketHits.clear();

        drawArmorSockets(graphics);
        drawWeaponSockets(graphics);
        drawInventoryPerks(graphics);

        // drag ghost
        if (dragType != DragType.NONE && !draggedItem.isEmpty()) {

            graphics.renderItem(draggedItem, dragX - 8, dragY - 8);

            graphics.fill(
                    dragX - 8,
                    dragY - 8,
                    dragX + 8,
                    dragY + 8,
                    0x55FFFFFF
            );
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawWindow(GuiGraphics graphics) {

        graphics.fill(leftPos, topPos, leftPos + WIDTH, topPos + HEIGHT, 0xCC202020);

        graphics.drawString(font, "Perks", leftPos + 8, topPos + 8, 0xFFFFFF);

        assert minecraft.player != null;
        graphics.drawString(font, minecraft.player.getDisplayName(), leftPos + 8, topPos + 20, 0xFFFFFF);

        graphics.fill(leftPos + 70, topPos + 20, leftPos + 71, topPos + HEIGHT - 10, 0xFF808080);
        graphics.fill(leftPos + 270, topPos + 20, leftPos + 271, topPos + HEIGHT - 10, 0xFF808080);
    }

    private void drawPlayer(GuiGraphics graphics, int mouseX, int mouseY) {

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                leftPos + 35,
                topPos + 120,
                40,
                leftPos + 35 - mouseX,
                topPos + 60 - mouseY,
                minecraft.player
        );
    }

    // =========================================================
    // INVENTORY PERKS (DRAG SOURCE)
    // =========================================================

    private void drawInventoryPerks(GuiGraphics graphics) {

        Player player = minecraft.player;

        int x = leftPos + 15;
        int y = topPos + 130;

        graphics.drawString(font, "Perks", x, y, 0xFFFFFF);

        y += 15;

        for (int i = 0; i < player.getInventory().items.size(); i++) {

            ItemStack stack = player.getInventory().items.get(i);

            if (!(stack.getItem() instanceof IPerkItem)) continue;

            inventoryHits.add(new InventoryHit(stack, i, x, y));

            graphics.renderItem(stack, x, y);

            y += 18;
        }
    }

    // =========================================================
    // ARMOR + WEAPON SOCKETS
    // =========================================================

    private void drawArmorSockets(GuiGraphics graphics) {

        Player player = minecraft.player;

        int x = leftPos + 80;
        int y = topPos + 10;

        for (int i = 3; i >= 0; i--) {

            ItemStack stack = player.getInventory().armor.get(i);
            if (!(stack.getItem() instanceof ArmorItem)) continue;

            drawSockets(graphics, stack, x, y);

            y += 90;
        }
    }

    private void drawWeaponSockets(GuiGraphics graphics) {

        Player player = minecraft.player;

        int x = leftPos + 290;

        graphics.drawString(font, "Main Hand", x, topPos + 20, 0xFFFFAA);
        drawSockets(graphics, player.getMainHandItem(), x, topPos + 35);

        graphics.drawString(font, "Off Hand", x, topPos + 110, 0xFFFFAA);
        drawSockets(graphics, player.getOffhandItem(), x, topPos + 125);
    }

    private void drawSockets(GuiGraphics graphics, ItemStack stack, int x, int y) {

        // header item
        graphics.renderItem(stack, x, y);
        graphics.drawString(font, stack.getHoverName(), x + 20, y, 0xFFFFFF);

        CompoundTag tag = stack.getTag();
        ListTag sockets = tag != null
                ? tag.getList("light_perks:sockets", Tag.TAG_COMPOUND)
                : new ListTag();

        int sy = y + 20;

        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket = sockets.getCompound(i);
            ItemStack socketItem = ItemStack.of(socket.getCompound("Item"));

            boolean empty = socketItem.isEmpty();

            // background slot box
            graphics.fill(x, sy, x + 170, sy + 16, 0x33000000);

            if (empty) {
                graphics.drawString(font, "Empty Slot", x + 20, sy + 4, 0x888888);
            } else {
                if (!(socketItem.getItem() instanceof IPerkItem perkedItem)) { return; }

                Perk perk = PerkRegistry.get(perkedItem.getPerkId());

                String name = perk.getDisplayName().getString();
                int level = perk.getMaxLevel();

                graphics.renderItem(socketItem, x, sy);

                graphics.drawString(
                        font,
                        name + " - Level " + level,
                        x + 20,
                        sy + 4,
                        0xFFFFFF
                );
            }

            // clickable hitbox
            socketHits.add(new SocketHit(stack, i, x, sy));

            sy += 18; // 👈 vertical stacking
        }
    }

    // =========================================================
    // INPUT
    // =========================================================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        // INVENTORY PICKUP
        for (InventoryHit hit : inventoryHits) {

            if (isInside(mouseX, mouseY, hit.x, hit.y)) {

                draggedItem = hit.stack.copy();
                draggedInventorySlot = hit.slot;
                dragType = DragType.INVENTORY_ITEM;

                return true;
            }
        }

        // SOCKET PICKUP
        for (SocketHit hit : socketHits) {

            ItemStack socketItem = getSocketItem(hit.stack, hit.index);

            if (socketItem.isEmpty()) continue;

            if (isInside(mouseX, mouseY, hit.x, hit.y)) {

                draggedItem = socketItem.copy();
                draggedFromStack = hit.stack;
                draggedSocketIndex = hit.index;

                dragType = DragType.SOCKET_ITEM;

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        dragX = (int) mouseX;
        dragY = (int) mouseY;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {

        if (dragType == DragType.NONE) return super.mouseReleased(mouseX, mouseY, button);

        // DROP INTO SOCKET
        for (SocketHit hit : socketHits) {

            if (isInside(mouseX, mouseY, hit.x, hit.y)) {

                ModNetwork.sendSocketUpdate(
                        getArmorSlot(hit.stack, minecraft.player),
                        hit.index,
                        draggedInventorySlot,
                        draggedItem
                );

                clearDrag();
                return true;
            }
        }

        // DROP BACK / REMOVE FROM SOCKET
        if (dragType == DragType.SOCKET_ITEM) {

            ModNetwork.sendSocketRemove(
                    getArmorSlot(draggedFromStack, minecraft.player),
                    draggedSocketIndex,
                    draggedItem
            );

            clearDrag();
            return true;
        }

        clearDrag();
        return true;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private boolean isInside(double mx, double my, int x, int y) {
        return mx >= x && mx <= x + 16 && my >= y && my <= y + 16;
    }

    private void clearDrag() {
        draggedItem = ItemStack.EMPTY;
        draggedInventorySlot = -1;
        draggedFromStack = ItemStack.EMPTY;
        draggedSocketIndex = -1;
        dragType = DragType.NONE;
    }

    private int getArmorSlot(ItemStack stack, Player player) {

        for (int i = 0; i < 4; i++) {
            if (player.getInventory().armor.get(i) == stack) return i;
        }

        if (stack == player.getMainHandItem()) return 4;
        if (stack == player.getOffhandItem()) return 5;

        return -1;
    }

    private ItemStack getSocketItem(ItemStack stack, int index) {

        CompoundTag tag = stack.getTag();
        if (tag == null) return ItemStack.EMPTY;

        ListTag sockets = tag.getList("light_perks:sockets", Tag.TAG_COMPOUND);
        if (index >= sockets.size()) return ItemStack.EMPTY;

        return ItemStack.of(
                sockets.getCompound(index).getCompound("Item")
        );
    }
}