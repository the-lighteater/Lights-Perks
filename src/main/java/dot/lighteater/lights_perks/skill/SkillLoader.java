package dot.lighteater.lights_perks.skill;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dot.lighteater.lights_perks.UpgradeScrolls;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.Map;

public class SkillLoader extends SimpleJsonResourceReloadListener {

    public static final Gson GSON = new Gson();

    public SkillLoader() {
        super(GSON, "skills");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        SkillManager.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {

            try {

                SkillData skill =
                        GSON.fromJson(
                                entry.getValue(),
                                SkillData.class
                        );

                SkillManager.register(
                        entry.getKey(),
                        skill
                );

            } catch (Exception e) {

                UpgradeScrolls.LOGGER.error(
                        "[SkillLoader] Failed to load skill: {}",
                        entry.getKey(),
                        e
                );
            }
        }

        UpgradeScrolls.LOGGER.info(
                "[SkillLoader] Loaded {} skills.",
                SkillManager.getAll().size()
        );
    }

    public static Collection<SkillData> getAll() {
        return SkillManager.getAll();
    }
}