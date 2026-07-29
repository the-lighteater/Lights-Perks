package dot.lighteater.upgrade_scrolls.event;

import dot.lighteater.upgrade_scrolls.ModKeyBindings;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.menu.PerkScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = UpgradeScrolls.MODID,
        value = Dist.CLIENT
)
public class ClientEvents {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft minecraft = Minecraft.getInstance();

        while (ModKeyBindings.OPEN_PERKS.consumeClick()) {

            if (minecraft.player != null) {
                minecraft.setScreen(new PerkScreen());
            }
        }
    }
}