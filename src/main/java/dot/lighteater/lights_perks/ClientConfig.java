package dot.lighteater.lights_perks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> TOGGLE_SKILLS_SUBMENU;

    public static final ForgeConfigSpec.ConfigValue<Integer> SYNC_PER_TICK_SCREEN;

    static {
        BUILDER.push("Visuals");

        TOGGLE_SKILLS_SUBMENU = BUILDER.comment("Enables toggle behavior for submenu, disabled means hold Submenu Key to open submenu")
                .define("Allows toggle", false);

        BUILDER.pop();

        BUILDER.push("Screen Sync");

        SYNC_PER_TICK_SCREEN = BUILDER.comment("How many ticks needs to elapse before the screen attempts to sync with skill manager.")
                        .define("Sync Ticks Elapsed", 1);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
    }
}
