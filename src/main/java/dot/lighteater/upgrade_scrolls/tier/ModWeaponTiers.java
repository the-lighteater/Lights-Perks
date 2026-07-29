package dot.lighteater.upgrade_scrolls.tier;

import net.minecraft.ChatFormatting;
import java.util.List;

import static dot.lighteater.upgrade_scrolls.trait.ModWeaponTraits.*;

public class ModWeaponTiers {

    public static final SubWeaponTier BRIGHT;
    public static final SubWeaponTier SHINING;
    public static final SubWeaponTier RADIANT;

    static {
        RADIANT = new SubWeaponTier(
                "Radiant",
                3,
                (List.of(glowing_3)),
                null, -1, 4, 1,
                "This weapon burns as bright as the morning sun.", ChatFormatting.GOLD
        );

        SHINING = new SubWeaponTier(
                "Shining",
                2,
                (List.of(glowing_2)),
                RADIANT, 250, 2, .5,
                "Their light will purge the evil.", ChatFormatting.YELLOW
        );

        BRIGHT = new SubWeaponTier(
                "Bright",
                1,
                (List.of(glowing_1)),
                SHINING, 100, 0, 0,
                "Light the end of the tunnel.", ChatFormatting.WHITE
        );
    }
}