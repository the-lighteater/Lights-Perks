package dot.lighteater.upgrade_scrolls;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SocketUpdatePacket {

    private final int armorSlot;     // 0-3, 4 main, 5 offhand
    private final int socketIndex;
    private final int inventorySlot; // 👈 NEW
    private final ItemStack item;

    public SocketUpdatePacket(int armorSlot, int socketIndex, int inventorySlot, ItemStack item) {
        this.armorSlot = armorSlot;
        this.socketIndex = socketIndex;
        this.inventorySlot = inventorySlot;
        this.item = item;
    }

    public static void encode(SocketUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.armorSlot);
        buf.writeInt(msg.socketIndex);
        buf.writeInt(msg.inventorySlot);
        buf.writeItem(msg.item);
    }

    public static SocketUpdatePacket decode(FriendlyByteBuf buf) {
        return new SocketUpdatePacket(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readItem()
        );
    }

    public static void handle(SocketUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        UpgradeScrolls.LOGGER.debug("=== SocketUpdatePacket ===");
        UpgradeScrolls.LOGGER.debug("armorSlot={}, socketIndex={}, inventorySlot={}, item={}",
                msg.armorSlot,
                msg.socketIndex,
                msg.inventorySlot,
                msg.item);

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            UpgradeScrolls.LOGGER.debug("Sender={}", player.getGameProfile().getName());

            ItemStack target = getTargetStack(player, msg.armorSlot);
            if (target.isEmpty()) return;

            UpgradeScrolls.LOGGER.debug("Target stack={}", target);

            // CASE 1: REMOVE SOCKET → inventory
            if (msg.socketIndex >= 0 && msg.inventorySlot == -1) {

                UpgradeScrolls.LOGGER.debug("Removing socket {} from {}", msg.socketIndex, target);

                ItemStack removed = removeSocket(target, msg.socketIndex);
                UpgradeScrolls.LOGGER.debug("Removed item={}", removed);

                if (!removed.isEmpty()) {
                    // UpgradeScrolls.LOGGER.debug("Inventory add() result={}", player.getInventory().add(removed));
                    UpgradeScrolls.LOGGER.debug("Added item stack={}", removed);
                    // player.getInventory().add(removed);
                }

                return;
            }



            // CASE 2: INVENTORY → SOCKET
            if (msg.inventorySlot >= 0) {

                ItemStack invItem = player.getInventory().getItem(msg.inventorySlot);
                if (invItem.isEmpty()) return;

                applyToSocket(target, msg.socketIndex, invItem);

                invItem.shrink(1);
                if (invItem.isEmpty()) {
                    player.getInventory().setItem(msg.inventorySlot, ItemStack.EMPTY);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

    private static ItemStack removeSocket(ItemStack armor, int index) {

        var tag = armor.getOrCreateTag();
        var sockets = tag.getList("light_perks:sockets", Tag.TAG_COMPOUND);

        if (index >= sockets.size()) return ItemStack.EMPTY;

        CompoundTag socket = sockets.getCompound(index);

        ItemStack item = ItemStack.of(socket.getCompound("Item"));

        UpgradeScrolls.LOGGER.debug("Removed item from socket={}", item);

        socket.put("Item", new CompoundTag());
        sockets.set(index, socket);
        tag.put("light_perks:sockets", sockets);

        return item;
    }

        private static ItemStack getTargetStack(net.minecraft.world.entity.player.Player player, int slot) {

            if (slot >= 0 && slot < 4) {
                return player.getInventory().armor.get(slot);
            }

            if (slot == 4) return player.getMainHandItem();
            if (slot == 5) return player.getOffhandItem();

            return ItemStack.EMPTY;
        }

        private static void applyToSocket(ItemStack armor, int index, ItemStack item) {

            var tag = armor.getOrCreateTag();

            var sockets = tag.getList("light_perks:sockets", 10);

            if (index >= sockets.size()) return;

            var socket = sockets.getCompound(index);

            ItemStack existing = ItemStack.of(socket.getCompound("Item"));

            if (!existing.isEmpty())
                return;

            var itemTag = new net.minecraft.nbt.CompoundTag();
            item.save(itemTag);

            socket.put("Item", itemTag);
            sockets.set(index, socket);

            tag.put("light_perks:sockets", sockets);
        }
    }