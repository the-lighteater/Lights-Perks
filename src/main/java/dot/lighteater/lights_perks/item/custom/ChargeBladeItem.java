package dot.lighteater.lights_perks.item.custom;

import com.github.alexthe666.citadel.repack.jcodec.common.DictionaryCompressor;
import dot.lighteater.lights_perks.LightsPerks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import java.util.HashMap;
import java.util.Map;


public class ChargeBladeItem extends SwordItem {

    public enum ChargeBladeVialTypes {
        EMPTY,
        CHARGED,
        OVERCHARGED
    }

    private int vialCount;
    private float vialCharge;
    private float vialChargeAmount = 20;

    private Map<Integer, ChargeBladeVialTypes> vialMap = new HashMap<>(
    );

    public ChargeBladeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);

        for (int i = 0; i < 5; i++) {
            vialMap.put(i, ChargeBladeVialTypes.EMPTY);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        vialCharge += this.getDamage();

        LightsPerks.LOGGER.debug("Vial Charge is {}, vial count is {}", vialCharge, vialChargeToVials());

        return super.hurtEnemy(pStack, pTarget, pAttacker);

    }

    private int vialChargeToVials() {
        int vials = (int) (vialCharge / vialChargeAmount);

        return Math.min(vials, 5);
    }
}
