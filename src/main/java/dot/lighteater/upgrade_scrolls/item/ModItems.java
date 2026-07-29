package dot.lighteater.upgrade_scrolls.item;

import com.railwayteam.railways.registry.CRCreativeModeTabs;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.armor.ItemTestingArmor;
import dot.lighteater.upgrade_scrolls.item.custom.ModularWeaponItem;
import dot.lighteater.upgrade_scrolls.item.custom.StrengthPerkItem;
import dot.lighteater.upgrade_scrolls.tier.ModWeaponTiers;
import dot.lighteater.upgrade_scrolls.tier.WeaponTier;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static dot.lighteater.upgrade_scrolls.trait.ModWeaponTraits.explosion;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UpgradeScrolls.MODID);

    public static final RegistryObject<Item> ECLIPSED_MACUAHUITL = ITEMS.register("eclipsed_macuahuitl",
            () -> new ModularWeaponItem(new WeaponTier("Eclipsed", 50, 4, 5, 5,
                    17, Items.DIAMOND, ModWeaponTiers.BRIGHT),
                    0, -2.4f, explosion, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TESTING_HELMET = ITEMS.register("testing_helmet",
            () -> new ItemTestingArmor(ArmorMaterials.IRON, ArmorItem.Type.HELMET));

    public static final RegistryObject<Item> TESTING_CHESTPLATE = ITEMS.register("testing_chestplate",
            () -> new ItemTestingArmor(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE));

    public static final RegistryObject<Item> TESTING_LEGGINGS = ITEMS.register("testing_leggings",
            () -> new ItemTestingArmor(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS));

    public static final RegistryObject<Item> TESTING_BOOTS = ITEMS.register("testing_boots",
            () -> new ItemTestingArmor(ArmorMaterials.IRON, ArmorItem.Type.BOOTS));

    public static final RegistryObject<Item> STRENGTH_PERK = ITEMS.register("strength_perk",
            () -> new StrengthPerkItem(new Item.Properties()));


    public static void register(IEventBus eventBus) {

        ITEMS.register(eventBus);

        LoadItems.load();

        for (ItemDefinition definition : LoadItems.getDefinitions().values()) {

            ITEMS.register(definition.id,
                    () -> new JsonItem(definition));
        }
    }
}
