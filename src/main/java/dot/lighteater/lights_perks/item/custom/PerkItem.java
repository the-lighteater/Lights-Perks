package dot.lighteater.lights_perks.item.custom;

import dot.lighteater.lights_perks.LightsPerks;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerkItem extends Item implements IPerkItem {

    private final ResourceLocation perkId;
    private final String displayName;

    public PerkItem(
            Properties properties,
            ResourceLocation perkId,
            String displayName
    ) {
        super(properties);

        this.perkId = perkId;
        this.displayName = displayName;
    }

    public PerkItem(
            Properties properties,
            ResourceLocation perkId
    ) {
        super(properties);

        this.perkId = perkId;
        this.displayName = null;
    }

    public ResourceLocation getPerkId() {
        return perkId;
    }

    public PerkData getPerkData() {
        return PerkLoader.get(perkId);
    }

    @Override
    public Component getName(ItemStack stack) {

        if (displayName == null
                || displayName.isBlank()) {

            return super.getName(stack);
        }

        return Component.literal(displayName);
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
                new HashMap<>();

        for (Map.Entry<String, Integer> entry :
                data.skills.entrySet()) {

            result.put(
                    new ResourceLocation(entry.getKey()),
                    entry.getValue()
            );
        }

        return result;
    }

    public int getColor() {

        Map<ResourceLocation, Integer> skills =
                getSkills();

        if (skills.isEmpty()) {
            return 0xFFFFFFFF;
        }

        ResourceLocation primarySkillId =
                skills.keySet().iterator().next();

        SkillData skill =
                SkillManager.get(primarySkillId);

        if (skill == null
                || skill.color == null
                || skill.color.isBlank()) {

            return 0xFFFFFFFF;
        }

        try {

            return (int) Long.parseLong(
                    skill.color
                            .replace("0x", "")
                            .replace("#", ""),
                    16
            );

        } catch (NumberFormatException e) {

            return 0xFFFFFFFF;
        }
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        super.appendHoverText(
                stack,
                level,
                tooltip,
                flag
        );

        tooltip.add(
                Component.literal(
                        "Level: " + getLevel()
                )
        );

        for (Map.Entry<ResourceLocation, Integer> entry :
                getSkills().entrySet()) {

            ResourceLocation skillId =
                    entry.getKey();

            SkillData skillData =
                    SkillManager.get(skillId);

            if (skillData == null) {
                continue;
            }

            String skillName =
                    skillData.title;

            int skillLevel =
                    entry.getValue();

            int color;

            try {

                color = (int) Long.parseLong(
                        skillData.color
                                .replace("0x", "")
                                .replace("#", ""),
                        16
                );

            } catch (Exception e) {

                color = 0xFFFFFFFF;
            }

            int finalColor = color;
            tooltip.add(
                    Component.literal(skillName)
                            .withStyle(
                                    style ->
                                            style.withColor(finalColor)
                            )
                            .append(
                                    Component.literal(
                                            ": Level " +
                                                    skillLevel
                                    )
                            )
            );
        }
    }
}