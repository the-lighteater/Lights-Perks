package dot.lighteater.lights_perks;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LightsPerks.MODID, "main"),
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

        CHANNEL.registerMessage(
                id(),
                SkillActivationPacket.class,
                SkillActivationPacket::encode,
                SkillActivationPacket::new,
                SkillActivationPacket::handle
        );
    }

    public static void sendOpenPerkMenu() {
        LightsPerks.LOGGER.debug("Sending packet");
        CHANNEL.sendToServer(new OpenPerkMenuPacket());
    }

    public static void sendSkillActivation(
            net.minecraft.server.level.ServerPlayer player,
            String message,
            boolean activated
    ) {
        CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SkillActivationPacket(message, activated)
        );
    }
}