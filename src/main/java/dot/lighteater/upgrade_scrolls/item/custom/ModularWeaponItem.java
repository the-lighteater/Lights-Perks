package dot.lighteater.upgrade_scrolls.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.tier.ModWeaponTiers;
import dot.lighteater.upgrade_scrolls.tier.SubWeaponTier;
import dot.lighteater.upgrade_scrolls.tier.WeaponTier;
import dot.lighteater.upgrade_scrolls.trait.Trait;
import dot.lighteater.upgrade_scrolls.trait.TraitContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class ModularWeaponItem extends SwordItem {
    private final WeaponTier tier;
    private static final String TAG_TIER = "jalmw:weapon_tier";
    private static final String TAG_DAMAGE = "jalmw:accumulated_damage";
    private final Trait weaponTrait;

    public ModularWeaponItem(WeaponTier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Trait weaponTrait, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        this.tier = pTier;
        this.weaponTrait = weaponTrait;
    }

    public WeaponData getWeaponData(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();

        WeaponData data = new WeaponData((WeaponTier) this.getTier()); // or stored WeaponTier

        data.setCurrentTierFromName(tag.getString(TAG_TIER));
        data.setAccumulatedDamage(tag.getInt(TAG_DAMAGE));

        return data;
    }

    @Override
    public Tier getTier() {
        return this.tier;
    }

    private void saveWeaponData(ItemStack stack, WeaponData data) {
        CompoundTag tag = stack.getOrCreateTag();

        tag.putString(TAG_TIER, data.getCurrentTier().getName());
        tag.putInt(TAG_DAMAGE, data.getAccumulatedDamage());
    }
    
    @Override
    public int getBarColor(ItemStack pStack) {
        float stackMaxDamage = (float)this.getMaxDamage(pStack);
        float f = Math.max(0.0F, (stackMaxDamage - (float)pStack.getDamageValue()) / stackMaxDamage);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        // Start with vanilla/base stats
        Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);

        if (slot != EquipmentSlot.MAINHAND) return modifiers;

        WeaponData data = getWeaponData(stack);
        SubWeaponTier tier = data.getCurrentTier();

        Multimap<Attribute, AttributeModifier> newModifiers = HashMultimap.create();
        newModifiers.putAll(modifiers);

        // Damage bonus
        newModifiers.put(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "Tier Damage Bonus",
                        tier.getBonusDamage(),
                        AttributeModifier.Operation.ADDITION
                ));

        // Attack speed bonus
        newModifiers.put(Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        UUID.fromString("22222222-2222-2222-2222-222222222222"),
                        "Tier Speed Bonus",
                        tier.getBonusAttackSpeed(),
                        AttributeModifier.Operation.ADDITION
                ));

        return newModifiers;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (!attacker.level().isClientSide) {
            TraitContext context = new TraitContext(attacker.level(), attacker, target, stack);
            WeaponData data = getWeaponData(stack);

            int damageDealt = (int) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);

            SubWeaponTier before = data.getCurrentTier();

            data.addDamage(damageDealt);

            SubWeaponTier after = data.getCurrentTier();

            saveWeaponData(stack, data);

            if (before != after && attacker instanceof Player player) {
                player.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.PLAYER_LEVELUP,
                        SoundSource.PLAYERS, 1.0f, 1.0f);
            }

            // Debug
//            System.out.println("Tier: " + data.getCurrentTier().getName() +
//                    " | Damage: " + data.getAccumulatedDamage());

            data.triggerOnHit(context);
            if (isCrit(attacker)) data.triggerOnCrit(context);
            if (!target.isAlive()) data.triggerOnDeath(context);

            if (weaponTrait != null) {
                weaponTrait.onHit(context);
                if (isCrit(attacker)) weaponTrait.onCrit(context);
                if (!target.isAlive()) weaponTrait.onDeath(context);
            }

        }

        return result;
    }

    private boolean isCrit(LivingEntity attacker) {
        return (attacker instanceof Player player && player.fallDistance > 0.0F
                && !player.onGround() && !player.isInWater());
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);

        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TAG_TIER, "Bright");
        tag.putInt(TAG_DAMAGE, 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        WeaponData data = getWeaponData(stack);

        addHeader(tooltip, data);
        addTraits(tooltip, data);
        addEvolution(tooltip, data);

        super.appendHoverText(stack, level, tooltip, flag);
    }

    private void addHeader(List<Component> tooltip, WeaponData data) {
        tooltip.add(Component.literal("Tier: ")
                .withStyle(ChatFormatting.GRAY)
                .append(title(data.getCurrentTier().getName().toUpperCase(), data.getCurrentTier().getColor())));

        tooltip.add(Component.literal(data.getCurrentTier().getTooltipString()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    private void addTraits(List<Component> tooltip, WeaponData data) {
        boolean expanded = Screen.hasShiftDown();
        tooltip.add(section("━━━━━━━━━━━━"));

        tooltip.add(Component.literal("Traits: ")
                .withStyle(ChatFormatting.GRAY));

        if (expanded) {
            tooltip.add(Component.literal(" • ")
                    .append(Component.literal(weaponTrait.getName())
                            .withStyle(weaponTrait.getTraitColor()))
                    .append(Component.literal(" [" + weaponTrait.getLevel() + "]")
                            .withStyle(ChatFormatting.DARK_GRAY)));            for (Trait trait : data.getTraits()) {
                tooltip.add(Component.literal(" • ")
                        .append(Component.literal(trait.getName())
                                .withStyle(trait.getTraitColor()))
                        .append(Component.literal(" [" + trait.getLevel() + "]")
                                .withStyle(ChatFormatting.DARK_GRAY)));
            }
        } else         tooltip.add(Component.literal("Hold SHIFT to inspect")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    private void addEvolution(List<Component> tooltip, WeaponData data) {
        if (data.getCurrentTier().getDamageToEvolve() < 0) return;

        tooltip.add(section("━━━━━━━━━━━━"));

        tooltip.add(Component.literal("Evolution")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(progressBar(data.getProgressToNextTier()));
    }

    private Component section(String text) {
        return Component.literal(text).withStyle(ChatFormatting.DARK_GRAY);
    }

    private Component title(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(color, ChatFormatting.BOLD);
    }

    private Component progressBar(float progress) {
        int size = 12;
        int filled = (int)(progress * size);

        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < size; i++) {
            bar.append(i < filled ? "§a█" : "§7█");
        }

        return Component.literal(bar.toString());
    }
}
