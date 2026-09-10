package dot.lighteater.lights_perks.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dot.lighteater.lights_perks.LightsPerks;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class LoadItems {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, ItemDefinition> DEFINITIONS = new HashMap<>();

    public static Map<String, ItemDefinition> getDefinitions() {
        return DEFINITIONS;
    }

    public static void load() {

        LightsPerks.LOGGER.info("========== BEGIN LOADING JSON ITEMS ==========");

        DEFINITIONS.clear();
        LightsPerks.LOGGER.info("Cleared existing item definitions.");

        Path folder = FMLPaths.CONFIGDIR.get()
                .resolve("upgrade_scrolls")
                .resolve("items");

        LightsPerks.LOGGER.info("Looking for item JSONs in: {}", folder.toAbsolutePath());

        try {

            Files.createDirectories(folder);
            LightsPerks.LOGGER.info("Verified directory exists.");

            int fileCount = 0;

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.json")) {

                for (Path file : stream) {

                    fileCount++;

                    LightsPerks.LOGGER.info("----------------------------------------");
                    LightsPerks.LOGGER.info("Reading file: {}", file.getFileName());

                    try (Reader reader = Files.newBufferedReader(file)) {

                        ItemDefinition definition =
                                GSON.fromJson(reader, ItemDefinition.class);

                        if (definition == null) {
                            LightsPerks.LOGGER.warn("JSON parsed to null! Skipping.");
                            continue;
                        }

                        LightsPerks.LOGGER.info("Parsed JSON successfully.");

                        LightsPerks.LOGGER.info("ID: {}", definition.id);
                        LightsPerks.LOGGER.info("Display Name: {}", definition.displayName);
                        LightsPerks.LOGGER.info("Max Stack Size: {}", definition.maxStackSize);
                        LightsPerks.LOGGER.info("Rarity: {}", definition.rarity);
                        LightsPerks.LOGGER.info("Texture: {}", definition.texture);

                        if (definition.id == null || definition.id.isBlank()) {
                            LightsPerks.LOGGER.warn("Item has no ID! Skipping.");
                            continue;
                        }

                        if (DEFINITIONS.containsKey(definition.id)) {
                            LightsPerks.LOGGER.warn("Duplicate item id '{}' found. Overwriting previous definition.", definition.id);
                        }

                        DEFINITIONS.put(definition.id, definition);

                        generateItemModel(definition);

                        LightsPerks.LOGGER.info("Successfully registered definition '{}'.", definition.id);

                    } catch (JsonParseException e) {

                        LightsPerks.LOGGER.error("Failed to parse JSON file '{}'.", file.getFileName(), e);

                    } catch (Exception e) {

                        LightsPerks.LOGGER.error("Unexpected error while reading '{}'.", file.getFileName(), e);
                    }
                }
            }

            LightsPerks.LOGGER.info("----------------------------------------");
            LightsPerks.LOGGER.info("Finished scanning folder.");
            LightsPerks.LOGGER.info("JSON files found: {}", fileCount);
            LightsPerks.LOGGER.info("Definitions loaded: {}", DEFINITIONS.size());

            LightsPerks.LOGGER.info("Loaded item IDs:");
            for (String id : DEFINITIONS.keySet()) {
                LightsPerks.LOGGER.info(" - {}", id);
            }

        } catch (IOException e) {

            LightsPerks.LOGGER.error("Failed to create/read item directory '{}'.", folder.toAbsolutePath(), e);
        }

        LightsPerks.LOGGER.info("=========== END LOADING JSON ITEMS ===========");
    }

    private static void generateItemModel(ItemDefinition definition) {

        Path modelFolder = FMLPaths.CONFIGDIR.get()
                .resolve("lights_perks")
                .resolve("generated_assets")
                .resolve("assets")
                .resolve("upgrade_scrolls")
                .resolve("models")
                .resolve("item");

        try {

            Files.createDirectories(modelFolder);

            Path modelFile = modelFolder.resolve(definition.id + ".json");


            String json =
                    """
                    {
                      "parent": "minecraft:item/generated",
                      "textures": {
                        "layer0": "%s"
                      }
                    }
                    """.formatted(definition.texture);


            Files.writeString(
                    modelFile,
                    json,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );


            LightsPerks.LOGGER.info(
                    "Generated item model: {}",
                    modelFile
            );


        } catch (IOException e) {

            LightsPerks.LOGGER.error(
                    "Failed generating model for {}",
                    definition.id,
                    e
            );
        }
    }

}