package dot.lighteater.upgrade_scrolls.item;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.item.custom.StrengthPerkItem;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UpgradeScrolls.MODID);

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
