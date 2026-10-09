package dot.lighteater.lights_perks.item.custom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RuntimeModelGenerator {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private RuntimeModelGenerator() {
    }

    public static void generateItemModel(
            Path resourcePackRoot,
            String namespace,
            String itemId,
            String texture
    ) throws IOException {

        if (namespace == null || namespace.isBlank()) {
            throw new IllegalArgumentException(
                    "Model namespace cannot be empty."
            );
        }

        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException(
                    "Model item ID cannot be empty."
            );
        }

        if (texture == null || texture.isBlank()) {
            throw new IllegalArgumentException(
                    "Model itemTexture cannot be empty."
            );
        }

        String normalizedTexture = texture.trim();

        if (normalizedTexture.endsWith(".png")) {
            normalizedTexture = normalizedTexture.substring(
                    0,
                    normalizedTexture.length() - 4
            );
        }

        ResourceLocation textureId =
                ResourceLocation.parse(normalizedTexture);

        JsonObject model = new JsonObject();
        model.addProperty(
                "parent",
                "minecraft:item/generated"
        );

        JsonObject textures = new JsonObject();
        textures.addProperty(
                "layer0",
                textureId.toString()
        );

        model.add("textures", textures);

        Path modelPath = resourcePackRoot
                .resolve("assets")
                .resolve(namespace)
                .resolve("models")
                .resolve("item")
                .resolve(itemId + ".json");

        Files.createDirectories(modelPath.getParent());

        Files.writeString(
                modelPath,
                GSON.toJson(model),
                StandardCharsets.UTF_8
        );
    }
}