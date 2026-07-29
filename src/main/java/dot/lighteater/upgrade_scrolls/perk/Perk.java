package dot.lighteater.upgrade_scrolls.perk;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class Perk {

    private final ResourceLocation id;
    private final Component displayName;
    private final int maxLevel;

    public Perk(ResourceLocation id, String name, int maxLevel) {
        this.id = id;
        this.displayName = Component.literal(name);
        this.maxLevel = maxLevel;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public abstract void apply(Player player, int level);
}