package dot.lighteater.lights_perks.menu;

import dot.lighteater.lights_perks.event.ModEvents;
import dot.lighteater.lights_perks.perk.IPerkItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class PerkSlot extends Slot {

    private final PerkContainer perkContainer;

    public PerkSlot(
            PerkContainer container,
            int slot,
            int x,
            int y
    ) {
        super(container, slot, x, y);

        this.perkContainer = container;
    }

    public PerkContainer getPerkContainer() {
        return perkContainer;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        // No equipment in this equipment column
        if (!perkContainer.hasEquipment()) {
            return false;
        }

        // Equipment itself isn't configured as perkable
        if (!ModEvents.isPerkable(perkContainer.getEquipment())) {
            return false;
        }

        // The inserted item isn't a perk
        if (!(stack.getItem() instanceof IPerkItem perkItem)) {
            return false;
        }

        // Get this particular socket's level
        int socketLevel =
                perkContainer.getSocketLevel(getContainerSlot());

        // Perk must be <= socket level
        return perkItem.getLevel() <= socketLevel;
    }

    @Override
    public boolean mayPickup(Player player) {
        if (!perkContainer.hasEquipment()) {
            return false;
        }

        return super.mayPickup(player);
    }

    @Override
    public boolean isActive() {
        return perkContainer.hasEquipment();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}