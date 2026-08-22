package dot.lighteater.lights_perks.skill;

import dot.lighteater.lights_perks.helpers.EquipmentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillData {

    public String title;
    public String skill_id;
    public String color;

    public int maxLevel;
    public int bonusLevel;

    public List<SkillLevelData> levels;

    public Map<EquipmentType, Integer> points;

    public SkillData() {
    }

    public SkillData(
            String title,
            String skill_id,
            String color,
            int maxLevel,
            int bonusLevel,
            List<SkillLevelData> levels,
            Map<EquipmentType, Integer> points
    ) {
        this.title = title;
        this.skill_id = skill_id;
        this.color = color;
        this.maxLevel = maxLevel;
        this.bonusLevel = bonusLevel;
        this.levels = levels;
        this.points = points;
    }

    public Map<EquipmentType, Integer> getPoints() {
        if (points == null) {
            points = new HashMap<>();
        }

        return points;
    }
}