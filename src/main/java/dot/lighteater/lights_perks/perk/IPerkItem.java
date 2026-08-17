package dot.lighteater.lights_perks.perk;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IPerkItem {
    ResourceLocation getPerkId();
    int getLevel(ItemStack stack);
}
