package dot.lighteater.lights_perks.skill;

import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.helpers.EquipmentType;
import dot.lighteater.lights_perks.item_config.ItemConfigData;
import dot.lighteater.lights_perks.item_config.ItemConfigManager;
import dot.lighteater.lights_perks.perk.IPerkItem;
import dot.lighteater.lights_perks.skill.bonus_skills.BonusLoader;
import dot.lighteater.lights_perks.skill.bonus_skills.BonusSkillEntry;
import dot.lighteater.lights_perks.skill.bonus_skills.EquipmentSnapshot;
import dot.lighteater.lights_perks.skill.skill_sets.SkillSetEntry;
import dot.lighteater.lights_perks.skill.skill_sets.SkillSetLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class SkillManager {

    private static final Map<ResourceLocation, SkillData> SKILLS =
            new HashMap<>();

    private static final String SOCKETS_TAG =
            "lights_perks:sockets";

    private static final String ITEM_TAG =
            "Item";

    private static final Map<UUID, Map<ResourceLocation, Integer>>
            PLAYER_SKILL_POINTS = new HashMap<>();

    private static final Map<UUID, Map<ResourceLocation, Map<EquipmentType, Integer>>>
            PLAYER_EQUIPMENT_POINTS = new HashMap<>();

    private static final Map<UUID, Set<ResourceLocation>> BONUS_SKILL_CACHE =
            new HashMap<>();

    private static final Map<UUID, EquipmentSnapshot> BONUS_SKILL_SNAPSHOTS =
            new HashMap<>();

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

    public static void updatePlayerSkills(Player player) {

        UUID uuid = player.getUUID();

        Map<ResourceLocation, Integer> points =
                getPlayerSkillPoints(player);

        Map<ResourceLocation, Map<EquipmentType, Integer>> equipmentPoints =
                getPlayerSkillPointsByEquipment(player);

        PLAYER_SKILL_POINTS.put(
                uuid,
                points
        );

        PLAYER_EQUIPMENT_POINTS.put(
                uuid,
                equipmentPoints
        );

        updateBonusSkills(player);
    }

    public static Map<ResourceLocation, Integer> getPlayerSkillPointsScreen(
            Player player
    ) {
        if (player == null) {
            return Map.of();
        }

        return PLAYER_SKILL_POINTS.getOrDefault(
                player.getUUID(),
                Map.of()
        );
    }

    public static Map<ResourceLocation, Map<EquipmentType, Integer>> getPlayerEquipmentPointsScreen(
        Player player
    ) {
            if (player == null) {
                return Map.of();
            }

            return PLAYER_EQUIPMENT_POINTS.getOrDefault(
                    player.getUUID(),
                    Map.of()
            );
    }

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
         * Skill set bonuses
         */
        addEquipmentPointsBySets(
                player,
                points
        );

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

    public static void addEquipmentPointsBySets(
            Player player,
            Map<ResourceLocation, Integer> points
    ) {
        for (SkillSetEntry entry : SkillSetLoader.getEntries()) {

            int matches = 0;

            for (ItemStack stack : getEquipment(player)) {

                if (stack.isEmpty()) {
                    continue;
                }

                ResourceLocation itemId =
                        ForgeRegistries.ITEMS.getKey(stack.getItem());

                if (itemId != null
                        && entry.items().contains(itemId)) {

                    matches++;
                }
            }

            if (matches >= entry.required()) {

                points.merge(
                        entry.skill(),
                        entry.points(),
                        Integer::sum
                );
            }
        }
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

    public static void updateBonusSkills(Player player) {

        UUID uuid = player.getUUID();

        EquipmentSnapshot current =
                EquipmentSnapshot.from(player);

        EquipmentSnapshot previous =
                BONUS_SKILL_SNAPSHOTS.get(uuid);

        if (current.equals(previous)) {
            return;
        }

        BONUS_SKILL_SNAPSHOTS.put(uuid, current);

        Set<ResourceLocation> bonusSkills =
                new HashSet<>();

        for (BonusSkillEntry entry : BonusLoader.getEntries()) {

            int matches = 0;

            for (ItemStack stack : getEquipment(player)) {

                if (stack.isEmpty()) {
                    continue;
                }

                ResourceLocation itemId =
                        ForgeRegistries.ITEMS.getKey(stack.getItem());

                if (itemId != null
                        && entry.items().contains(itemId)) {

                    matches++;
                }
            }

            if (matches >= entry.required()) {
                bonusSkills.add(entry.skill());
            }
        }

        BONUS_SKILL_CACHE.put(uuid, bonusSkills);
    }

    public static boolean hasBonusSkill(
            Player player,
            ResourceLocation skill
    ) {
        updateBonusSkills(player);

        return BONUS_SKILL_CACHE
                .getOrDefault(
                        player.getUUID(),
                        Set.of()
                )
                .contains(skill);
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

    private static List<ItemStack> getEquipment(Player player) {
        return List.of(
                player.getInventory().armor.get(3),
                player.getInventory().armor.get(2),
                player.getInventory().armor.get(1),
                player.getInventory().armor.get(0),
                player.getMainHandItem(),
                player.getOffhandItem()
        );
    }
}