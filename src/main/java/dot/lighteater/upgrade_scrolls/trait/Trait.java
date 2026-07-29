package dot.lighteater.upgrade_scrolls.trait;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class Trait {
    private String name;
    private int level;
    private ChatFormatting traitColor;

    public Trait(String name, int level, ChatFormatting traitColor) {
        this.name = name;
        this.level = level;
        this.traitColor = traitColor;
    }

    public abstract Trait upgrade();

    public String getName() { return name; }
    public int getLevel() { return level; }
    public ChatFormatting getTraitColor() { return traitColor; }

    public abstract void onHit(TraitContext ctx);
    public abstract void onCrit(TraitContext ctx);
    public abstract void onDeath(TraitContext ctx);
    public abstract void onHurt(TraitContext ctx);
    public abstract void onTick(TraitContext ctx);
}
