package dot.lighteater.lights_perks.skill;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dot.lighteater.lights_perks.UpgradeScrolls;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SkillRuntimeLoader {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Map<String, SkillData> SKILLS =
            new LinkedHashMap<>();

    private static final Path SKILLS_DIRECTORY =
            FMLPaths.CONFIGDIR
                    .get()
                    .resolve("lights_perks")
                    .resolve("skills");

    private SkillRuntimeLoader() {
    }

    public static void loadSkills() {

        SKILLS.clear();

        try {

            Files.createDirectories(SKILLS_DIRECTORY);

        } catch (IOException e) {

            UpgradeScrolls.LOGGER.error(
                    "Failed to create skills directory: {}",
                    SKILLS_DIRECTORY,
                    e
            );

            return;
        }

        try (var files = Files.list(SKILLS_DIRECTORY)) {

            files
                    .filter(path ->
                            path.toString().endsWith(".json"))
                    .forEach(SkillRuntimeLoader::loadSkill);

        } catch (IOException e) {

            UpgradeScrolls.LOGGER.error(
                    "Failed to read skills directory: {}",
                    SKILLS_DIRECTORY,
                    e
            );
        }

        UpgradeScrolls.LOGGER.info(
                "Loaded {} skills.",
                SKILLS.size()
        );
    }

    private static void loadSkill(Path path) {

        String fileName =
                path.getFileName().toString();

        String skillId =
                fileName.substring(
                        0,
                        fileName.length() - 5
                );

        try (Reader reader = Files.newBufferedReader(path)) {

            SkillData skill =
                    GSON.fromJson(
                            reader,
                            SkillData.class
                    );

            if (skill == null) {

                UpgradeScrolls.LOGGER.error(
                        "Skill file {} produced a null SkillData.",
                        path
                );

                return;
            }

            SKILLS.put(skillId, skill);

            UpgradeScrolls.LOGGER.info(
                    "Loaded skill '{}' ({})",
                    skill.title,
                    skillId
            );

        } catch (JsonParseException e) {

            UpgradeScrolls.LOGGER.error(
                    "Invalid skill JSON: {}",
                    path,
                    e
            );

        } catch (IOException e) {

            UpgradeScrolls.LOGGER.error(
                    "Failed to read skill file: {}",
                    path,
                    e
            );
        }
    }

    public static List<SkillData> getSkills() {

        return Collections.unmodifiableList(
                new ArrayList<>(SKILLS.values())
        );
    }

    public static SkillData getSkill(String id) {

        return SKILLS.get(id);
    }

    public static boolean hasSkill(String id) {

        return SKILLS.containsKey(id);
    }

    public static void clear() {

        SKILLS.clear();
    }
}