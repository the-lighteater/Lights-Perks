package dot.lighteater.lights_perks.slots;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemSlotManager {

    private static final Map<ResourceLocation, ItemSlotData> ITEMS =
            new HashMap<>();

    private ItemSlotManager() {
    }

    public static void clear() {
        ITEMS.clear();
    }

    public static void register(
            ResourceLocation id,
            ItemSlotData data
    ) {
        ITEMS.put(id, data);
    }

    public static ItemSlotData get(
            ResourceLocation id
    ) {
        return ITEMS.get(id);
    }

    public static boolean contains(
            ResourceLocation id
    ) {
        return ITEMS.containsKey(id);
    }

    public static Map<ResourceLocation, ItemSlotData> getAll() {
        return ITEMS;
    }

    public static ItemSlotData get(ItemStack stack) {

        if (stack.isEmpty()) {
            return null;
        }

        ResourceLocation id =
                net.minecraftforge.registries.ForgeRegistries.ITEMS
                        .getKey(stack.getItem());

        if (id == null) {
            return null;
        }

        return get(id);
    }
}