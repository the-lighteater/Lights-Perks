package dot.lighteater.lights_perks.perk;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public interface IPerkItem {

    ResourceLocation getPerkId();
    PerkData getPerkData();
    int getLevel();
    Map<ResourceLocation, Integer> getSkills();
}
