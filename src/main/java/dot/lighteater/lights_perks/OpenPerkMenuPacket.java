package dot.lighteater.lights_perks;

import dot.lighteater.lights_perks.menu.PerkMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenPerkMenuPacket {

    public OpenPerkMenuPacket() {
    }

    public static void handle(
            OpenPerkMenuPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {

            LightsPerks.LOGGER.debug("Recieved packet");

            ServerPlayer player = context.getSender();

            if (player == null)
                return;

            LightsPerks.LOGGER.debug("Recieved packet 2");

            try {

                LightsPerks.LOGGER.debug("Before openScreen");

                NetworkHooks.openScreen(
                        player,
                        new MenuProvider() {

                            @Override
                            public Component getDisplayName() {
                                return Component.literal("Perks");
                            }

                            @Override
                            public AbstractContainerMenu createMenu(
                                    int containerId,
                                    Inventory inventory,
                                    Player player
                            ) {
                                LightsPerks.LOGGER.debug("Creating PerkMenu");

                                PerkMenu menu = new PerkMenu(
                                        containerId,
                                        inventory
                                );

                                LightsPerks.LOGGER.debug("PerkMenu created");

                                return menu;
                            }
                        }
                );

                LightsPerks.LOGGER.debug("After openScreen");

            } catch (Exception e) {

                LightsPerks.LOGGER.error(
                        "Failed to open PerkMenu",
                        e
                );
            }

            LightsPerks.LOGGER.debug("Recieved packet 4");
        });

        context.setPacketHandled(true);
    }
}