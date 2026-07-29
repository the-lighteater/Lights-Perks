package dot.lighteater.upgrade_scrolls.trait;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TraitContext {
    public final Level level;
    public final LivingEntity attacker;
    public final LivingEntity victim;
    public final ItemStack stack;

    public TraitContext(Level level, LivingEntity attacker, LivingEntity victim, ItemStack stack) {
        this.level = level;
        this.attacker = attacker;
        this.victim = victim;
        this.stack = stack;
    }
}