package dot.lighteater.upgrade_scrolls.armor;

import dot.lighteater.upgrade_scrolls.armor.model.ModelTestingArmor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemTestingArmor extends ArmorItem {
    private static final ModelTestingArmor INNER = new ModelTestingArmor(true);
    private static final ModelTestingArmor OUTER = new ModelTestingArmor(false);

    public ItemTestingArmor(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot, new Item.Properties());
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlot armorSlot,
                                                                   HumanoidModel<?> _default) {
                return (armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD)
                        ? INNER
                        : OUTER;            }
        });
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "upgrade_scrolls:textures/models/armor/" + (slot == EquipmentSlot.LEGS ? "armor_testing_layer_2" : "armor_testing_layer_1") + ".png";
    }
}
