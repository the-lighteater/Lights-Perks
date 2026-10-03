package dot.lighteater.lights_perks;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SkillActivationPacket {

    private final String message;
    private final boolean activated;

    public SkillActivationPacket(String message, boolean activated) {
        this.message = message;
        this.activated = activated;
    }

    public SkillActivationPacket(FriendlyByteBuf buffer) {
        this.message = buffer.readUtf(256);
        this.activated = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(message, 256);
        buffer.writeBoolean(activated);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT,
                        () -> () -> SkillActivationClientHandler.show(
                                message,
                                activated
                        )
                )
        );

        context.setPacketHandled(true);
    }
}