package dot.lighteater.lights_perks.perk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dot.lighteater.lights_perks.LightsPerks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class PerkLoader extends SimpleJsonResourceReloadListener {

    public static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Map<ResourceLocation, PerkData> DATA_PERKS =
            new HashMap<>();

    private static final Map<ResourceLocation, PerkData> RUNTIME_PERKS =
            new HashMap<>();

    private static final Path RUNTIME_PERKS_DIRECTORY =
            FMLPaths.CONFIGDIR
                    .get()
                    .resolve("lights_perks")
                    .resolve("perks");

    public PerkLoader() {
        super(GSON, "perk_items");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {

        loadDataPerks(jsonMap);

        loadRuntimePerks();

        LightsPerks.LOGGER.info(
                "[PerkLoader] Loaded {} data perks and {} runtime perks.",
                DATA_PERKS.size(),
                RUNTIME_PERKS.size()
        );
    }

    private static void loadDataPerks(
            Map<ResourceLocation, JsonElement> jsonMap
    ) {

        DATA_PERKS.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry :
                jsonMap.entrySet()) {

            ResourceLocation id =
                    entry.getKey();

            try {

                PerkData perk =
                        GSON.fromJson(
                                entry.getValue(),
                                PerkData.class
                        );

                if (perk == null) {

                    LightsPerks.LOGGER.warn(
                            "[PerkLoader] Skipping null data perk '{}'",
                            id
                    );

                    continue;
                }

                DATA_PERKS.put(
                        id,
                        perk
                );

                LightsPerks.LOGGER.info(
                        "[PerkLoader] Loaded data perk '{}'",
                        id
                );

            } catch (Exception e) {

                LightsPerks.LOGGER.error(
                        "[PerkLoader] Failed to load data perk '{}'",
                        id,
                        e
                );
            }
        }
    }

    private static void loadRuntimePerks() {

        RUNTIME_PERKS.clear();

        createRuntimePerkDirectory();

        LightsPerks.LOGGER.info(
                "[PerkLoader] Scanning runtime perk directory: {}",
                RUNTIME_PERKS_DIRECTORY.toAbsolutePath()
        );

        try (Stream<Path> files =
                     Files.list(RUNTIME_PERKS_DIRECTORY)) {

            files
                    .filter(path ->
                            path.toString()
                                    .toLowerCase()
                                    .endsWith(".json")
                    )
                    .sorted()
                    .forEach(
                            PerkLoader::loadRuntimePerk
                    );

        } catch (IOException e) {

            LightsPerks.LOGGER.error(
                    "[PerkLoader] Failed to scan runtime perk directory: {}",
                    RUNTIME_PERKS_DIRECTORY.toAbsolutePath(),
                    e
            );
        }
    }

    private static void createRuntimePerkDirectory() {

        try {

            Files.createDirectories(
                    RUNTIME_PERKS_DIRECTORY
            );

        } catch (IOException e) {

            LightsPerks.LOGGER.error(
                    "[PerkLoader] Failed to create runtime perk directory: {}",
                    RUNTIME_PERKS_DIRECTORY.toAbsolutePath(),
                    e
            );
        }
    }

    private static void loadRuntimePerk(
            Path path
    ) {

        String fileName =
                path.getFileName()
                        .toString();

        String fileId =
                fileName.substring(
                        0,
                        fileName.length() - ".json".length()
                );

        ResourceLocation id;

        try {
            id =
                    new ResourceLocation(
                            LightsPerks.MODID,
                            fileId
                    );

        } catch (Exception e) {
            LightsPerks.LOGGER.error(
                    "[PerkLoader] Invalid runtime perk ID '{}'",
                    fileId,
                    e
            );

            return;
        }

        try (Reader reader =
                     Files.newBufferedReader(path)) {
            PerkData perk =
                    GSON.fromJson(
                            reader,
                            PerkData.class
                    );

            if (perk == null) {
                LightsPerks.LOGGER.warn(
                        "[PerkLoader] Skipping null runtime perk '{}'",
                        path
                );
                return;
            }

            RUNTIME_PERKS.put(
                    id,
                    perk
            );

            if (DATA_PERKS.containsKey(id)) {
                LightsPerks.LOGGER.info(
                        "[PerkLoader] Runtime perk '{}' overrides data perk.",
                        id
                );

            } else {
                LightsPerks.LOGGER.info(
                        "[PerkLoader] Loaded runtime perk '{}'",
                        id
                );
            }

        } catch (Exception e) {
            LightsPerks.LOGGER.error(
                    "[PerkLoader] Failed to load runtime perk '{}'",
                    path,
                    e
            );
        }
    }

    public static PerkData get(
            ResourceLocation id
    ) {

        PerkData runtime =
                RUNTIME_PERKS.get(id);

        if (runtime != null) {
            return runtime;
        }

        return DATA_PERKS.get(id);
    }

    public static void registerRuntime(
            ResourceLocation id,
            PerkData perk
    ) {

        if (id == null || perk == null) {
            return;
        }

        RUNTIME_PERKS.put(
                id,
                perk
        );

        if (DATA_PERKS.containsKey(id)) {

            LightsPerks.LOGGER.info(
                    "[PerkLoader] Runtime perk '{}' overrides data perk.",
                    id
            );

        } else {

            LightsPerks.LOGGER.info(
                    "[PerkLoader] Registered runtime perk '{}'.",
                    id
            );
        }
    }

    public static void unregisterRuntime(
            ResourceLocation id
    ) {

        if (RUNTIME_PERKS.remove(id) != null) {

            LightsPerks.LOGGER.info(
                    "[PerkLoader] Unregistered runtime perk '{}'.",
                    id
            );
        }
    }

    public static boolean contains(
            ResourceLocation id
    ) {

        return RUNTIME_PERKS.containsKey(id)
                || DATA_PERKS.containsKey(id);
    }

    public static boolean hasRuntime(
            ResourceLocation id
    ) {

        return RUNTIME_PERKS.containsKey(id);
    }

    public static boolean hasData(
            ResourceLocation id
    ) {

        return DATA_PERKS.containsKey(id);
    }

    public static Map<ResourceLocation, PerkData> getAll() {

        Map<ResourceLocation, PerkData> result =
                new HashMap<>(
                        DATA_PERKS
                );

        result.putAll(
                RUNTIME_PERKS
        );

        return Collections.unmodifiableMap(
                result
        );
    }

    public static Map<ResourceLocation, PerkData> getDataPerks() {

        return Collections.unmodifiableMap(
                DATA_PERKS
        );
    }

    public static Map<ResourceLocation, PerkData> getRuntimePerks() {

        return Collections.unmodifiableMap(
                RUNTIME_PERKS
        );
    }

    public static void clearRuntime() {

        RUNTIME_PERKS.clear();
    }

    public static void clear() {

        DATA_PERKS.clear();
        RUNTIME_PERKS.clear();
    }
}