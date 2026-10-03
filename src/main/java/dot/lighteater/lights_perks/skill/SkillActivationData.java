package dot.lighteater.lights_perks.skill;

public class SkillActivationData {

    public String mode = "passive";

    public String condition;

    public String trigger;

    public int duration = 0;

    public int cooldown = 0;

    public double threshold = 0.3;

    public boolean retrigger = false;

    public String conditionPolicy = "while_true";

    public boolean showNotification = true;
    public String activationMessage;
    public String deactivationMessage;
}