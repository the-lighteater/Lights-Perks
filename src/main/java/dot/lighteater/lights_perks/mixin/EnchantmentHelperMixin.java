package dot.lighteater.lights_perks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dot.lighteater.lights_perks.UpgradeScrolls;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @ModifyReturnValue(
            method = "getItemEnchantmentLevel",
            at = @At("RETURN")
    )
    private static int upgradeScrolls$modifyFortune(
            int original,
            @Local(ordinal = 0, argsOnly = true) Enchantment enchantment,
            @Local(ordinal = 0, argsOnly = true) ItemStack stack
    ) {
        // Only modify Fortune
        if (enchantment != Enchantments.BLOCK_FORTUNE) {
            return original;
        }

        int bonus = 0;

        if (stack.is(Items.DIAMOND_PICKAXE)) {
            bonus += 3;
        }

        UpgradeScrolls.LOGGER.debug("Fortune Level: {}", bonus);

        return original + bonus;
    }
}