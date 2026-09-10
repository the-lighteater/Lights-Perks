package dot.lighteater.lights_perks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = LightsPerks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> SYNC_PER_TICK_MANAGER;

    static {
        BUILDER.push("Skill Manager Sync");

        SYNC_PER_TICK_MANAGER = BUILDER.comment("How many ticks needs to elapse before the skill manager attempts to sync.")
                        .define("Sync Ticks Elapsed for Manager", 1);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
    }
}
