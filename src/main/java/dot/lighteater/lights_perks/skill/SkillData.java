package dot.lighteater.lights_perks.skill;

import dot.lighteater.lights_perks.helpers.EquipmentType;

import java.util.List;
import java.util.Map;

public class SkillData {
    public int color;
    public String title;
    public int maxLevel;
    public int currLevel;
    public int bonusLevel;
    public List<SkillLevelData> levels;
    public Map<EquipmentType, Integer> points;

    public SkillData(String title, int color, int maxLevel, int currLevel, int bonusLevel, List<SkillLevelData> levels,
                     Map<EquipmentType, Integer> points) {
        this.title = title;
        this.color = color;
        this.maxLevel = maxLevel;
        this.currLevel = currLevel;
        this.bonusLevel = bonusLevel;
        this.levels = levels;
        this.points = points;
    }
}
