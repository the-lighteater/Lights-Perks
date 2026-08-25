package dot.lighteater.lights_perks.skill;

import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.helpers.EquipmentType;
import dot.lighteater.lights_perks.item_config.ItemConfigData;
import dot.lighteater.lights_perks.item_config.ItemConfigManager;
import dot.lighteater.lights_perks.perk.IPerkItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class SkillManager {

    private static final Map<ResourceLocation, SkillData> SKILLS =
            new HashMap<>();

    private static final String SOCKETS_TAG =
            "lights_perks:sockets";

    private static final String ITEM_TAG =
            "Item";

    private SkillManager() {
    }

    public static Collection<Map.Entry<ResourceLocation, SkillData>> getAllEntries() {
        return SKILLS.entrySet();
    }

    public static void register(
            ResourceLocation id,
            SkillData skill
    ) {
        SKILLS.put(id, skill);
    }

    public static void clear() {
        SKILLS.clear();
    }

    public static SkillData get(
            ResourceLocation id
    ) {
        return SKILLS.get(id);
    }

    public static boolean contains(
            ResourceLocation id
    ) {
        return SKILLS.containsKey(id);
    }

    public static Collection<SkillData> getAll() {
        return SKILLS.values();
    }

    public static Collection<ResourceLocation> getAllIds() {
        return SKILLS.keySet();
    }

    public static List<SkillData> getAllSkills() {
        List<SkillData> skills = new ArrayList<>();

        skills.addAll(SkillLoader.getAll());
        skills.addAll(SkillRuntimeLoader.getSkills());

        return skills;
    }

    /*
     * =========================================================
     * PLAYER SKILL DATA
     * =========================================================
     */

    /**
     * Calculates all skill points supplied by the player's
     * currently equipped perk items.
     */
    public static Map<ResourceLocation, Integer> getPlayerSkillPoints(
            Player player
    ) {
        Map<ResourceLocation, Integer> points =
                new HashMap<>();

        /*
         * Main hand
         */
        addEquipmentPoints(
                player,
                player.getMainHandItem(),
                EquipmentType.MAIN_HAND,
                points
        );

        /*
         * Off hand
         */
        addEquipmentPoints(
                player,
                player.getOffhandItem(),
                EquipmentType.OFF_HAND,
                points
        );

        /*
         * Armor
         */
        addEquipmentPoints(
                player,
                player.getInventory().armor.get(3),
                EquipmentType.HELMET,
                points
        );

        addEquipmentPoints(
                player,
                player.getInventory().armor.get(2),
                EquipmentType.CHESTPLATE,
                points
        );

        addEquipmentPoints(
                player,
                player.getInventory().armor.get(1),
                EquipmentType.LEGGINGS,
                points
        );

        addEquipmentPoints(
                player,
                player.getInventory().armor.get(0),
                EquipmentType.BOOTS,
                points
        );

        return points;
    }


    /**
     * Gets the number of points the player has for a specific skill.
     */
    public static int getPlayerSkillPoints(
            Player player,
            ResourceLocation skillId
    ) {
        return getPlayerSkillPoints(player)
                .getOrDefault(skillId, 0);
    }

    public static Map<ResourceLocation, Map<EquipmentType, Integer>>
        getPlayerSkillPointsByEquipment(Player player) {

        Map<ResourceLocation, Map<EquipmentType, Integer>> points =
                new HashMap<>();

        /*
         * Main hand
         */
        addEquipmentPointsByEquipment(
                player.getMainHandItem(),
                EquipmentType.MAIN_HAND,
                points
        );

        /*
         * Off hand
         */
        addEquipmentPointsByEquipment(
                player.getOffhandItem(),
                EquipmentType.OFF_HAND,
                points
        );

        /*
         * Armor
         */
        addEquipmentPointsByEquipment(
                player.getInventory().armor.get(3),
                EquipmentType.HELMET,
                points
        );

        addEquipmentPointsByEquipment(
                player.getInventory().armor.get(2),
                EquipmentType.CHESTPLATE,
                points
        );

        addEquipmentPointsByEquipment(
                player.getInventory().armor.get(1),
                EquipmentType.LEGGINGS,
                points
        );

        addEquipmentPointsByEquipment(
                player.getInventory().armor.get(0),
                EquipmentType.BOOTS,
                points
        );

        return points;
    }

    private static void addEquipmentPointsByEquipment(
            ItemStack equipment,
            EquipmentType equipmentType,
            Map<ResourceLocation, Map<EquipmentType, Integer>> points
    ) {
        if (equipment.isEmpty()) {
            return;
        }

        ItemConfigData itemData = ItemConfigManager.get(equipment);

        if (itemData != null) {
            for (Map.Entry<String, Integer> entry : itemData.builtin_skills.entrySet()) {
                Map<EquipmentType, Integer> equipmentPoints =
                        points.computeIfAbsent(
                                new ResourceLocation(entry.getKey()),
                                id -> new HashMap<>()
                        );

                equipmentPoints.merge(
                        equipmentType,
                        entry.getValue(),
                        Integer::sum
                );
            }
        }

        CompoundTag tag = equipment.getTag();

        if (tag == null ||
                !tag.contains(SOCKETS_TAG, Tag.TAG_LIST)) {
            return;
        }

        ListTag sockets =
                tag.getList(
                        SOCKETS_TAG,
                        Tag.TAG_COMPOUND
                );

        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket =
                    sockets.getCompound(i);

            if (!socket.contains(
                    ITEM_TAG,
                    Tag.TAG_COMPOUND
            )) {
                continue;
            }

            ItemStack perkStack =
                    ItemStack.of(
                            socket.getCompound(ITEM_TAG)
                    );

            if (perkStack.isEmpty()) {
                continue;
            }

            if (!(perkStack.getItem()
                    instanceof IPerkItem perkItem)) {
                continue;
            }

            for (Map.Entry<ResourceLocation, Integer> entry : perkItem.getSkills().entrySet()) {

                ResourceLocation skillId =
                        entry.getKey();

                int skillPoints =
                        entry.getValue();

                /*
                 * Get the equipment map for this skill.
                 * If it doesn't exist yet, create it.
                 */
                Map<EquipmentType, Integer> equipmentPoints =
                        points.computeIfAbsent(
                                skillId,
                                id -> new HashMap<>()
                        );

                /*
                 * Add this perk's points to the
                 * appropriate equipment slot.
                 */
                equipmentPoints.merge(
                        equipmentType,
                        skillPoints,
                        Integer::sum
                );
            }
        }
    }


    /*
     * =========================================================
     * EQUIPMENT
     * =========================================================
     */

    private static void addEquipmentPoints(
            Player player,
            ItemStack equipment,
            EquipmentType equipmentType,
            Map<ResourceLocation, Integer> points
    ) {
        if (equipment.isEmpty()) {
            return;
        }

        ItemConfigData itemData = ItemConfigManager.get(equipment);

        if (itemData != null) {
            for (Map.Entry<String, Integer> entry : itemData.builtin_skills.entrySet()) {

                points.merge(
                        new ResourceLocation(entry.getKey()),
                        entry.getValue(),
                        Integer::sum
                );
            }
        }

        CompoundTag tag = equipment.getTag();

        if (tag == null ||
                !tag.contains(SOCKETS_TAG, Tag.TAG_LIST)) {
            return;
        }

        ListTag sockets =
                tag.getList(
                        SOCKETS_TAG,
                        Tag.TAG_COMPOUND
                );

        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket =
                    sockets.getCompound(i);

            if (!socket.contains(
                    ITEM_TAG,
                    Tag.TAG_COMPOUND
            )) {
                continue;
            }

            ItemStack perkStack =
                    ItemStack.of(
                            socket.getCompound(ITEM_TAG)
                    );

            if (perkStack.isEmpty()) {
                continue;
            }

            if (!(perkStack.getItem()
                    instanceof IPerkItem perkItem)) {
                continue;
            }

            for (Map.Entry<ResourceLocation, Integer> entry : perkItem.getSkills().entrySet()) {


                ResourceLocation skillId =
                        entry.getKey();

                int skillPoints =
                        entry.getValue();

                /*
                 * Add this perk's points to the
                 * player's total for this skill.
                 */
                points.merge(
                        skillId,
                        skillPoints,
                        Integer::sum
                );

//            UpgradeScrolls.LOGGER.debug(
//                    "[SkillManager] {} provides {} point(s) to {} from {}",
//                    perkStack.getItem(),
//                    skillPoints,
//                    skillId,
//                    equipmentType
//            );
            }
        }
    }

    private static Map<ResourceLocation, Integer> debugPoints = null;

    public static void debugSkills(Player player) {

        Map<ResourceLocation, Integer> current =
                getPlayerSkillPoints(player);

        if (!current.equals(debugPoints)) {

            debugPoints = new HashMap<>(current);

            UpgradeScrolls.LOGGER.debug(
                    "[SkillManager] Player skill points: {}",
                    current
            );
        }
    }
}