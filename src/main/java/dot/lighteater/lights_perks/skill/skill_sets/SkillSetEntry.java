package dot.lighteater.lights_perks.skill.skill_sets;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SkillSetEntry (
            ResourceLocation skill,
            int required,
            int points,
            List<ResourceLocation> items
    ) {
    }
