package dot.lighteater.lights_perks.menu;

import dot.lighteater.lights_perks.helpers.EquipmentType;
import dot.lighteater.lights_perks.perk.IPerkItem;
import dot.lighteater.lights_perks.perk.Perk;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PerkMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;

    private final PerkContainer[] perkContainers;

    public static final int EQUIPMENT_COUNT = 6;

    public static final int[] EQUIPMENT_X = {
            50,   // Helmet
            70,   // Chest
            90,   // Leggings
            110,  // Boots
            130,  // Main hand
            150   // Off hand
    };

    public static final int EQUIPMENT_Y = 20;

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
                new PerkContainer(inventory.player, EquipmentType.HELMET),
                new PerkContainer(inventory.player, EquipmentType.CHESTPLATE),
                new PerkContainer(inventory.player, EquipmentType.LEGGINGS),
                new PerkContainer(inventory.player, EquipmentType.BOOTS),
                new PerkContainer(inventory.player, EquipmentType.MAIN_HAND),
                new PerkContainer(inventory.player, EquipmentType.OFF_HAND)
        };

        /*
         * Add the dynamically-sized perk slots.
         */
        for (int i = 0; i < EQUIPMENT_COUNT; i++) {

            addPerkSlots(
                    perkContainers[i],
                    EQUIPMENT_X[i],
                    35
            );
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

    public Inventory getPlayerInventory() {
        return playerInventory;
    }

    private void addPerkSlots(
            PerkContainer container,
            int x,
            int y
    ) {
        int perkSlots = container.getContainerSize();

        for (int i = 0; i < perkSlots; i++) {
            addSlot(new PerkSlot(
                    container,
                    i,
                    x,
                    y + i * 20
            ));
        }
    }

    /**
     * Returns the number of perk slots currently
     * present in the menu.
     */
    public int getTotalPerkSlots() {

        int total = 0;

        for (PerkContainer container :
                perkContainers) {

            total += container.getSocketCount();
        }

        return total;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {

        Slot slot = slots.get(index);

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        int totalPerkSlots = getTotalPerkSlots();

        // Perk slot -> player inventory
        if (index < totalPerkSlots) {

            if (!moveItemStackTo(
                    stack,
                    totalPerkSlots,
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
                    totalPerkSlots,
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