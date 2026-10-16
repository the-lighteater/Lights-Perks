package dot.lighteater.lights_perks.skill;

import java.util.ArrayList;
import java.util.List;

public class SkillLevelData {

    public int level;
    public boolean bonus;

    /*
     * Human-readable text shown in the GUI.
     */
    public List<String> effects;

    /*
     * Actual gameplay effects.
     */
    public List<SkillEffectData> skillEffects;

    public SkillActivationData activation;

    public SkillLevelData() {
    }

    public SkillLevelData(
            int level,
            boolean bonus,
            List<String> effects,
            List<SkillEffectData> skillEffects,
            SkillActivationData activation
    ) {
        this.level = level;
        this.bonus = bonus;
        this.effects = effects;
        this.skillEffects = skillEffects;
        this.activation = activation;
    }
}