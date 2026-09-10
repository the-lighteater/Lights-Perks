package dot.lighteater.lights_perks.menu;

import dot.lighteater.lights_perks.LightsPerks;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    ForgeRegistries.MENU_TYPES,
                    LightsPerks.MODID
            );

    public static final RegistryObject<MenuType<PerkMenu>> PERK_MENU =
            MENUS.register("perk_menu",
                    () -> IForgeMenuType.create(PerkMenu::new)
            );
}