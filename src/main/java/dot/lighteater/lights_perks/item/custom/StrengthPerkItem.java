package dot.lighteater.lights_perks.item.custom;

import dot.lighteater.lights_perks.perk.IPerkItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class StrengthPerkItem extends Item implements IPerkItem {

    public StrengthPerkItem(Properties props) {
        super(props);
    }

    @Override
    public ResourceLocation getPerkId() {
        return new ResourceLocation("lights_perks", "strength");
    }

    @Override
    public int getLevel(ItemStack stack) {
        return 1;
    }
}