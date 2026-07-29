package dot.lighteater.upgrade_scrolls.trait;

import net.minecraft.ChatFormatting;

public class ModWeaponTraits {

    public static final Trait glowing_3;
    public static final Trait glowing_2;
    public static final Trait glowing_1;
    public static final Trait explosion;

    static {
        glowing_3 = new GlowingTrait("Glow", 3, ChatFormatting.YELLOW);
        glowing_2 = new GlowingTrait("Glow", 2, ChatFormatting.YELLOW);
        glowing_1 = new GlowingTrait("Glow", 1, ChatFormatting.YELLOW);
        explosion = new ExplosionTrait("Explosion", 1, ChatFormatting.DARK_PURPLE);
    }
}
