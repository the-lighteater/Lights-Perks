package dot.lighteater.lights_perks.item_config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemConfigManager {

    private static final Map<ResourceLocation, ItemConfigData> ITEMS =
            new HashMap<>();

    private ItemConfigManager() {
    }

    public static void clear() {
        ITEMS.clear();
    }

    public static void register(
            ResourceLocation id,
            ItemConfigData data
    ) {
        ITEMS.put(id, data);
    }

    public static ItemConfigData get(
            ResourceLocation id
    ) {
        return ITEMS.get(id);
    }

    public static boolean contains(
            ResourceLocation id
    ) {
        return ITEMS.containsKey(id);
    }

    public static Map<ResourceLocation, ItemConfigData> getAll() {
        return ITEMS;
    }

    public static ItemConfigData get(ItemStack stack) {

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