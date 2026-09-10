package dot.lighteater.lights_perks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dot.lighteater.lights_perks.LightsPerks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    private static final String PERK_ENCHANTS_TAG =
            "lights_perks:perk_enchants";

    @ModifyReturnValue(
            method = "getItemEnchantmentLevel",
            at = @At("RETURN")
    )
    private static int lightsPerks$modifyEnchantmentLevel(
            int original,
            @Local(ordinal = 0, argsOnly = true) Enchantment enchantment,
            @Local(ordinal = 0, argsOnly = true) ItemStack stack
    ) {

        CompoundTag tag =
                stack.getTag();

        if (tag == null ||
                !tag.contains(
                        PERK_ENCHANTS_TAG,
                        Tag.TAG_COMPOUND
                )) {
            return original;
        }

        ResourceLocation enchantmentId =
                ForgeRegistries.ENCHANTMENTS.getKey(enchantment);

        if (enchantmentId == null) {
            return original;
        }

        CompoundTag perkEnchants =
                tag.getCompound(
                        PERK_ENCHANTS_TAG
                );

        String key =
                enchantmentId.toString();

        if (!perkEnchants.contains(
                key,
                Tag.TAG_INT
        )) {
            return original;
        }

        int perkLevel =
                perkEnchants.getInt(key);

        int result =
                Math.max(
                        original,
                        perkLevel
                );

        LightsPerks.LOGGER.debug(
                "[EnchantmentHelperMixin] {} has {} level(s) from perks. Vanilla={}, Result={}",
                enchantmentId,
                perkLevel,
                original,
                result
        );

        return result;
    }
}