package dot.lighteater.lights_perks.skill.bonus_skills;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record BonusSkillEntry(
        ResourceLocation skill,
        int required,
        List<ResourceLocation> items
) {
}