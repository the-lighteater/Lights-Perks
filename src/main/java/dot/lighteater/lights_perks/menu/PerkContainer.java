package dot.lighteater.lights_perks.menu;

import dot.lighteater.lights_perks.helpers.EquipmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PerkContainer implements Container {

    private static final String SOCKETS_TAG = "lights_perks:sockets";
    private static final String ITEM_TAG = "Item";

    private final Player player;
    private final int size;
    private final EquipmentType equipmentType;

    public PerkContainer(
            Player player,
            int size,
            EquipmentType equipmentType
    ) {
        this.player = player;
        this.size = size;
        this.equipmentType = equipmentType;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public boolean hasEquipment() {
        return !getEquipment().isEmpty();
    }

    private ItemStack getEquipment() {
        return switch (equipmentType) {
            case HELMET -> player.getInventory().armor.get(3);
            case CHESTPLATE -> player.getInventory().armor.get(2);
            case LEGGINGS -> player.getInventory().armor.get(1);
            case BOOTS -> player.getInventory().armor.get(0);
            case MAIN_HAND -> player.getMainHandItem();
            case OFF_HAND -> player.getOffhandItem();
        };
    }

    private ListTag getSockets(ItemStack equipment) {
        if (equipment.isEmpty()) {
            return null;
        }

        CompoundTag tag = equipment.getTag();

        if (tag == null || !tag.contains(SOCKETS_TAG, Tag.TAG_LIST)) {
            return null;
        }

        return tag.getList(
                SOCKETS_TAG,
                Tag.TAG_COMPOUND
        );
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

        ListTag sockets = getSockets(getEquipment());

        if (sockets == null || index >= sockets.size()) {
            return ItemStack.EMPTY;
        }

        CompoundTag socket = sockets.getCompound(index);

        if (!socket.contains(ITEM_TAG, Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.of(
                socket.getCompound(ITEM_TAG)
        );
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = getItem(index);

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = stack.copy();

        if (stack.getCount() <= count) {
            setItem(index, ItemStack.EMPTY);
        } else {
            result.setCount(count);

            stack.shrink(count);
            setItem(index, stack);
        }

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = getItem(index);

        if (!stack.isEmpty()) {
            setItem(index, ItemStack.EMPTY);
        }

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
            socket.remove(ITEM_TAG);
        } else {
            CompoundTag itemTag = new CompoundTag();

            stack.save(itemTag);

            socket.put(
                    ITEM_TAG,
                    itemTag
            );
        }

        sockets.set(index, socket);
        tag.put(SOCKETS_TAG, sockets);
    }

    @Override
    public void setChanged() {
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