package dot.lighteater.lights_perks.skill.bonus_skills;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public record EquipmentSnapshot(
        Item helmet,
        Item chestplate,
        Item leggings,
        Item boots,
        Item mainHand,
        Item offHand
) {

    public static EquipmentSnapshot from(Player player) {
        return new EquipmentSnapshot(
                player.getInventory().armor.get(3).getItem(),
                player.getInventory().armor.get(2).getItem(),
                player.getInventory().armor.get(1).getItem(),
                player.getInventory().armor.get(0).getItem(),
                player.getMainHandItem().getItem(),
                player.getOffhandItem().getItem()
        );
    }
}