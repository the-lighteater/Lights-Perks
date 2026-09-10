package dot.lighteater.lights_perks.item_config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import dot.lighteater.lights_perks.LightsPerks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemConfigLoader extends SimpleJsonResourceReloadListener {

    public static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Type ITEM_MAP_TYPE =
            new TypeToken<Map<String, ItemConfigData>>() {}.getType();

    public ItemConfigLoader() {
        super(GSON, "config");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        ItemConfigManager.clear();

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
                        LightsPerks.MODID,
                        "items"
                );

        JsonElement json =
                jsonMap.get(id);

        if (json == null) {

            LightsPerks.LOGGER.warn(
                    "[ItemSlotLoader] items.json was not found."
            );

            return;
        }

        try {

            LightsPerks.LOGGER.info(
                    "[ItemSlotLoader] Parsing JSON: {}",
                    json
            );

            Map<String, ItemConfigData> items =
                    GSON.fromJson(
                            json,
                            ITEM_MAP_TYPE
                    );

            for (Map.Entry<String, ItemConfigData> entry
                    : items.entrySet()) {

                ResourceLocation itemId =
                        new ResourceLocation(entry.getKey());

                ItemConfigManager.register(
                        itemId,
                        entry.getValue()
                );

                LightsPerks.LOGGER.debug(
                        "[ItemSlotLoader] Loaded {} with {} socket(s)",
                        itemId,
                        entry.getValue().slots == null
                                ? 0
                                : entry.getValue().slots.size()
                );

                LightsPerks.LOGGER.debug(
                        "[ItemSlotLoader] Loaded {} with {} builtin skills",
                        itemId,
                        entry.getValue().builtin_skills == null
                                ? 0
                                : entry.getValue().builtin_skills.size()
                );

                if (entry.getValue().builtin_skills != null) {

                    for (Map.Entry<String, Integer> entrySkill
                            : entry.getValue().builtin_skills.entrySet()) {

                        ResourceLocation skillId =
                                new ResourceLocation(entrySkill.getKey());

                        int level = entrySkill.getValue();

                        LightsPerks.LOGGER.debug(
                                "Builtin skill: {} Level {}",
                                skillId,
                                level
                        );
                    }
                }
            }

        } catch (Exception e) {

            LightsPerks.LOGGER.error(
                    "[ItemSlotLoader] Failed to load items.json",
                    e
            );
        }

        LightsPerks.LOGGER.info(
                "[ItemSlotLoader] Loaded {} item configurations.",
                ItemConfigManager.getAll().size()
        );
    }
}