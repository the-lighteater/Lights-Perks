package dot.lighteater.upgrade_scrolls;

import dot.lighteater.upgrade_scrolls.menu.PerkMenu;
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

            UpgradeScrolls.LOGGER.debug("Recieved packet");

            ServerPlayer player = context.getSender();

            if (player == null)
                return;

            UpgradeScrolls.LOGGER.debug("Recieved packet 2");

            try {

                UpgradeScrolls.LOGGER.debug("Before openScreen");

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
                                UpgradeScrolls.LOGGER.debug("Creating PerkMenu");

                                PerkMenu menu = new PerkMenu(
                                        containerId,
                                        inventory
                                );

                                UpgradeScrolls.LOGGER.debug("PerkMenu created");

                                return menu;
                            }
                        }
                );

                UpgradeScrolls.LOGGER.debug("After openScreen");

            } catch (Exception e) {

                UpgradeScrolls.LOGGER.error(
                        "Failed to open PerkMenu",
                        e
                );
            }

            UpgradeScrolls.LOGGER.debug("Recieved packet 4");
        });

        context.setPacketHandled(true);
    }
}