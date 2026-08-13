package dot.lighteater.upgrade_scrolls.menu;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PerkContainer implements Container {

    private static final String SOCKETS_TAG = "light_perks:sockets";

    private final Player player;
    private final int size;

    public PerkContainer(Player player, int size) {
        this.player = player;
        this.size = size;

        UpgradeScrolls.LOGGER.debug(
                "[PerkContainer] Created for player '{}' with {} slots",
                player.getName().getString(),
                size
        );
    }

    /**
     * For now, all perk slots belong to the main-hand item.
     *
     * Later this will be replaced with equipment-specific
     * containers for helmet/chest/legs/boots/main-hand/off-hand.
     */
    private ItemStack getEquipment() {
        return player.getMainHandItem();
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < size; i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int index) {

        if (index < 0 || index >= size) {
            return ItemStack.EMPTY;
        }

        ItemStack equipment = getEquipment();

        if (equipment.isEmpty()) {
            return ItemStack.EMPTY;
        }

        CompoundTag tag = equipment.getTag();

        if (tag == null ||
                !tag.contains(SOCKETS_TAG, Tag.TAG_LIST)) {
            return ItemStack.EMPTY;
        }

        ListTag sockets = tag.getList(
                SOCKETS_TAG,
                Tag.TAG_COMPOUND
        );

        if (index >= sockets.size()) {
            return ItemStack.EMPTY;
        }

        CompoundTag socket = sockets.getCompound(index);

        if (!socket.contains("Item", Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = ItemStack.of(
                socket.getCompound("Item")
        );

        UpgradeScrolls.LOGGER.debug(
                "[PerkContainer] getItem({}): {} x{}",
                index,
                result.getItem(),
                result.getCount()
        );

        return result;
    }

    @Override
    public ItemStack removeItem(int index, int count) {

        ItemStack stack = getItem(index);

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result;

        if (stack.getCount() <= count) {
            result = stack;
            setItem(index, ItemStack.EMPTY);
        } else {
            result = stack.split(count);
            setItem(index, stack);
        }

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {

        ItemStack stack = getItem(index);

        setItem(index, ItemStack.EMPTY);

        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {

        if (index < 0 || index >= size) {
            return;
        }

        ItemStack equipment = getEquipment();

        if (equipment.isEmpty()) {
            return;
        }

        UpgradeScrolls.LOGGER.debug(
                "[PerkContainer] setItem({}, {} x{}) on {}",
                index,
                stack.getItem(),
                stack.getCount(),
                equipment.getItem()
        );

        CompoundTag tag = equipment.getOrCreateTag();

        ListTag sockets = tag.getList(
                SOCKETS_TAG,
                Tag.TAG_COMPOUND
        );

        while (sockets.size() <= index) {
            sockets.add(new CompoundTag());
        }

        CompoundTag socket = sockets.getCompound(index);

        if (stack.isEmpty()) {

            socket.remove("Item");

        } else {

            CompoundTag itemTag = new CompoundTag();

            stack.save(itemTag);

            socket.put(
                    "Item",
                    itemTag
            );
        }

        sockets.set(index, socket);

        tag.put(SOCKETS_TAG, sockets);

        equipment.setTag(tag);

        UpgradeScrolls.LOGGER.debug(
                "[PerkContainer] Finished writing socket {}",
                index
        );
    }

    @Override
    public void setChanged() {
        // Menu synchronization will handle this.
    }

    @Override
    public boolean stillValid(Player player) {
        return player == this.player;
    }

    @Override
    public void clearContent() {

        for (int i = 0; i < size; i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }
}