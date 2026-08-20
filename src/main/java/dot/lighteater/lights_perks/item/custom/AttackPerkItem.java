package dot.lighteater.lights_perks.item.custom;

import dot.lighteater.lights_perks.perk.IPerkItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class AttackPerkItem extends Item implements IPerkItem {
    public AttackPerkItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ResourceLocation getSkillId() {
        return new ResourceLocation("lights_perks", "attack_up");
    }

    @Override
    public int getSkillPoints() {
        return 1;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
