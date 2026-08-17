package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.menu.ModMenus;
import dot.lighteater.lights_perks.menu.PerkScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = UpgradeScrolls.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientSetup {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {

            MenuScreens.register(
                    ModMenus.PERK_MENU.get(),
                    PerkScreen::new
            );

        });
    }
}