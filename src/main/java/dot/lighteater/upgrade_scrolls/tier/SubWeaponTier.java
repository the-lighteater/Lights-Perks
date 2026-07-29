package dot.lighteater.upgrade_scrolls.tier;

import dot.lighteater.upgrade_scrolls.trait.Trait;
import net.minecraft.ChatFormatting;

import java.util.List;

public class SubWeaponTier {
    private String name;
    private int level;
    private List<Trait> tierTrait;
    private SubWeaponTier nextTier;
    private int damageToEvolve;
    private double bonusDamage;
    private double bonusAttackSpeed;
    private String tooltipString;
    private ChatFormatting color;

    public SubWeaponTier(String name, int level, List<Trait> tierTrait,
                         SubWeaponTier nextTier,
                         int damageToEvolve,
                         double bonusDamage,
                         double bonusAttackSpeed, String tooltipString,
                         ChatFormatting color) {

        this.name = name;
        this.level = level;
        this.tierTrait = tierTrait;
        this.nextTier = nextTier;
        this.damageToEvolve = damageToEvolve;
        this.bonusDamage = bonusDamage;
        this.bonusAttackSpeed = bonusAttackSpeed;
        this.tooltipString = tooltipString;
        this.color = color;
    }

    public void setNextTier(SubWeaponTier nextTier) { this.nextTier = nextTier; }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public List<Trait> getTierTrait() { return tierTrait; }
    public SubWeaponTier getNextTier() { return nextTier; }
    public int getDamageToEvolve() { return damageToEvolve; }
    public double getBonusDamage() { return bonusDamage; }
    public double getBonusAttackSpeed() { return bonusAttackSpeed; }
    public String getTooltipString() { return tooltipString; }
    public ChatFormatting getColor() { return color; }
}
