package dot.lighteater.lights_perks.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.item.custom.PerkItem;
import dot.lighteater.lights_perks.item.custom.RuntimeModelGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LoadItems {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Path ITEM_FOLDER =
            FMLPaths.CONFIGDIR.get()
                    .resolve("lights_perks")
                    .resolve("items");

    private static final Path GENERATED_PACK_ROOT =
            FMLPaths.CONFIGDIR.get()
                    .resolve("lights_perks")
                    .resolve("generated_pack");

    private static final List<RegistryObject<Item>> ITEMS =
            new ArrayList<>();

    private LoadItems() {
    }

    public static List<RegistryObject<Item>> getItems() {
        return ITEMS;
    }

    public static void register(
            IEventBus eventBus,
            DeferredRegister<Item> items
    ) {

        LightsPerks.LOGGER.info(
                "========== BEGIN REGISTERING ITEMS =========="
        );

        createItemDirectory();

        scanItems(items);

        LightsPerks.LOGGER.info(
                "Registered {} item definitions.",
                ITEMS.size()
        );

        LightsPerks.LOGGER.info(
                "=========== END REGISTERING ITEMS ==========="
        );
    }

    private static void createItemDirectory() {

        try {

            Files.createDirectories(ITEM_FOLDER);

            LightsPerks.LOGGER.info(
                    "Verified item directory: {}",
                    ITEM_FOLDER.toAbsolutePath()
            );

        } catch (IOException e) {

            LightsPerks.LOGGER.error(
                    "Failed to create item directory: {}",
                    ITEM_FOLDER.toAbsolutePath(),
                    e
            );
        }
    }

    private static void scanItems(
            DeferredRegister<Item> items
    ) {

        LightsPerks.LOGGER.info(
                "Scanning item definitions from: {}",
                ITEM_FOLDER.toAbsolutePath()
        );

        try (Stream<Path> files = Files.list(ITEM_FOLDER)) {

            files
                    .filter(path ->
                            path.toString()
                                    .toLowerCase()
                                    .endsWith(".json")
                    )
                    .sorted()
                    .forEach(path ->
                            loadItem(
                                    path,
                                    items
                            )
                    );

        } catch (IOException e) {

            LightsPerks.LOGGER.error(
                    "Failed to scan item directory: {}",
                    ITEM_FOLDER.toAbsolutePath(),
                    e
            );
        }
    }

    private static void loadItem(
            Path path,
            DeferredRegister<Item> items
    ) {

        String fileName =
                path.getFileName().toString();

        String fileId =
                fileName.substring(
                        0,
                        fileName.length() - ".json".length()
                );

        try (Reader reader = Files.newBufferedReader(path)) {

            ItemDefinition definition =
                    GSON.fromJson(
                            reader,
                            ItemDefinition.class
                    );

            if (definition == null) {

                LightsPerks.LOGGER.warn(
                        "Skipping empty item definition: {}",
                        path
                );

                return;
            }

            String itemId =
                    definition.id;

            if (itemId == null || itemId.isBlank()) {

                itemId = fileId;
            }

            registerItem(
                    items,
                    itemId,
                    definition
            );

        } catch (JsonParseException e) {

            LightsPerks.LOGGER.error(
                    "Invalid item JSON: {}",
                    path,
                    e
            );

        } catch (IOException e) {

            LightsPerks.LOGGER.error(
                    "Failed to read item definition: {}",
                    path,
                    e
            );
        }
    }

    private static void registerItem(
            DeferredRegister<Item> items,
            String itemId,
            ItemDefinition definition
    ) {

        ResourceLocation itemIdLocation =
                new ResourceLocation(
                        LightsPerks.MODID,
                        itemId
                );

        LightsPerks.LOGGER.info(
                "Registering item: {} | type={}",
                itemIdLocation,
                definition.type
        );

        RegistryObject<Item> registryObject;

        switch (definition.type.toLowerCase()) {

            case "perk":

                if (definition.itemTexture != null
                        && !definition.itemTexture.isBlank()) {

                    try {

                        RuntimeModelGenerator.generateItemModel(
                                GENERATED_PACK_ROOT,
                                LightsPerks.MODID,
                                itemId,
                                definition.itemTexture
                        );

                        LightsPerks.LOGGER.info(
                                "Generated runtime model for {} using itemTexture {}",
                                itemIdLocation,
                                definition.itemTexture
                        );

                    } catch (Exception e) {

                        LightsPerks.LOGGER.error(
                                "Failed to generate runtime model for {}",
                                itemIdLocation,
                                e
                        );
                    }

                } else {

                    LightsPerks.LOGGER.warn(
                            "No itemTexture configured for perk item {}. "
                                    + "It will use the missing/default model "
                                    + "unless another model is provided.",
                            itemIdLocation
                    );
                }

                registryObject =
                        items.register(
                                itemId,
                                () -> new PerkItem(
                                        new Item.Properties()
                                                .stacksTo(
                                                        definition.maxStackSize
                                                ),
                                        itemIdLocation,
                                        definition.displayName
                                )
                        );

                break;

            default:

                registryObject =
                        items.register(
                                itemId,
                                () -> new Item(
                                        new Item.Properties()
                                                .stacksTo(
                                                        definition.maxStackSize
                                                )
                                )
                        );

                break;
        }

        ITEMS.add(registryObject);
    }
}