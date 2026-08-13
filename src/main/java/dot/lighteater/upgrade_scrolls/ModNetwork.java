package dot.lighteater.upgrade_scrolls;

import com.mojang.serialization.Decoder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(UpgradeScrolls.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int index = 0;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void init() {
        CHANNEL.messageBuilder(SocketUpdatePacket.class, index++)
                .encoder(SocketUpdatePacket::encode)
                .decoder(SocketUpdatePacket::decode)
                .consumerMainThread(SocketUpdatePacket::handle)
                .add();

        CHANNEL.registerMessage(
                id(),
                OpenPerkMenuPacket.class,
                (packet, buffer) -> {
                },
                buffer -> new OpenPerkMenuPacket(),
                OpenPerkMenuPacket::handle
        );
    }

    public static void sendSocketUpdate(int armorSlot, int socketIndex, int inventorySlot, ItemStack item) {

        CHANNEL.sendToServer(
                new SocketUpdatePacket(
                        armorSlot,
                        socketIndex,
                        inventorySlot,
                        item
                )
        );
    }

    public static void sendSocketRemove(
            int armorSlot,
            int socketIndex,
            ItemStack item
    ) {
        CHANNEL.sendToServer(new SocketUpdatePacket(
                armorSlot,
                socketIndex,
                -1,
                item
        ));
    }

    public static void sendOpenPerkMenu() {
        UpgradeScrolls.LOGGER.debug("Sending packet");
        CHANNEL.sendToServer(new OpenPerkMenuPacket());
    }
}