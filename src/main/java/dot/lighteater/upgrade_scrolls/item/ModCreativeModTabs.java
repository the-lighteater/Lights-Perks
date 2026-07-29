package dot.lighteater.upgrade_scrolls.item;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UpgradeScrolls.MODID);

    public static final RegistryObject<CreativeModeTab> UPGRADE_SCROLLS = CREATIVE_MODE_TABS.register("upgrade_scrolls", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> Items.DIAMOND_SWORD.getDefaultInstance()) // Testing
            .title(Component.translatable("creativetab.upgrade_scrolls"))
            .displayItems((parameters, output) -> {

                output.accept(ModItems.ECLIPSED_MACUAHUITL.get());

                output.accept(ModItems.STRENGTH_PERK.get());

            }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
