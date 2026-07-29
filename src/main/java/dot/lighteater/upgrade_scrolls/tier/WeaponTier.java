package dot.lighteater.upgrade_scrolls.tier;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class WeaponTier implements Tier {

    private final String name;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int level;
    private final int enchantmentValue;
    private final Item repairIngredient;

    private final SubWeaponTier startingTier;

    public WeaponTier(String name,
                      int uses,
                      float speed,
                      float attackDamageBonus,
                      int level,
                      int enchantmentValue,
                      Item repairIngredient,
                      SubWeaponTier startingTier) {

        this.name = name;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.level = level;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
        this.startingTier = startingTier;
    }

    public String getName() { return name; }
    public SubWeaponTier getStartingTier() { return startingTier; }

    @Override
    public int getUses() { return uses; }

    @Override
    public float getSpeed() { return speed; }

    @Override
    public float getAttackDamageBonus() { return attackDamageBonus; }

    @Override
    public int getLevel() { return level; }

    @Override
    public int getEnchantmentValue() { return enchantmentValue; }

    @Override
    public Ingredient getRepairIngredient() { return Ingredient.of(repairIngredient); }
}
