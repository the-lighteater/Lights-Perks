package dot.lighteater.lights_perks.skill.skill_sets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkillSetLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    private static final String DIRECTORY = "skill_sets";

    private static final List<SkillSetEntry> ENTRIES =
            new ArrayList<>();

    public SkillSetLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> resources,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        ENTRIES.clear();

        for (Map.Entry<ResourceLocation, JsonElement> resource : resources.entrySet()) {

            try {
                JsonObject root = resource.getValue().getAsJsonObject();

                if (!root.has("entries")) {
                    continue;
                }

                JsonArray entries = root.getAsJsonArray("entries");

                for (JsonElement element : entries) {
                    JsonObject entry = element.getAsJsonObject();

                    ResourceLocation skill =
                            ResourceLocation.parse(
                                    entry.get("skill").getAsString()
                            );

                    int required =
                            entry.get("required").getAsInt();

                    int points =
                            entry.get("points").getAsInt();

                    List<ResourceLocation> items =
                            new ArrayList<>();

                    JsonArray itemArray =
                            entry.getAsJsonArray("items");

                    for (JsonElement itemElement : itemArray) {
                        items.add(
                                ResourceLocation.parse(
                                        itemElement.getAsString()
                                )
                        );
                    }

                    ENTRIES.add(
                            new SkillSetEntry(
                                    skill,
                                    required,
                                    points,
                                    items
                            )
                    );
                }

            } catch (Exception exception) {
                System.err.println(
                        "Failed to load bonus skills from "
                                + resource.getKey()
                );

                exception.printStackTrace();
            }
        }

        System.out.println(
                "Loaded " + ENTRIES.size() + " bonus skill entries."
        );
    }

    public static List<SkillSetEntry> getEntries() {
        return Collections.unmodifiableList(ENTRIES);
    }
}