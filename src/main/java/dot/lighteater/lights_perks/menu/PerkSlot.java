package dot.lighteater.lights_perks.menu;

import dot.lighteater.lights_perks.perk.IPerkItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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
        if (!perkContainer.hasEquipment()) {
            return false;
        }

        return stack.getItem() instanceof IPerkItem;
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
}