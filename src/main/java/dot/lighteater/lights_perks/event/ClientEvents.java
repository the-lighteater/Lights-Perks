package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.ModKeyBindings;
import dot.lighteater.lights_perks.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = LightsPerks.MODID,
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
                ModNetwork.sendOpenPerkMenu();
            }
        }
    }
}