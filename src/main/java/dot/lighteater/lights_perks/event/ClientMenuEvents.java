package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.menu.skill_builder.SkillBuilderScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = LightsPerks.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientMenuEvents {

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TitleScreen)) return;

        int width = event.getScreen().width;
        int targetY = event.getScreen().height / 2 - 52;

        for (var widget : event.getScreen().children()) {
            if (widget instanceof AbstractWidget abstractWidget) {
                // Finds the y coords by using 'Mods' button, risky I know.
                if (abstractWidget.getMessage().getString().equals("Mods")) {
                    targetY = abstractWidget.getY();
                    break;
                }
            }
        }

        event.addListener(
                Button.builder(
                                Component.literal("Skill Builder"),
                                button -> openSkillBuilder()
                        ).bounds(
                                width / 2 - 124,
                                targetY,
                                20,
                                20
                        ).build());
    }

    private static void openSkillBuilder() {
        Minecraft.getInstance().setScreen(
                new SkillBuilderScreen()
        );
    }
}