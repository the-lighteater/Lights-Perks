package dot.lighteater.upgrade_scrolls.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
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

        UpgradeScrolls.LOGGER.info("========== BEGIN LOADING JSON ITEMS ==========");

        DEFINITIONS.clear();
        UpgradeScrolls.LOGGER.info("Cleared existing item definitions.");

        Path folder = FMLPaths.CONFIGDIR.get()
                .resolve("upgrade_scrolls")
                .resolve("items");

        UpgradeScrolls.LOGGER.info("Looking for item JSONs in: {}", folder.toAbsolutePath());

        try {

            Files.createDirectories(folder);
            UpgradeScrolls.LOGGER.info("Verified directory exists.");

            int fileCount = 0;

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.json")) {

                for (Path file : stream) {

                    fileCount++;

                    UpgradeScrolls.LOGGER.info("----------------------------------------");
                    UpgradeScrolls.LOGGER.info("Reading file: {}", file.getFileName());

                    try (Reader reader = Files.newBufferedReader(file)) {

                        ItemDefinition definition =
                                GSON.fromJson(reader, ItemDefinition.class);

                        if (definition == null) {
                            UpgradeScrolls.LOGGER.warn("JSON parsed to null! Skipping.");
                            continue;
                        }

                        UpgradeScrolls.LOGGER.info("Parsed JSON successfully.");

                        UpgradeScrolls.LOGGER.info("ID: {}", definition.id);
                        UpgradeScrolls.LOGGER.info("Display Name: {}", definition.displayName);
                        UpgradeScrolls.LOGGER.info("Max Stack Size: {}", definition.maxStackSize);
                        UpgradeScrolls.LOGGER.info("Rarity: {}", definition.rarity);
                        UpgradeScrolls.LOGGER.info("Texture: {}", definition.texture);

                        if (definition.id == null || definition.id.isBlank()) {
                            UpgradeScrolls.LOGGER.warn("Item has no ID! Skipping.");
                            continue;
                        }

                        if (DEFINITIONS.containsKey(definition.id)) {
                            UpgradeScrolls.LOGGER.warn("Duplicate item id '{}' found. Overwriting previous definition.", definition.id);
                        }

                        DEFINITIONS.put(definition.id, definition);

                        generateItemModel(definition);

                        UpgradeScrolls.LOGGER.info("Successfully registered definition '{}'.", definition.id);

                    } catch (JsonParseException e) {

                        UpgradeScrolls.LOGGER.error("Failed to parse JSON file '{}'.", file.getFileName(), e);

                    } catch (Exception e) {

                        UpgradeScrolls.LOGGER.error("Unexpected error while reading '{}'.", file.getFileName(), e);
                    }
                }
            }

            UpgradeScrolls.LOGGER.info("----------------------------------------");
            UpgradeScrolls.LOGGER.info("Finished scanning folder.");
            UpgradeScrolls.LOGGER.info("JSON files found: {}", fileCount);
            UpgradeScrolls.LOGGER.info("Definitions loaded: {}", DEFINITIONS.size());

            UpgradeScrolls.LOGGER.info("Loaded item IDs:");
            for (String id : DEFINITIONS.keySet()) {
                UpgradeScrolls.LOGGER.info(" - {}", id);
            }

        } catch (IOException e) {

            UpgradeScrolls.LOGGER.error("Failed to create/read item directory '{}'.", folder.toAbsolutePath(), e);
        }

        UpgradeScrolls.LOGGER.info("=========== END LOADING JSON ITEMS ===========");
    }

    private static void generateItemModel(ItemDefinition definition) {

        Path modelFolder = FMLPaths.CONFIGDIR.get()
                .resolve("upgrade_scrolls")
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


            UpgradeScrolls.LOGGER.info(
                    "Generated item model: {}",
                    modelFile
            );


        } catch (IOException e) {

            UpgradeScrolls.LOGGER.error(
                    "Failed generating model for {}",
                    definition.id,
                    e
            );
        }
    }

}