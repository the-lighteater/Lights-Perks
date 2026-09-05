package dot.lighteater.lights_perks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(
        modid = UpgradeScrolls.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER =
            new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue MOB_DROP_CHANCE;

    public static final ForgeConfigSpec.DoubleValue LOOT_TABLE_DROP_CHANCE;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("mob_drops");

        MOB_DROP_CHANCE = BUILDER
                .comment("Chance for an eligible mob to drop one random item from the drop pool.")
                .defineInRange(
                        "dropChance",
                        0.05,
                        0.0,
                        1.0
                );

        LOOT_TABLE_DROP_CHANCE = BUILDER
                .comment("Chance for an eligible loot table to contain one random item from the drop pool.")
                .defineInRange(
                        "lootTableDropChance",
                        0.05,
                        0.0,
                        1.0
                );

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}