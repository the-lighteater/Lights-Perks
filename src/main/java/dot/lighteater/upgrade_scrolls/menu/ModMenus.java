package dot.lighteater.upgrade_scrolls.menu;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    ForgeRegistries.MENU_TYPES,
                    UpgradeScrolls.MODID
            );

    public static final RegistryObject<MenuType<PerkMenu>> PERK_MENU =
            MENUS.register("perk_menu",
                    () -> IForgeMenuType.create(PerkMenu::new)
            );
}