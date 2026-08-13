package dot.lighteater.upgrade_scrolls.menu;

import dot.lighteater.upgrade_scrolls.perk.IPerkItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PerkMenu extends AbstractContainerMenu {

    private final PerkContainer perkContainer;

    /*
     * Slot layout
     *
     * 0-1   Helmet
     * 2-3   Chest
     * 4-5   Weapon
     *
     * 6-32  Player inventory
     * 33-41 Hotbar
     */

    public static final int HELMET_SLOT_START = 0;
    public static final int CHEST_SLOT_START = 1;
    public static final int WEAPON_SLOT_START = 2;

    public static final int PERK_SLOT_COUNT = 3;

    public PerkMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null);
    }

    public PerkMenu(
            int containerId,
            Inventory inventory,
            net.minecraft.network.FriendlyByteBuf data
    ) {
        super(ModMenus.PERK_MENU.get(), containerId);

        perkContainer = new PerkContainer(
                inventory.player,
                PERK_SLOT_COUNT
        );

        /*
         * Custom perk slots
         */
        for (int i = 0; i < PERK_SLOT_COUNT; i++) {

            int x;
            int y;

            /*
             * Temporary layout.
             *
             * We'll change these coordinates when we
             * redesign the screen.
             */
            if (i < 1) {
                // Helmet
                x = 50;
                y = 35;

            } else if (i < 2) {
                // Chest
                x = 50;
                y = 55;

            } else {
                // Weapon
                x = 50;
                y = 75;
            }

            addSlot(new PerkSlot(
                    perkContainer,
                    i,
                    x,
                    y
            ));
        }

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

    @Override
    public ItemStack quickMoveStack(Player player, int index) {

        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        /*
         * Custom perk slots
         */
        if (index < PERK_SLOT_COUNT) {

            if (!moveItemStackTo(
                    stack,
                    PERK_SLOT_COUNT,
                    slots.size(),
                    true
            )) {
                return ItemStack.EMPTY;
            }

        } else {

            /*
             * Player inventory -> perk slots
             */
            if (!(stack.getItem() instanceof IPerkItem)) {
                return ItemStack.EMPTY;
            }

            if (!moveItemStackTo(
                    stack,
                    0,
                    PERK_SLOT_COUNT,
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