package dot.lighteater.lights_perks.skill;

import java.util.List;

public class SkillData {
    public int color;
    public String title;
    public int maxLevel;
    public int currLevel;
    public int bonusLevel;
    public List<SkillLevelData> levels;

    public SkillData(String title, int color, int maxLevel, int currLevel, int bonusLevel, List<SkillLevelData> levels) {
        this.title = title;
        this.color = color;
        this.maxLevel = maxLevel;
        this.currLevel = currLevel;
        this.bonusLevel = bonusLevel;
        this.levels = levels;
    }
}
