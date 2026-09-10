package dot.lighteater.lights_perks.perk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dot.lighteater.lights_perks.LightsPerks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class PerkLoader extends SimpleJsonResourceReloadListener {

    public static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Map<ResourceLocation, PerkData> PERKS =
            new HashMap<>();

    public PerkLoader() {
        super(GSON, "perk_items");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        PERKS.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry
                : jsonMap.entrySet()) {

            try {

                PerkData perk =
                        GSON.fromJson(
                                entry.getValue(),
                                PerkData.class
                        );

                PERKS.put(
                        entry.getKey(),
                        perk
                );

                LightsPerks.LOGGER.info(
                        "[PerkLoader] Loaded perk '{}'",
                        entry.getKey()
                );

            } catch (Exception e) {

                LightsPerks.LOGGER.error(
                        "[PerkLoader] Failed to load perk: {}",
                        entry.getKey(),
                        e
                );
            }
        }

        LightsPerks.LOGGER.info(
                "[PerkLoader] Loaded {} perks.",
                PERKS.size()
        );
    }

    public static PerkData get(ResourceLocation id) {
        return PERKS.get(id);
    }

    public static boolean contains(ResourceLocation id) {
        return PERKS.containsKey(id);
    }

    public static Map<ResourceLocation, PerkData> getAll() {
        return PERKS;
    }

    public static void clear() {
        PERKS.clear();
    }
}