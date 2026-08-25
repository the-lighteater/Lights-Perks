package dot.lighteater.lights_perks.item.custom;

import dot.lighteater.lights_perks.perk.IPerkItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class StrengthPerkItem extends Item implements IPerkItem {

    public StrengthPerkItem(Properties props) {
        super(props);
    }

    @Override
    public ResourceLocation getSkillId() {
        return null;
    }

    @Override
    public int getSkillPoints() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public Map<ResourceLocation, Integer> getSkills() {
        return Map.of();
    }
}