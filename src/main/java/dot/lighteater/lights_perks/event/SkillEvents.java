package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.skill.SkillActivationManager;
import dot.lighteater.lights_perks.skill.SkillManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LightsPerks.MODID)
public class SkillEvents {
    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        SkillManager.tickPlayer(player);
    }

    @SubscribeEvent
    public static void onBlockMined(
            BlockEvent.BreakEvent event
    ) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        boolean changed = SkillActivationManager.trigger(
                player,
                "block_mined"
        );

        if (changed) {
            SkillManager.refreshPlayerSkillEffects(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(
            LivingHurtEvent event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        boolean changed = SkillActivationManager.trigger(
                player,
                "damage_taken"
        );

        if (changed) {
            SkillManager.refreshPlayerSkillEffects(player);
        }
    }

    @SubscribeEvent
    public static void onEntityKilled(
            LivingDeathEvent event
    ) {
        if (!(event.getSource().getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        boolean changed = SkillActivationManager.trigger(
                player,
                "entity_killed"
        );

        if (changed) {
            SkillManager.refreshPlayerSkillEffects(player);
        }
    }
}
