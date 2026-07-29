package dot.lighteater.upgrade_scrolls.perk;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class SwiftSneakPerk extends Perk{
    public SwiftSneakPerk(ResourceLocation id, String name, int maxLevel) {
        super(id, name, maxLevel);
    }

    @Override
    public void apply(Player player, int level) {
    }
}
