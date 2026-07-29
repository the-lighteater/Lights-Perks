package dot.lighteater.upgrade_scrolls.event;

import dot.lighteater.upgrade_scrolls.ModKeyBindings;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.item.ModItems;
import dot.lighteater.upgrade_scrolls.menu.PerkScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

            ItemProperties.register(
                    ModItems.ECLIPSED_MACUAHUITL.get(),
                    new ResourceLocation(UpgradeScrolls.MODID, "tier"),
                    (stack, level, entity, seed) -> {

                        if (stack.getTag() == null) return 0.0F;

                        String tier = stack.getTag().getString("jalmw:weapon_tier");

                        return switch (tier) {
                            case "Shining" -> 1.0F;
                            case "Radiant" -> 2.0F;
                            default -> 0.0F;
                        };
                    }
            );

        });
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.OPEN_PERKS);
    }
}