package dot.lighteater.lights_perks.slots;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import dot.lighteater.lights_perks.UpgradeScrolls;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemSlotLoader extends SimpleJsonResourceReloadListener {

    public static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Type ITEM_MAP_TYPE =
            new TypeToken<Map<String, ItemSlotData>>() {}.getType();

    public ItemSlotLoader() {
        super(GSON, "config");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        ItemSlotManager.clear();

        /*
         * We are specifically looking for:
         *
         * data/lights_perks/config/items.json
         *
         * SimpleJsonResourceReloadListener will give us
         * the file as:
         *
         * lights_perks:items
         */
        ResourceLocation id =
                new ResourceLocation(
                        UpgradeScrolls.MODID,
                        "items"
                );

        JsonElement json =
                jsonMap.get(id);

        if (json == null) {

            UpgradeScrolls.LOGGER.warn(
                    "[ItemSlotLoader] items.json was not found."
            );

            return;
        }

        try {

            Map<String, ItemSlotData> items =
                    GSON.fromJson(
                            json,
                            ITEM_MAP_TYPE
                    );

            for (Map.Entry<String, ItemSlotData> entry
                    : items.entrySet()) {

                ResourceLocation itemId =
                        new ResourceLocation(entry.getKey());

                ItemSlotManager.register(
                        itemId,
                        entry.getValue()
                );

                UpgradeScrolls.LOGGER.debug(
                        "[ItemSlotLoader] Loaded {} with {} socket(s)",
                        itemId,
                        entry.getValue().slots == null
                                ? 0
                                : entry.getValue().slots.size()
                );
            }

        } catch (Exception e) {

            UpgradeScrolls.LOGGER.error(
                    "[ItemSlotLoader] Failed to load items.json",
                    e
            );
        }

        UpgradeScrolls.LOGGER.info(
                "[ItemSlotLoader] Loaded {} item configurations.",
                ItemSlotManager.getAll().size()
        );
    }
}