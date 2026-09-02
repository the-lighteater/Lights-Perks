package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.UpgradeScrolls;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkillEnchantmentEvents {

    @SubscribeEvent
    public static void onHarvestDrops(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        UpgradeScrolls.LOGGER.debug("[Skill Enchant Events] Fortune is: {}",
                player.getMainHandItem().getEnchantmentLevel(Enchantments.BLOCK_FORTUNE));

        int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getMainHandItem());

        UpgradeScrolls.LOGGER.debug("[Skill Enchant Events] Silk Touch is: {}",
                level);
    }

    @SubscribeEvent
    public static void onLootingLevel(LootingLevelEvent event) {
        UpgradeScrolls.LOGGER.debug("[Skill Enchant Events] Looting is: {}", event.getLootingLevel());
    }

    @SubscribeEvent
    public static void onPlayerXpChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();

        if (player == null) return;

        UpgradeScrolls.LOGGER.debug("[Skill Enchant Events] Experience gained is: {}", event.getAmount());
    }
}
