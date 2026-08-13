package dot.lighteater.upgrade_scrolls;

import com.github.L_Ender.cataclysm.init.ModMenu;
import com.mojang.logging.LogUtils;
import dot.lighteater.upgrade_scrolls.item.LoadItems;
import dot.lighteater.upgrade_scrolls.item.ModCreativeModTabs;
import dot.lighteater.upgrade_scrolls.item.ModItems;
import dot.lighteater.upgrade_scrolls.menu.ModMenus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// Naming conventions and file management is credited to Kaupenjoe and his excellent 1.20.1 Forge Modding Tutorials.
// The link to his work will be credited in the description on CurseForge

@Mod(UpgradeScrolls.MODID)
public class UpgradeScrolls
{

    public static final String MODID = "upgrade_scrolls";
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
    }
}
