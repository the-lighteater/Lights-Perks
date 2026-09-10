package dot.lighteater.lights_perks.item.custom;

import dot.lighteater.lights_perks.perk.IPerkItem;
import dot.lighteater.lights_perks.perk.PerkData;
import dot.lighteater.lights_perks.perk.PerkLoader;
import dot.lighteater.lights_perks.skill.SkillData;
import dot.lighteater.lights_perks.skill.SkillManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PerkItem extends Item implements IPerkItem {

    private final ResourceLocation perkId;

    private final String color = "0xFF7A1030";

    public PerkItem(
            Properties properties,
            ResourceLocation perkId
    ) {
        super(properties);

        this.perkId = perkId;
    }

    public ResourceLocation getPerkId() {
        return perkId;
    }

    public PerkData getPerkData() {
        return PerkLoader.get(perkId);
    }

    public int getColor() {
        return (int) Long.parseLong(
                color.replace("0x", ""),
                16
        );
    }

    public int getLevel() {

        PerkData data = getPerkData();

        if (data == null) {
            return 0;
        }

        return data.level;
    }

    public Map<ResourceLocation, Integer> getSkills() {

        PerkData data = getPerkData();

        if (data == null || data.skills == null) {
            return Collections.emptyMap();
        }

        Map<ResourceLocation, Integer> result =
                new java.util.HashMap<>();

        for (Map.Entry<String, Integer> entry
                : data.skills.entrySet()) {

            result.put(
                    new ResourceLocation(entry.getKey()),
                    entry.getValue()
            );
        }

        return result;
    }

    @Override
    public void appendHoverText(
            ItemStack pStack,
            @Nullable Level pLevel,
            List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced
    ) {
        super.appendHoverText(
                pStack,
                pLevel,
                pTooltipComponents,
                pIsAdvanced
        );

        Map<ResourceLocation, Integer> skills = getSkills();

        if (skills == null) {
            return;
        }

        pTooltipComponents.add(
                Component.literal("Level: " + getLevel())
        );

        for (Map.Entry<ResourceLocation, Integer> entry : skills.entrySet()) {

            ResourceLocation skillId = entry.getKey();

            SkillData skillData = SkillManager.get(skillId);

            String skillName = skillData.title;
            int level = entry.getValue();

            int color = (int) Long.parseLong(
                    skillData.color.replace("0x", ""),
                    16
            );

            pTooltipComponents.add(
                    Component.literal(skillName)
                            .withStyle(style -> style.withColor(color))
                            .append(
                                    Component.literal(": Level " + level)
                            )
            );
        }
    }
}
