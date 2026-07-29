package dot.lighteater.upgrade_scrolls.event;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.item.custom.ModularWeaponItem;
import dot.lighteater.upgrade_scrolls.item.custom.WeaponData;
import dot.lighteater.upgrade_scrolls.perk.IPerkItem;
import dot.lighteater.upgrade_scrolls.perk.Perk;
import dot.lighteater.upgrade_scrolls.perk.PerkRegistry;
import dot.lighteater.upgrade_scrolls.trait.Trait;
import dot.lighteater.upgrade_scrolls.trait.TraitContext;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Events handled by the mod.

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        for (ItemStack stack : player.getInventory().items) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        applyPerks(player);
    }

    private static void applyPerks(Player player) {

        applyStack(player, player.getMainHandItem());
        applyStack(player, player.getOffhandItem());

        for (ItemStack armor : player.getInventory().armor) {
            applyStack(player, armor);
        }
    }

    private static void applyStack(Player player, ItemStack stack) {

        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("light_perks:sockets"))
            return;

        ListTag sockets = tag.getList("light_perks:sockets", Tag.TAG_COMPOUND);

        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket = sockets.getCompound(i);

            ItemStack socketItem = ItemStack.of(socket.getCompound("Item"));

            if (socketItem.isEmpty()) continue;

            if (socketItem.getItem() instanceof IPerkItem perkItem) {

                Perk perk = PerkRegistry.get(perkItem.getPerkId());

                if (perk != null) {
                    perk.apply(player, perkItem.getLevel(socketItem));
                }
            }
        }
    }

    private static boolean isPerkable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof ShieldItem;
    }

    public static void initializeSockets(ItemStack item) {

        CompoundTag tag = item.getOrCreateTag();

        if (!tag.contains("light_perks:sockets")) {

            ListTag sockets = new ListTag();

            for (int i = 0; i < 3; i++) {

                CompoundTag socket = new CompoundTag();
                socket.putInt("Slot", i);

                // empty itemstack
                socket.put("Item", new CompoundTag());

                sockets.add(socket);
            }

            tag.put("light_perks:sockets", sockets);
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();

        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("light_perks:perks"))
            return;

        event.getToolTip().add(Component.literal(""));
        event.getToolTip().add(Component.literal("Perk Slots"));

        ListTag slots = tag.getList("light_perks:perks", Tag.TAG_COMPOUND);

        for (int i = 0; i < slots.size(); i++) {

            CompoundTag slot = slots.getCompound(i);

            int level = slot.getInt("Level");

            event.getToolTip().add(
                    Component.literal("◇ Lv." + level + " ").append(
                            Component.translatable(slot.getString("Tooltip"))
                    )
            );
        }
    }

    private static CompoundTag createSlot(int level, String id, String translatable) {
        CompoundTag slot = new CompoundTag();

        slot.putInt("Level", level);
        slot.putString("Perk", id);
        slot.putString("Tooltip", translatable);

        return slot;
    }
}
