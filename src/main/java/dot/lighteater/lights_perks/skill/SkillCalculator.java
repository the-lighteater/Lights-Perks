package dot.lighteater.lights_perks.skill;

import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.helpers.EquipmentType;
import dot.lighteater.lights_perks.perk.IPerkItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SkillCalculator {

    private SkillCalculator() {
    }

    public static Map<ResourceLocation, SkillData> calculate(
            Player player
    ) {
        Map<ResourceLocation, SkillData> skills =
                createRuntimeSkills();

        /*
         * Armor
         */
        processEquipment(
                player.getInventory().armor.get(3),
                EquipmentType.HELMET,
                skills
        );

        processEquipment(
                player.getInventory().armor.get(2),
                EquipmentType.CHESTPLATE,
                skills
        );

        processEquipment(
                player.getInventory().armor.get(1),
                EquipmentType.LEGGINGS,
                skills
        );

        processEquipment(
                player.getInventory().armor.get(0),
                EquipmentType.BOOTS,
                skills
        );

        /*
         * Weapons
         */
        processEquipment(
                player.getMainHandItem(),
                EquipmentType.MAIN_HAND,
                skills
        );

        processEquipment(
                player.getOffhandItem(),
                EquipmentType.OFF_HAND,
                skills
        );

        /*
         * Once all perk points have been collected,
         * calculate each skill's current level.
         */
        for (SkillData skill : skills.values()) {
            calculateCurrentLevel(skill);
        }

        return skills;
    }

    private static Map<ResourceLocation, SkillData>
    createRuntimeSkills() {

        Map<ResourceLocation, SkillData> skills =
                new HashMap<>();

        for (Map.Entry<ResourceLocation, SkillData> entry
                : SkillManager.getAllEntries()) {

            SkillData source = entry.getValue();

            SkillData runtimeSkill =
                    new SkillData(
                            source.title,
                            source.color,
                            source.maxLevel,
                            0,
                            source.bonusLevel,
                            source.levels,
                            new HashMap<>()
                    );

            skills.put(
                    entry.getKey(),
                    runtimeSkill
            );
        }

        return skills;
    }

    private static void processEquipment(
            ItemStack equipment,
            EquipmentType equipmentType,
            Map<ResourceLocation, SkillData> skills
    ) {
        if (equipment.isEmpty()) {
            return;
        }

        for (ItemStack perkStack : getPerkItems(equipment)) {

            if (perkStack.isEmpty()) {
                continue;
            }

            if (!(perkStack.getItem() instanceof IPerkItem perk)) {
                continue;
            }

            ResourceLocation skillId =
                    perk.getSkillId();

            SkillData skill =
                    skills.get(skillId);

            if (skill == null) {

                UpgradeScrolls.LOGGER.warn(
                        "[SkillCalculator] Unknown skill: {}",
                        skillId
                );

                continue;
            }

            skill.getPoints().merge(
                    equipmentType,
                    perk.getSkillPoints(),
                    Integer::sum
            );
        }
    }

    private static void calculateCurrentLevel(
            SkillData skill
    ) {
        int totalPoints = 0;

        for (int points : skill.getPoints().values()) {
            totalPoints += points;
        }

        skill.currLevel =
                Math.min(
                        totalPoints,
                        skill.maxLevel
                );
    }

    private static java.util.List<ItemStack> getPerkItems(
            ItemStack equipment
    ) {
        /*
         * TODO:
         *
         * Read your existing socket NBT here.
         */

        return java.util.List.of();
    }
}