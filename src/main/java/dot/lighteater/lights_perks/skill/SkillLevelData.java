package dot.lighteater.lights_perks.skill;

import java.util.ArrayList;
import java.util.List;

public class SkillLevelData {

    public int level;
    public boolean bonus;
    public List<String> effects;

    public SkillLevelData() {
    }

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