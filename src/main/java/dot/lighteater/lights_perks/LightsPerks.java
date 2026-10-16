package dot.lighteater.lights_perks;

import com.mojang.logging.LogUtils;
import dot.lighteater.lights_perks.block.ModBlocks;
import dot.lighteater.lights_perks.item.LoadItems;
import dot.lighteater.lights_perks.item.ModCreativeModTabs;
import dot.lighteater.lights_perks.item.ModItems;
import dot.lighteater.lights_perks.loot.ModLootModifiers;
import dot.lighteater.lights_perks.menu.ModMenus;
import dot.lighteater.lights_perks.perk.PerkLoader;
import dot.lighteater.lights_perks.skill.SkillLoader;
import dot.lighteater.lights_perks.skill.SkillRuntimeLoader;
import dot.lighteater.lights_perks.item_config.ItemConfigLoader;
import dot.lighteater.lights_perks.skill.bonus_skills.BonusLoader;
import dot.lighteater.lights_perks.skill.skill_sets.SkillSetLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.file.Path;

// Naming conventions and file management is credited to Kaupenjoe and his excellent 1.20.1 Forge Modding Tutorials.
// The link to his work will be credited in the description on CurseForge

@Mod(LightsPerks.MODID)
public class LightsPerks
{

    public static final String MODID = "lights_perks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LightsPerks(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModNetwork.init();

        ModMenus.MENUS.register(modEventBus);

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);

        ModBlocks.register(modEventBus);
        ModLootModifiers.register(modEventBus);

        SkillRuntimeLoader.loadSkills();

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);

        // Runtime resource pack
        modEventBus.addListener(this::addRuntimeResourcePack);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        context.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SkillLoader());
        SkillRuntimeLoader.loadSkills();
        event.addListener(new ItemConfigLoader());
        event.addListener(new PerkLoader());
        event.addListener(new BonusLoader());
        event.addListener(new SkillSetLoader());
    }

    private void addRuntimeResourcePack(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }

        Path runtimeRoot = FMLPaths.CONFIGDIR.get()
                .resolve("lights_perks")
                .resolve("generated_pack");

        event.addRepositorySource(consumer -> {

            Pack.ResourcesSupplier supplier =
                    id -> new PathPackResources(
                            id,
                            runtimeRoot,
                            true
                    );

            Pack pack = Pack.readMetaAndCreate(
                    "lights_perks_runtime",
                    Component.literal("Lights Perks Runtime"),
                    true,
                    supplier,
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN
            );

            if (pack != null) {
                consumer.accept(pack);

                LOGGER.info(
                        "Registered Lights Perks runtime resource pack at {}",
                        runtimeRoot
                );
            } else {
                LOGGER.error(
                        "Failed to create Lights Perks runtime resource pack."
                );
            }
        });
    }
}
