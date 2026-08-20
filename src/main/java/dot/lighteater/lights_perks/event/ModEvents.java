package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.perk.IPerkItem;
import dot.lighteater.lights_perks.perk.Perk;
import dot.lighteater.lights_perks.perk.PerkRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.TickEvent;
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

        if (tag == null || !tag.contains("lights_perks:sockets"))
            return;

        ListTag sockets = tag.getList("lights_perks:sockets", Tag.TAG_COMPOUND);

        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket = sockets.getCompound(i);

            ItemStack socketItem = ItemStack.of(socket.getCompound("Item"));

            if (socketItem.isEmpty()) continue;

            if (socketItem.getItem() instanceof IPerkItem perkItem) {
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

        if (!tag.contains("lights_perks:sockets")) {

            ListTag sockets = new ListTag();

            for (int i = 0; i < 3; i++) {

                CompoundTag socket = new CompoundTag();
                socket.putInt("Slot", i);

                // empty itemstack
                socket.put("Item", new CompoundTag());

                sockets.add(socket);
            }

            tag.put("lights_perks:sockets", sockets);
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();

        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("lights_perks:perks"))
            return;

        event.getToolTip().add(Component.literal(""));
        event.getToolTip().add(Component.literal("Perk Slots"));

        ListTag slots = tag.getList("lights_perks:perks", Tag.TAG_COMPOUND);

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
