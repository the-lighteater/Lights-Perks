package dot.lighteater.upgrade_scrolls.perk;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PerkRegistry {

    private static final Map<ResourceLocation, Perk> PERKS = new HashMap<>();

    public static final Perk STRENGTH = register(new StrengthPerk());

    private static Perk register(Perk perk) {
        PERKS.put(perk.getId(), perk);
        return perk;
    }

    public static Perk get(ResourceLocation id) {
        return PERKS.get(id);
    }
}