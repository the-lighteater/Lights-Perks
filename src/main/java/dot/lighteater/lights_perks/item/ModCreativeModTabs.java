package dot.lighteater.lights_perks.item;

import dot.lighteater.lights_perks.LightsPerks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LightsPerks.MODID);

    public static final RegistryObject<CreativeModeTab> LIGHTS_PERKS = CREATIVE_MODE_TABS.register("lights_perks", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(ModItems.LOOTING_PERK.get())) // Testing
            .title(Component.translatable("creativetab.lights_perks"))
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> item : ModItems.ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
