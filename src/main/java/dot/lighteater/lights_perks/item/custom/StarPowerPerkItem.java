package dot.lighteater.lights_perks.item.custom;

import com.github.alexthe666.citadel.repack.jcodec.common.DictionaryCompressor;
import dot.lighteater.lights_perks.perk.IPerkItem;
import dot.lighteater.lights_perks.skill.SkillData;
import dot.lighteater.lights_perks.skill.SkillManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class StarPowerPerkItem extends Item implements IPerkItem {
    public StarPowerPerkItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ResourceLocation getSkillId() {
        return new ResourceLocation("lights_perks", "star_power");
    }

    @Override
    public int getSkillPoints() {
        return 2;
    }

    @Override
    public int getLevel() {
        return 4;
    }

    @Override
    public Map<ResourceLocation, Integer> getSkills() {
        return Map.of(
                new ResourceLocation("lights_perks", "star_power"), 2,
                new ResourceLocation("lights_perks", "attack_up"), 1
        );
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
