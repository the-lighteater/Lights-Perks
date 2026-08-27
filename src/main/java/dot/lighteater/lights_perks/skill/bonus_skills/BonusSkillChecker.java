package dot.lighteater.lights_perks.skill.bonus_skills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class BonusSkillChecker {

    private static final Map<UUID, Set<ResourceLocation>> CACHE =
            new HashMap<>();

    private static final Map<UUID, EquipmentSnapshot> SNAPSHOTS =
            new HashMap<>();

    public static void update(Player player) {

        UUID uuid = player.getUUID();

        EquipmentSnapshot current =
                EquipmentSnapshot.from(player);

        EquipmentSnapshot previous =
                SNAPSHOTS.get(uuid);

        /*
         * Nothing changed, so don't recalculate.
         */
        if (current.equals(previous)) {
            return;
        }

        SNAPSHOTS.put(uuid, current);

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

        CACHE.put(uuid, bonusSkills);
    }

    public static boolean hasBonusSkill(
            Player player,
            ResourceLocation skill
    ) {
        return CACHE
                .getOrDefault(
                        player.getUUID(),
                        Set.of()
                )
                .contains(skill);
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