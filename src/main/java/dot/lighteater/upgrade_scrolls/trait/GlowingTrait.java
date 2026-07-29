package dot.lighteater.upgrade_scrolls.trait;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class GlowingTrait extends Trait{
    public GlowingTrait(String name, int level, ChatFormatting traitColor) {
        super(name, level, traitColor);
    }

    @Override
    public Trait upgrade() {
        return new GlowingTrait(getName(), getLevel() + 1, getTraitColor());
    }

    @Override
    public void onHit(TraitContext ctx) {
        int duration = 40 + (getLevel() * 20);
        int amplifier = getLevel() / 2;

        ctx.victim.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, amplifier));
    }

    @Override
    public void onCrit(TraitContext ctx) {
    }

    @Override
    public void onDeath(TraitContext ctx) {
        ctx.level.playLocalSound(ctx.victim.getX(), ctx.victim.getY(), ctx.victim.getY(), SoundEvents.TOTEM_USE, SoundSource.BLOCKS, 2, 2, true);
    }

    @Override
    public void onHurt(TraitContext ctx) {

    }

    @Override
    public void onTick(TraitContext ctx) {

    }
}
