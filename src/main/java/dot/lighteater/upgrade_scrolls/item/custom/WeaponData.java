package dot.lighteater.upgrade_scrolls.item.custom;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.tier.SubWeaponTier;
import dot.lighteater.upgrade_scrolls.tier.WeaponTier;
import dot.lighteater.upgrade_scrolls.trait.Trait;
import dot.lighteater.upgrade_scrolls.trait.TraitContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class WeaponData {
    private WeaponTier weaponTier;
    private SubWeaponTier currentTier;
    private List<Trait> traits;
    private int accumulatedDamage;

    public WeaponData(WeaponTier weaponTier) {
        this.weaponTier = weaponTier;
        this.currentTier = weaponTier.getStartingTier();
        this.traits = new ArrayList<>();
        this.traits.addAll(currentTier.getTierTrait());
    }

    public void evolve() {
        if (currentTier.getNextTier() == null) return;

        currentTier = currentTier.getNextTier();

        // Upgrade traits
        List<Trait> newTraits = new ArrayList<>();
        for (Trait trait : traits) {
            newTraits.add(trait.upgrade());
        }

        // Add tier-specific trait
        newTraits.addAll(currentTier.getTierTrait());

        this.traits = newTraits;
    }

    public SubWeaponTier getCurrentTier() {
        return currentTier;
    }

    public List<Trait> getTraits() {
        return traits;
    }

    public void addDamage(int damage) {
        if (currentTier.getNextTier() == null) return;

        accumulatedDamage += damage;

        if (accumulatedDamage >= currentTier.getDamageToEvolve()) {
            evolve();
            accumulatedDamage = 0;
        }
    }

    public void setAccumulatedDamage(int damage) {
        this.accumulatedDamage = damage;
    }

    public float getProgressToNextTier() {
        if (currentTier.getNextTier() == null) return 1.0f;
        return (float) accumulatedDamage / currentTier.getDamageToEvolve();
    }

    public int getAccumulatedDamage() { return accumulatedDamage;}

    public void setCurrentTierFromName(String name) {
        SubWeaponTier current = weaponTier.getStartingTier();

        while (current != null) {
            if (current.getName().equals(name)) {
                this.currentTier = current;
                return;
            }
            current = current.getNextTier();
        }

        // fallback safety
        this.currentTier = weaponTier.getStartingTier();
    }

    public void triggerOnHit(TraitContext ctx) {
        for (Trait trait : traits) {
            UpgradeScrolls.LOGGER.debug("HIT TRIGGER");
            trait.onHit(ctx);
        }
    }

    public void triggerOnHurt(TraitContext ctx) {
        for (Trait trait : traits) {
            UpgradeScrolls.LOGGER.debug("HURT TRIGGER");
            trait.onHurt(ctx);
        }
    }

    public void triggerOnCrit(TraitContext ctx) {
        for (Trait trait : traits) {
            UpgradeScrolls.LOGGER.debug("CRIT TRIGGER");
            trait.onCrit(ctx);
        }
    }

    public void triggerOnTick(TraitContext ctx) {
        for (Trait trait : traits) {
            UpgradeScrolls.LOGGER.debug("TICK TRIGGER");
            trait.onTick(ctx);
        }
    }

    public void triggerOnDeath(TraitContext ctx) {
        for (Trait trait : traits) {
            UpgradeScrolls.LOGGER.debug("DEATH TRIGGER");
            trait.onDeath(ctx);
        }
    }
}