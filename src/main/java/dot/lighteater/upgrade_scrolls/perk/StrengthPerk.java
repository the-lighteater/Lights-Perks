package dot.lighteater.upgrade_scrolls.perk;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class StrengthPerk extends Perk {

    public StrengthPerk() {
        super(
                new ResourceLocation("light_perks", "strength"),
                "Strength",
                5
        );
    }

    @Override
    public void apply(Player player, int level) {

        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_BOOST,
                5,
                level - 1,
                true,
                false,
                false
        ));
    }
}