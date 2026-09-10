package dot.lighteater.lights_perks;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = LightsPerks.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ModKeyBindings {

    public static final String CATEGORY =
            "key.categories.lights_perks";

    public static final KeyMapping OPEN_PERKS =
            new KeyMapping(
                    "key.lights_perks.open_perks",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_B,
                    CATEGORY
            );

    public static final KeyMapping TOGGLE_PERK_SUBMENU =
            new KeyMapping(
                    "key.light_perks.perk_submenu",
                    GLFW.GLFW_KEY_LEFT_SHIFT,
                    CATEGORY
            );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_PERKS);
        event.register(TOGGLE_PERK_SUBMENU);
    }
}