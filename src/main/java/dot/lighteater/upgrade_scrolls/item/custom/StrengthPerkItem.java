package dot.lighteater.upgrade_scrolls.item.custom;

import dot.lighteater.upgrade_scrolls.perk.IPerkItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class StrengthPerkItem extends Item implements IPerkItem {

    public StrengthPerkItem(Properties props) {
        super(props);
    }

    @Override
    public ResourceLocation getPerkId() {
        return new ResourceLocation("light_perks", "strength");
    }

    @Override
    public int getLevel(ItemStack stack) {
        return 1;
    }
}