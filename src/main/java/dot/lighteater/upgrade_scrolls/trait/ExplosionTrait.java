package dot.lighteater.upgrade_scrolls.trait;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ExplosionTrait extends Trait{
    public ExplosionTrait(String name, int level, ChatFormatting traitColor) {
        super(name, level, traitColor);
    }

    @Override
    public Trait upgrade() {
        return new ExplosionTrait(getName(), getLevel() + 1, getTraitColor());
    }

    @Override
    public void onHit(TraitContext ctx) {
        int radius = 2 * getLevel();
        LivingEntity victim = ctx.victim;

        ctx.level.explode(victim, victim.getX(), victim.getY(), victim.getZ(), radius, true, Level.ExplosionInteraction.MOB);
    }

    @Override
    public void onCrit(TraitContext ctx) {

    }

    @Override
    public void onDeath(TraitContext ctx) {

    }

    @Override
    public void onHurt(TraitContext ctx) {

    }

    @Override
    public void onTick(TraitContext ctx) {

    }
}
