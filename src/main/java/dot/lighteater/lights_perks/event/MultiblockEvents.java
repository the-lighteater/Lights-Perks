package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.block.MultiblockManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "lights_perks")
public class MultiblockEvents {

    @SubscribeEvent
    public static void onBlockChanged(
            BlockEvent.NeighborNotifyEvent event
    ) {

        Level level = (Level) event.getLevel();

        if (level.isClientSide) {
            return;
        }

        BlockPos changedPos = event.getPos();

        checkNearbyManager(level, changedPos);
    }

    private static void checkNearbyManager(
            Level level,
            BlockPos changedPos
    ) {

        MultiblockManager manager =
                MultiblockManager.findManager(
                        level,
                        changedPos
                );

        if (manager == null) {
            return;
        }

        manager.updateStates(level);
    }
}