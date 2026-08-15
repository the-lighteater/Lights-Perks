package dot.lighteater.upgrade_scrolls.menu;

import dot.lighteater.upgrade_scrolls.helpers.EquipmentType;
import dot.lighteater.upgrade_scrolls.perk.IPerkItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PerkMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;

    private final PerkContainer[] perkContainers;

    public static final int EQUIPMENT_COUNT = 6;
    public static final int PERK_SLOT_COUNT = 3;
    public static final int TOTAL_PERK_SLOTS =
            EQUIPMENT_COUNT * PERK_SLOT_COUNT;

    public static final int[] EQUIPMENT_X = {
            50,   // Helmet
            70,   // Chest
            90,   // Leggings
            110,  // Boots
            130,  // Main hand
            150   // Off hand
    };

    public static final int EQUIPMENT_Y = 15;

    public PerkMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null);
    }

    public PerkMenu(
            int containerId,
            Inventory inventory,
            net.minecraft.network.FriendlyByteBuf data
    ) {
        super(ModMenus.PERK_MENU.get(), containerId);

        this.playerInventory = inventory;

        perkContainers = new PerkContainer[] {
                new PerkContainer(inventory.player, PERK_SLOT_COUNT, EquipmentType.HELMET),
                new PerkContainer(inventory.player, PERK_SLOT_COUNT, EquipmentType.CHESTPLATE),
                new PerkContainer(inventory.player, PERK_SLOT_COUNT, EquipmentType.LEGGINGS),
                new PerkContainer(inventory.player, PERK_SLOT_COUNT, EquipmentType.BOOTS),
                new PerkContainer(inventory.player, PERK_SLOT_COUNT, EquipmentType.MAIN_HAND),
                new PerkContainer(inventory.player, PERK_SLOT_COUNT, EquipmentType.OFF_HAND)
        };

        addPerkSlots(perkContainers[0], EQUIPMENT_X[0], 35);
        addPerkSlots(perkContainers[1], EQUIPMENT_X[1], 35);
        addPerkSlots(perkContainers[2], EQUIPMENT_X[2], 35);
        addPerkSlots(perkContainers[3], EQUIPMENT_X[3], 35);
        addPerkSlots(perkContainers[4], EQUIPMENT_X[4], 35);
        addPerkSlots(perkContainers[5], EQUIPMENT_X[5], 35);

        /*
         * Player inventory
         */
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {

                addSlot(new Slot(
                        inventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        114 + row * 18
                ));
            }
        }

        /*
         * Hotbar
         */
        for (int column = 0; column < 9; column++) {

            addSlot(new Slot(
                    inventory,
                    column,
                    8 + column * 18,
                    172
            ));
        }
    }

    public Inventory getPlayerInventory() {
        return playerInventory;
    }

    private void addPerkSlots(
            PerkContainer container,
            int x,
            int y
    ) {
        for (int i = 0; i < PERK_SLOT_COUNT; i++) {
            addSlot(new PerkSlot(
                    container,
                    i,
                    x,
                    y + i * 20
            ));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {

        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        // Perk slot -> player inventory
        if (index < TOTAL_PERK_SLOTS) {

            if (!moveItemStackTo(
                    stack,
                    TOTAL_PERK_SLOTS,
                    slots.size(),
                    true
            )) {
                return ItemStack.EMPTY;
            }

        }
        // Player inventory -> perk slots
        else {

            if (!(stack.getItem() instanceof IPerkItem)) {
                return ItemStack.EMPTY;
            }

            if (!moveItemStackTo(
                    stack,
                    0,
                    TOTAL_PERK_SLOTS,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}