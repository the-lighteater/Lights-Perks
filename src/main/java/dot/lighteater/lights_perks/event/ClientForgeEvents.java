package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.SkillActivationClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = LightsPerks.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        SkillActivationClientHandler.render(
                event.getGuiGraphics(),
                event.getWindow().getGuiScaledWidth(),
                event.getWindow().getGuiScaledHeight()
        );
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        SkillActivationClientHandler.tick();
    }
}