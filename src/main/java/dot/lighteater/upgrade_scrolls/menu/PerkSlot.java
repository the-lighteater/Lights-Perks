package dot.lighteater.upgrade_scrolls.menu;

import dot.lighteater.upgrade_scrolls.perk.IPerkItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PerkSlot extends Slot {

    public PerkSlot(
            Container container,
            int slot,
            int x,
            int y
    ) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof IPerkItem;
    }
}