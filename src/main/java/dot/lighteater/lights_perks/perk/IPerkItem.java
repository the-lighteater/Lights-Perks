package dot.lighteater.lights_perks.perk;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IPerkItem {
    ResourceLocation getSkillId();
    int getSkillPoints();
    int getLevel();
}
