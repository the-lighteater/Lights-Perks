package dot.lighteater.lights_perks.skill;

import java.util.List;

public class SkillLevelData {

    public final int level;
    public final List<String> effects;
    public final boolean bonus;

    public SkillLevelData(
            int level,
            boolean bonus,
            String... effects
    ) {
        this.level = level;
        this.bonus = bonus;
        this.effects = List.of(effects);
    }
}