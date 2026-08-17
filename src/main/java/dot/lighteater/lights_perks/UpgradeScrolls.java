package dot.lighteater.lights_perks;

import com.mojang.logging.LogUtils;
import dot.lighteater.lights_perks.item.LoadItems;
import dot.lighteater.lights_perks.item.ModCreativeModTabs;
import dot.lighteater.lights_perks.item.ModItems;
import dot.lighteater.lights_perks.menu.ModMenus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// Naming conventions and file management is credited to Kaupenjoe and his excellent 1.20.1 Forge Modding Tutorials.
// The link to his work will be credited in the description on CurseForge

@Mod(UpgradeScrolls.MODID)
public class UpgradeScrolls
{

    public static final String MODID = "lights_perks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public UpgradeScrolls(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        LoadItems.load();

        ModNetwork.init();

        ModMenus.MENUS.register(modEventBus);

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
