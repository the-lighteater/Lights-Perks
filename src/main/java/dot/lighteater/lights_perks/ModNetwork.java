package dot.lighteater.lights_perks;

import net.minecraft.resources.ResourceLocation;
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

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void init() {
        CHANNEL.registerMessage(
                id(),
                OpenPerkMenuPacket.class,
                (packet, buffer) -> {
                },
                buffer -> new OpenPerkMenuPacket(),
                OpenPerkMenuPacket::handle
        );
    }

    public static void sendOpenPerkMenu() {
        UpgradeScrolls.LOGGER.debug("Sending packet");
        CHANNEL.sendToServer(new OpenPerkMenuPacket());
    }
}