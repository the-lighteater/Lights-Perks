package dot.lighteater.lights_perks.menu.skill_builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dot.lighteater.lights_perks.LightsPerks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class SkillBuilderScreen extends Screen {

    private EditBox titleBox;
    private EditBox synopsisBox;
    private EditBox skillIdBox;
    private EditBox colorBox;
    private EditBox maxLevelBox;
    private EditBox bonusLevelBox;

    private JsonObject currentSkillJson;

    private String generatedJson = "";

    private int selectedLevelIndex = 0;

    private EditBox levelNumberBox;

    private Button bonusButton;

    private Button previousLevelButton;
    private Button nextLevelButton;
    private Button addLevelButton;
    private Button removeLevelButton;

    private Button enchantsPageButton;
    private Button effectsPageButton;
    private Button attributesPageButton;
    private Button conditionsPageButton;

    private enum EditorPage {
        ENCHANTS,
        EFFECTS,
        ATTRIBUTES,
        CONDITIONS
    }

    private EditorPage currentPage = EditorPage.ATTRIBUTES;

    private final List<AbstractWidget> dynamicWidgets = new ArrayList<>();

    private final Set<EditBox> errorFields = new HashSet<>();

    private static final Pattern SKILL_ID_PATTERN = Pattern.compile("lights_perks:[a-z0-9_]+");
    private static final Pattern COLOR_PATTERN = Pattern.compile("0x[0-9A-Fa-f]{8}");


    public SkillBuilderScreen() {
        super(Component.literal("Skill Builder"));

        currentSkillJson = createDefaultSkill();
    }

    private void setTooltip(AbstractWidget widget, String text) {
        widget.setTooltip(Tooltip.create(Component.literal(text)));
    }

    @Override
    protected void init() {
        super.init();

        if (currentSkillJson == null) currentSkillJson = createDefaultSkill();

        int centerX = this.width / 2;
        int leftX = centerX - 300;
        int rightX = centerX + 10;
        int boxWidth = 280;

        int y = 42;
        int diffY = 34;

        titleBox = createEditBox(
                        leftX,
                        y,
                        boxWidth,
                        "Title"
                );

        setTooltip(titleBox, "The name displayed for this skill.");
        titleBox.setMaxLength(20);

        y += diffY;

        synopsisBox =
                createEditBox(
                        leftX,
                        y,
                        boxWidth,
                        "Synopsis"
                );

        setTooltip(synopsisBox, "The synopsis for this skill.");
        synopsisBox.setMaxLength(100);

        y += diffY;

        skillIdBox =
                createEditBox(
                        leftX,
                        y,
                        boxWidth,
                        "Skill ID"
                );

        setTooltip(skillIdBox, "The skill id for this skill.");
        skillIdBox.setMaxLength(50);

        y += diffY;

        colorBox =
                createEditBox(
                        leftX,
                        y,
                        boxWidth,
                        "Color"
                );

        setTooltip(colorBox, "The color value for this skill.");
        colorBox.setMaxLength(10);

        y += diffY;

        maxLevelBox =
                createEditBox(
                        leftX,
                        y,
                        boxWidth,
                        "Max Level"
                );

        setTooltip(maxLevelBox, "The # of max levels for this skill.");
        maxLevelBox.setMaxLength(2);

        y += diffY;

        bonusLevelBox =
                createEditBox(
                        leftX,
                        y,
                        boxWidth,
                        "Bonus Levels"
                );

        setTooltip(bonusLevelBox, "The # of bonus levels for this skill.");
        bonusLevelBox.setMaxLength(2);

        previousLevelButton =
                Button.builder(Component.literal("< Level"),
                                button -> selectPreviousLevel())
                        .bounds(
                                rightX,
                                42,
                                85,
                                20
                        ).build();

        addRenderableWidget(previousLevelButton);

        nextLevelButton =
                Button.builder(Component.literal("Level >"),
                                button -> selectNextLevel())
                        .bounds(
                                rightX + 90,
                                42,
                                85,
                                20
                        ).build();

        addRenderableWidget(nextLevelButton);

        addLevelButton =
                Button.builder(Component.literal("+ Level"),
                                button -> addLevel())
                        .bounds(
                                rightX + 180,
                                42,
                                100,
                                20
                        ).build();

        addRenderableWidget(addLevelButton);

        removeLevelButton =
                Button.builder(Component.literal("- Level"),
                                button -> removeLevel())
                        .bounds(
                                rightX,
                                67,
                                135,
                                20
                        ).build();

        addRenderableWidget(removeLevelButton);

        levelNumberBox =
                createEditBox(
                        rightX,
                        100,
                        135,
                        "Level"
                );

        levelNumberBox.setMaxLength(2);

        bonusButton =
                Button.builder(Component.literal("Bonus: OFF"),
                                button -> toggleBonus())
                        .bounds(
                                rightX + 140,
                                100,
                                140,
                                20
                        ).build();

        setTooltip(bonusButton, "Determines if this is a bonus level. " +
                        "Bonus levels are hidden unless a Bonus " +
                        "Skill Set is worn by the player");

        addRenderableWidget(bonusButton);

        int pageY = 130;
        int pageWidth = 68;

        enchantsPageButton =
                createPageButton(
                        "Enchants",
                        rightX,
                        pageY,
                        pageWidth,
                        EditorPage.ENCHANTS
                );

        setTooltip(enchantsPageButton, "Switches to all enchant entries");

        effectsPageButton =
                createPageButton(
                        "Effects",
                        rightX + 71,
                        pageY,
                        pageWidth,
                        EditorPage.EFFECTS
                );

        setTooltip(effectsPageButton, "Switches to all effect entries");

        attributesPageButton =
                createPageButton(
                        "Attributes",
                        rightX + 142,
                        pageY,
                        pageWidth,
                        EditorPage.ATTRIBUTES
                );

        setTooltip(attributesPageButton, "Switches to all attribute entries");

        conditionsPageButton =
                createPageButton(
                        "Conditions",
                        rightX + 213,
                        pageY,
                        67,
                        EditorPage.CONDITIONS
                );

        setTooltip(conditionsPageButton, "Switches to all conditions");

        addRenderableWidget(enchantsPageButton);
        addRenderableWidget(effectsPageButton);
        addRenderableWidget(attributesPageButton);
        addRenderableWidget(conditionsPageButton);

        addRenderableWidget(
                Button.builder(Component.literal("JSON"),
                                button -> openJsonEditor())
                        .bounds(
                                centerX - 205,
                                this.height - 35,
                                100,
                                20
                        ).build());

        addRenderableWidget(
                Button.builder(Component.literal("Perk Item Builder"),
                                button -> openPerkEditor())
                        .bounds(
                                5,
                                this.height - 55,
                                100,
                                20
                        ).build());

        addRenderableWidget(
                Button.builder(Component.literal("Generate JSON"),
                                button -> generateJson())
                        .bounds(
                                centerX - 100,
                                this.height - 35,
                                95,
                                20
                        ).build());

        addRenderableWidget(
                Button.builder(Component.literal("Save to Files"),
                                button -> saveToFilesJson())
                        .bounds(
                                centerX + 5,
                                this.height - 35,
                                95,
                                20
                        ).build());

        addRenderableWidget(
                Button.builder(Component.literal("Back"),
                                button -> onClose())
                        .bounds(
                                centerX + 105,
                                this.height - 35,
                                100,
                                20
                        ).build());

        syncFieldsFromJson();
        rebuildCurrentPage();
    }

    private EditBox createEditBox(
            int x,
            int y,
            int width,
            String message
    ) {
        EditBox box = new EditBox(
                        this.font,
                        x,
                        y,
                        width,
                        20,
                        Component.literal(message)
                );

        addRenderableWidget(box);

        return box;
    }

    private Button createPageButton(
            String text,
            int x,
            int y,
            int width,
            EditorPage page
    ) {
        return Button.builder(Component.literal(text), button -> selectPage(page)).bounds(
                        x,
                        y,
                        width,
                        20
                ).build();
    }

    private void selectPage(
            EditorPage page
    ) {
        saveCurrentPage();
        currentPage = page;
        rebuildCurrentPage();
    }

    private JsonObject createDefaultSkill() {
        JsonObject skill = new JsonObject();

        skill.addProperty("title", "Attack Up");
        skill.addProperty("skillSynopsis", "Slightly increases your attack damage and speed");
        skill.addProperty("skill_id", "lights_perks:attack_up");
        skill.addProperty("color", "0xFF7A1030");
        skill.addProperty("maxLevel", 5);
        skill.addProperty("bonusLevel", 3);

        JsonArray levels = new JsonArray();

        levels.add(createDefaultLevel(1));
        skill.add("levels", levels);

        return skill;
    }

    private JsonObject createDefaultLevel(int levelNumber) {
        JsonObject level = new JsonObject();

        level.addProperty("level", levelNumber);
        level.addProperty("bonus", false);level.add("effects", new JsonArray());
        level.add("skillEffects", new JsonArray());

        return level;
    }

    private void syncFieldsFromJson() {
        if (currentSkillJson == null) return;
        if (currentSkillJson.has("title")) titleBox.setValue(
                currentSkillJson.get("title").getAsString());

        if (currentSkillJson.has("skillSynopsis"))
            synopsisBox.setValue(currentSkillJson.get("skillSynopsis").getAsString());

        if (currentSkillJson.has("skill_id"))
            skillIdBox.setValue(currentSkillJson.get("skill_id").getAsString());

        if (currentSkillJson.has("color"))
            colorBox.setValue(currentSkillJson.get("color").getAsString());

        if (currentSkillJson.has("maxLevel"))
            maxLevelBox.setValue(String.valueOf(currentSkillJson.get("maxLevel").getAsInt()));

        if (currentSkillJson.has("bonusLevel"))
            bonusLevelBox.setValue(String.valueOf(currentSkillJson.get("bonusLevel").getAsInt()));
    }

    private void syncTopLevelFieldsToJson() {
        if (currentSkillJson == null) currentSkillJson = createDefaultSkill();

        currentSkillJson.addProperty("title", titleBox.getValue());
        currentSkillJson.addProperty("skillSynopsis", synopsisBox.getValue());
        currentSkillJson.addProperty("skill_id", skillIdBox.getValue());
        currentSkillJson.addProperty("color", colorBox.getValue());
        currentSkillJson.addProperty("maxLevel", parseIntOrDefault(maxLevelBox.getValue(), 1));
        currentSkillJson.addProperty("bonusLevel", parseIntOrDefault(bonusLevelBox.getValue(), 0));
    }

    private JsonArray getLevels() {
        if (!currentSkillJson.has("levels")
                || !currentSkillJson.get("levels").isJsonArray())
            currentSkillJson.add("levels", new JsonArray());

        return currentSkillJson.getAsJsonArray("levels");
    }

    private JsonObject getSelectedLevel() {

        JsonArray levels = getLevels();

        if (levels.isEmpty()) {
            levels.add(createDefaultLevel(1));
            selectedLevelIndex = 0;
        }

        if (selectedLevelIndex < 0) selectedLevelIndex = 0;

        if (selectedLevelIndex >= levels.size())
            selectedLevelIndex = levels.size() - 1;

        return levels.get(selectedLevelIndex).getAsJsonObject();
    }

    private void addLevel() {

        saveCurrentPage();

        JsonArray levels = getLevels();

        int newLevelNumber = levels.size() + 1;

        JsonObject level = createDefaultLevel(newLevelNumber);

        int maxLevel = parseIntOrDefault(maxLevelBox.getValue(), 1);

        if (newLevelNumber > maxLevel) level.addProperty("bonus", true);

        levels.add(level);

        selectedLevelIndex = levels.size() - 1;

        rebuildCurrentPage();
    }

    private void removeLevel() {

        saveCurrentPage();

        JsonArray levels = getLevels();

        if (levels.size() <= 1) return;

        levels.remove(selectedLevelIndex);

        if (selectedLevelIndex >= levels.size()) selectedLevelIndex = levels.size() - 1;

        rebuildCurrentPage();
    }

    private void selectPreviousLevel() {
        saveCurrentPage();

        if (selectedLevelIndex > 0) selectedLevelIndex--;

        rebuildCurrentPage();
    }


    private void selectNextLevel() {

        saveCurrentPage();

        JsonArray levels = getLevels();

        if (selectedLevelIndex < levels.size() - 1) selectedLevelIndex++;

        rebuildCurrentPage();
    }

    private void toggleBonus() {

        saveCurrentPage();

        JsonObject level = getSelectedLevel();

        boolean bonus = getBoolean(level, "bonus", false);

        level.addProperty("bonus", !bonus);

        updateBonusButton(!bonus);
    }

    private void updateBonusButton(boolean bonus) {
        bonusButton.setMessage(Component.literal(bonus ? "Bonus: ON" : "Bonus: OFF"));
    }

    private void rebuildCurrentPage() {
        for (AbstractWidget widget : dynamicWidgets) {
            removeWidget(widget);
        }

        dynamicWidgets.clear();

        JsonObject level = getSelectedLevel();

        levelNumberBox.setValue(String.valueOf(getInt(level, "level", selectedLevelIndex + 1)));

        updateBonusButton(getBoolean(level, "bonus", false));

        switch (currentPage) {
            case ENCHANTS:
                buildEnchantsPage(level);
                break;

            case EFFECTS:
                buildEffectsPage(level);
                break;

            case ATTRIBUTES:
                buildAttributesPage(level);
                break;

            case CONDITIONS:
                buildConditionsPage(level);
                break;
        }

        updateLevelButtons();

        updatePageButtons();
    }

    private void updateLevelButtons() {

        JsonArray levels = getLevels();

        previousLevelButton.active = selectedLevelIndex > 0;

        nextLevelButton.active = selectedLevelIndex < levels.size() - 1;

        removeLevelButton.active = levels.size() > 1;
    }

    private void updatePageButtons() {

        enchantsPageButton.active = currentPage != EditorPage.ENCHANTS;

        effectsPageButton.active = currentPage != EditorPage.EFFECTS;

        attributesPageButton.active = currentPage != EditorPage.ATTRIBUTES;

        conditionsPageButton.active = currentPage != EditorPage.CONDITIONS;
    }

    private void buildEnchantsPage(JsonObject level) {
        JsonArray skillEffects = getArray(level, "skillEffects");

        int y = 175;

        for (int i = 0; i < skillEffects.size(); i++) {
            JsonObject effect = skillEffects.get(i).getAsJsonObject();

            if (!"enchantment".equals(getString(effect, "type", ""))) continue;

            y = createEnchantmentEditor(effect, i, y);
        }

        Button addButton = Button.builder(Component.literal("+ Add Enchantment"),
                                button -> addEnchantment()).bounds(
                                this.width / 2 + 10,
                                Math.min(
                                        y,
                                        this.height - 75
                                ),
                                280,
                                20
                                ).build();

        addDynamicWidget(addButton);
    }

    private int createEnchantmentEditor(
            JsonObject effect,
            int index,
            int y
    ) {

        EditBox enchantmentBox = createDynamicEditBox(
                        this.width / 2 + 10,
                        y,
                        180,
                        "Enchantment");

        enchantmentBox.setValue(getString(
                        effect,
                        "enchantment",
                        ""));

        setTooltip(enchantmentBox, "The ID for the enchantment.");

        EditBox applicationBox = createDynamicEditBox(
                        this.width / 2 + 10,
                        y + 25,
                        180,
                        "Application");

        applicationBox.setValue(getString(
                        effect,
                        "application",
                        ""));

        setTooltip(applicationBox, "The application method for the enchantment. Default/Only Used is 'per_item'");

        EditBox levelBox = createDynamicEditBox(
                        this.width / 2 + 195,
                        y,
                        95,
                        "Level");

        levelBox.setValue(String.valueOf(getInt(
                effect, "level", 1)));

        setTooltip(levelBox, "The level of the enchantment.");

        Button remove = Button.builder(Component.literal("Remove"),
                        button -> { saveCurrentPage();
                                    JsonArray effects =
                                            getArray(
                                                    getSelectedLevel(),
                                                    "skillEffects"
                                            );

                                    if (index >= 0
                                            && index <
                                            effects.size()) {

                                        effects.remove(
                                                index
                                        );
                                    }

                                    rebuildCurrentPage();})
                        .bounds(
                                this.width / 2 + 195,
                                y + 25,
                                95,
                                20).build();

        addDynamicWidget(remove);

        enchantmentEditors.add(new EnchantmentEditor(
                        effect,
                        enchantmentBox,
                        applicationBox,
                        levelBox
                )
        );

        return y + 60;
    }

    private void addEnchantment() {

        saveCurrentPage();

        JsonObject effect = new JsonObject();

        effect.addProperty("type", "enchantment");
        effect.addProperty("enchantment", "minecraft:sharpness");
        effect.addProperty("application", "per_item");
        effect.addProperty("level", 1);
        getArray(getSelectedLevel(), "skillEffects").add(effect);

        rebuildCurrentPage();
    }

    private void buildEffectsPage(JsonObject level) {
        int y = 175;

        graphicsEffectEditors.clear();

        JsonArray displayEffects = getArray(level, "effects");

        for (int i = 0; i < displayEffects.size(); i++) {
            final int effectIndex = i;

            EditBox box = createDynamicEditBox(
                            this.width / 2 + 10,
                            y,
                            240,
                            "Display Effect");

            box.setValue(displayEffects.get(effectIndex).getAsString());

            setTooltip(box, "Display text for this level.");

            Button remove = Button.builder(Component.literal("X"),
                                    button -> { saveCurrentPage();
                                        JsonArray effects =
                                                getArray(getSelectedLevel(), "effects");

                                        if (effectIndex < effects.size()) effects.remove(effectIndex);
                                        rebuildCurrentPage();
                                    }
                            ).bounds(
                                    this.width / 2 + 255,
                                    y,
                                    35,
                                    20
                            ).build();

            addDynamicWidget(remove);

            graphicsEffectEditors.add(
                    new DisplayEffectEditor(
                            displayEffects, effectIndex, box));

            y += 25;
        }

        Button addDisplay = Button.builder(
                Component.literal("+ Display Effect"),
                        button -> addDisplayEffect())
                        .bounds(
                                this.width / 2 + 10,
                                y,
                                280,
                                20
                        ).build();

        addDynamicWidget(addDisplay);

        y += 30;

        graphicsEffectSkillEditors.clear();

        JsonArray skillEffects = getArray(
                level, "skillEffects");

        for (int i = 0; i < skillEffects.size(); i++) {

            JsonObject effect = skillEffects.get(i).getAsJsonObject();

            if (!"effect".equals(getString(effect, "type", ""))) continue;

            y = createStatusEffectEditor(effect, i, y);
        }

        Button addEffect = Button.builder(
                Component.literal("+ Add Status Effect"),
                                button -> addStatusEffect()).bounds(
                                this.width / 2 + 10,
                                y,
                                280,
                                20
                ).build();

        addDynamicWidget(addEffect);
    }

    private int createStatusEffectEditor(
            JsonObject effect,
            int index,
            int y
    ) {

        EditBox effectBox =
                createDynamicEditBox(
                        this.width / 2 + 10,
                        y,
                        180,
                        "Effect"
                );

        effectBox.setValue(getString(effect, "effect", ""));

        setTooltip(effectBox, "The ID of the effect to use.");

        EditBox durationBox = createDynamicEditBox(
                        this.width / 2 + 195,
                        y,
                        95,
                        "Duration");

        durationBox.setValue(String.valueOf(getInt(effect, "duration", 200)));

        setTooltip(durationBox, "The length of the effect.");

        EditBox strengthBox = createDynamicEditBox(
                        this.width / 2 + 10,
                        y + 25,
                        180,
                        "Strength"
                );

        strengthBox.setValue(String.valueOf(getInt(effect, "strength", 0)));

        setTooltip(strengthBox, "The strength of the effect.");

        Button remove = Button.builder(Component.literal("Remove"),
                                button -> {saveCurrentPage();
                                    JsonArray effects =
                                            getArray(getSelectedLevel(), "skillEffects");

                                    if (index >= 0 && index < effects.size())
                                        effects.remove(index);
                                    rebuildCurrentPage();
                                }
                        ).bounds(
                                this.width / 2 + 195,
                                y + 25,
                                95,
                                20
                        ).build();

        addDynamicWidget(remove);

        statusEffectEditors.add(
                new StatusEffectEditor(
                        effect,
                        effectBox,
                        durationBox,
                        strengthBox
                )
        );

        return y + 60;
    }

    private void addDisplayEffect() {
        saveCurrentPage();

        getArray(getSelectedLevel(), "effects").add("New Effect");

        rebuildCurrentPage();
    }

    private void addStatusEffect() {

        saveCurrentPage();

        JsonObject effect = new JsonObject();

        effect.addProperty("type", "effect");
        effect.addProperty("effect", "minecraft:haste");
        effect.addProperty("duration", 200);
        effect.addProperty("strength", 0);
        getArray(getSelectedLevel(), "skillEffects").add(effect);

        rebuildCurrentPage();
    }

    private void buildAttributesPage(JsonObject level) {
        attributeEditors.clear();

        JsonArray skillEffects = getArray(level, "skillEffects");

        int y = 175;

        for (int i = 0; i < skillEffects.size(); i++) {

            JsonObject effect = skillEffects.get(i).getAsJsonObject();

            if (!"attribute".equals(getString(effect, "type", ""))) continue;

            y = createAttributeEditor(effect, i, y);
        }

        Button addButton = Button.builder(Component.literal("+ Add Attribute"),
                                button -> addAttribute())
                        .bounds(
                                this.width / 2 + 10,
                                Math.min(
                                        y,
                                        this.height - 75
                                ),
                                280,
                                20
                        ).build();

        addDynamicWidget(addButton);
    }

    private int createAttributeEditor(
            JsonObject effect,
            int index,
            int y
    ) {

        EditBox attributeBox = createDynamicEditBox(
                        this.width / 2 + 10,
                        y,
                        280,
                        "Attribute");

        attributeBox.setValue(getString(effect, "attribute", ""));

        setTooltip(attributeBox, "The ID of the attribute.");

        EditBox amountBox =
                createDynamicEditBox(
                        this.width / 2 + 10,
                        y + 25,
                        135,
                        "Amount"
                );

        amountBox.setValue(
                String.valueOf(
                        getDouble(
                                effect,
                                "amount",
                                0.0
                        )
                )
        );

        setTooltip(amountBox, "The amount of change for the attribute.");

        EditBox operationBox =
                createDynamicEditBox(
                        this.width / 2 + 150,
                        y + 25,
                        140,
                        "Operation"
                );

        operationBox.setValue(
                getString(
                        effect,
                        "operation",
                        "addition"
                )
        );

        setTooltip(operationBox, "The operation style for the attribute." +
                " Valid operations are: " +
                "\n    'addition' - Adds the amount to the attribute's value," +
                "\n    'multiply_base' - Multiplies by the base value of the attribute," +
                "\n    'multiply_total' - Multiplies by the current total of the attribute.");

        Button remove =
                Button.builder(
                                Component.literal(
                                        "Remove"
                                ),
                                button -> {

                                    saveCurrentPage();

                                    JsonArray effects =
                                            getArray(
                                                    getSelectedLevel(),
                                                    "skillEffects"
                                            );

                                    if (index >= 0 && index < effects.size()) effects.remove(index);


                                    rebuildCurrentPage();
                                }
                        )
                        .bounds(
                                this.width / 2 + 10,
                                y + 50,
                                100,
                                20
                        )
                        .build();

        addDynamicWidget(remove);

        attributeEditors.add(
                new AttributeEditor(
                        effect,
                        attributeBox,
                        amountBox,
                        operationBox
                )
        );

        return y + 80;
    }

    private void addAttribute() {

        saveCurrentPage();

        JsonObject effect =
                new JsonObject();

        effect.addProperty("type", "attribute");
        effect.addProperty("attribute", "minecraft:generic.attack_damage");
        effect.addProperty("amount", 0.0);
        effect.addProperty("operation", "addition");
        getArray(getSelectedLevel(), "skillEffects").add(effect);

        rebuildCurrentPage();
    }


    private EditBox modeBox;
    private EditBox conditionBox;
    private EditBox triggerBox;
    private EditBox durationBox;
    private EditBox cooldownBox;
    private EditBox thresholdBox;
    private EditBox conditionPolicyBox;
    private EditBox activationMessageBox;
    private EditBox deactivationMessageBox;

    private Button retriggerButton;
    private Button notificationButton;
    private Button activationButton;

    private boolean activationEnabled = false;

    private void buildConditionsPage(JsonObject level) {

        int x = this.width / 2 + 10;

        boolean hasActivation =
                level.has("activation")
                        && level
                        .get("activation")
                        .isJsonObject();

        activationEnabled =
                hasActivation;

        activationButton =
                Button.builder(
                                Component.literal(
                                        hasActivation
                                                ? "Activation: ON"
                                                : "Activation: OFF"
                                ),
                                button ->
                                        toggleActivation()
                        )
                        .bounds(
                                x,
                                175,
                                280,
                                20
                        )
                        .build();

        addDynamicWidget(
                activationButton
        );

        setTooltip(activationButton, "Toggles conditions on/off. Off is used for constant skills, so most will be off.");

        if (!hasActivation) {

            addDynamicWidget(
                    Button.builder(
                                    Component.literal(
                                            "Enable activation to edit conditions"
                                    ),
                                    button ->
                                            toggleActivation()
                            )
                            .bounds(
                                    x,
                                    205,
                                    280,
                                    20
                            )
                            .build()
            );

            return;
        }

        JsonObject activation =
                level
                        .getAsJsonObject(
                                "activation"
                        );

        int y = 210;

        modeBox =
                createDynamicEditBox(
                        x,
                        y,
                        135,
                        "Mode"
                );

        modeBox.setValue(
                getString(
                        activation,
                        "mode",
                        "passive"
                )
        );

        setTooltip(modeBox, "Activation style of the skill. Valid mode strings are: " +
                "\n    'passive' - This is the default and renders the skill active all the time" +
                "\n    'conditional' - This is a on/off mode" +
                "\n    'timed' - This is a duration mode");

        conditionBox =
                createDynamicEditBox(
                        x + 140,
                        y,
                        140,
                        "Condition"
                );

        conditionBox.setValue(
                getString(
                        activation,
                        "condition",
                        ""
                )
        );

        setTooltip(
                conditionBox,
                "Condition checked by the skill. Used with conditional and timed modes. " +
                        "Valid condition strings are:" +
                        "\n    'sprinting' - True while the player is sprinting" +
                        "\n    'sneaking' - True while the player is crouching" +
                        "\n    'blocking' - True while the player is blocking" +
                        "\n    'on_ground' - True while the player is standing on the ground" +
                        "\n    'underwater' - True while the player is underwater" +
                        "\n    'burning' - True while the player is on fire" +
                        "\n    'moving' - True while the player is moving horizontally" +
                        "\n    'low_health' - True when health is at or below the threshold" +
                        "\n    'high_health' - True when health is at or above the threshold" +
                        "\n    'full_health' - True when the player has maximum health" +
                        "\n    'full_stamina' - True when food level is 20" +
                        "\n    'empty_stamina' - True when food level is 0" +
                        "\n    'high_stamina' - True when food level is at or above the threshold" +
                        "\n    'low_stamina' - True when food level is at or below the threshold" +
                        "\n    'effect:<effect_id>' - True while the player has the specified effect"
        );

        y += 25;

        triggerBox =
                createDynamicEditBox(
                        x,
                        y,
                        135,
                        "Trigger"
                );

        triggerBox.setValue(
                getString(
                        activation,
                        "trigger",
                        ""
                )
        );

        setTooltip(
                triggerBox,
                "Event that activates the skill. Valid trigger strings are:" +
                        "\n    'on_cured_all' - Activates when all active effects are cured" +
                        "\n    'on_ended:<effect_name>' - Activates when the specified effect ends" +
                        "\n    'on_begin:<effect_name>' - Activates when the specified effect begins" +
                        "\n    'block_broken' - Activates when the player breaks a block" +
                        "\n    'damage_taken' - Activates when the player takes damage" +
                        "\n    'entity_killed' - Activates when the player kills an entity"
        );

        conditionPolicyBox =
                createDynamicEditBox(
                        x + 140,
                        y,
                        140,
                        "Condition Policy"
                );

        conditionPolicyBox.setValue(
                getString(
                        activation,
                        "conditionPolicy",
                        "while_true"
                )
        );

        setTooltip(conditionPolicyBox, "Determines skill application behavior. Still WIP");

        y += 25;

        durationBox =
                createDynamicEditBox(
                        x,
                        y,
                        135,
                        "Duration"
                );

        durationBox.setValue(
                String.valueOf(
                        getInt(
                                activation,
                                "duration",
                                0
                        )
                )
        );

        setTooltip(durationBox, "How long the skill is active.");

        cooldownBox =
                createDynamicEditBox(
                        x + 140,
                        y,
                        140,
                        "Cooldown"
                );

        cooldownBox.setValue(
                String.valueOf(
                        getInt(
                                activation,
                                "cooldown",
                                0
                        )
                )
        );

        setTooltip(cooldownBox, "How long the skill has to be inactive until it can be reactivated.");

        y += 25;

        thresholdBox =
                createDynamicEditBox(
                        x,
                        y,
                        135,
                        "Threshold"
                );

        thresholdBox.setValue(
                String.valueOf(
                        getDouble(
                                activation,
                                "threshold",
                                0.0
                        )
                )
        );

        setTooltip(thresholdBox, "Only used for high/low stamina/health conditions. 0-20 range for stamina, 0-1 for health.");

        retriggerButton =
                Button.builder(
                                Component.literal(
                                        getBoolean(
                                                activation,
                                                "retrigger",
                                                false
                                        )
                                                ? "Retrigger: ON"
                                                : "Retrigger: OFF"
                                ),
                                button ->
                                        toggleRetrigger()
                        )
                        .bounds(
                                x + 140,
                                y,
                                140,
                                20
                        )
                        .build();

        addDynamicWidget(
                retriggerButton
        );

        setTooltip(retriggerButton, "If the skill reactivates during activation." +
                " Off means it only runs for the initial cause, on means a secondary" +
                " activation during it will cause it to extend the period.");

        y += 25;

        notificationButton =
                Button.builder(
                                Component.literal(
                                        getBoolean(
                                                activation,
                                                "showNotification",
                                                true
                                        )
                                                ? "Notifications: ON"
                                                : "Notifications: OFF"
                                ),
                                button ->
                                        toggleNotifications()
                        )
                        .bounds(
                                x,
                                y,
                                280,
                                20
                        )
                        .build();

        addDynamicWidget(
                notificationButton
        );

        setTooltip(notificationButton, "Allows the skill to send a HUD notification when activated.");

        y += 30;

        activationMessageBox =
                createDynamicEditBox(
                        x,
                        y,
                        280,
                        "Activation Message"
                );

        activationMessageBox.setMaxLength(
                200
        );

        activationMessageBox.setValue(
                getString(
                        activation,
                        "activationMessage",
                        ""
                )
        );

        setTooltip(activationMessageBox, "Custom activation message. Default is 'Skill Name Activated'.");

        y += 30;

        deactivationMessageBox =
                createDynamicEditBox(
                        x,
                        y,
                        280,
                        "Deactivation Message"
                );

        deactivationMessageBox.setMaxLength(
                200
        );

        deactivationMessageBox.setValue(
                getString(
                        activation,
                        "deactivationMessage",
                        ""
                )
        );

        setTooltip(deactivationMessageBox, "Custom deactivation message. Default is 'Skill Name Deactivated'.");

    }

    private void toggleActivation() {

        saveCurrentPage();

        JsonObject level =
                getSelectedLevel();

        if (level.has("activation")
                && level.get(
                "activation"
        ).isJsonObject()) {

            level.remove(
                    "activation"
            );

        } else {

            JsonObject activation =
                    new JsonObject();

            activation.addProperty(
                    "mode",
                    "passive"
            );

            activation.addProperty(
                    "condition",
                    ""
            );

            activation.addProperty(
                    "trigger",
                    ""
            );

            activation.addProperty(
                    "duration",
                    0
            );

            activation.addProperty(
                    "cooldown",
                    0
            );

            activation.addProperty(
                    "threshold",
                    0.0
            );

            activation.addProperty(
                    "retrigger",
                    false
            );

            activation.addProperty(
                    "conditionPolicy",
                    "while_true"
            );

            activation.addProperty(
                    "showNotification",
                    true
            );

            activation.addProperty(
                    "activationMessage",
                    ""
            );

            activation.addProperty(
                    "deactivationMessage",
                    ""
            );

            level.add(
                    "activation",
                    activation
            );
        }

        rebuildCurrentPage();
    }

    private void toggleRetrigger() {

        saveCurrentPage();

        JsonObject activation =
                getActivation();

        boolean value =
                getBoolean(
                        activation,
                        "retrigger",
                        false
                );

        activation.addProperty(
                "retrigger",
                !value
        );

        rebuildCurrentPage();
    }

    private void toggleNotifications() {

        saveCurrentPage();

        JsonObject activation =
                getActivation();

        boolean value =
                getBoolean(
                        activation,
                        "showNotification",
                        true
                );

        activation.addProperty(
                "showNotification",
                !value
        );

        rebuildCurrentPage();
    }

    private JsonObject getActivation() {

        JsonObject level =
                getSelectedLevel();

        if (!level.has("activation")
                || !level.get(
                "activation"
        ).isJsonObject()) {

            JsonObject activation =
                    new JsonObject();

            activation.addProperty(
                    "mode",
                    "passive"
            );

            activation.addProperty(
                    "condition",
                    ""
            );

            activation.addProperty(
                    "trigger",
                    ""
            );

            activation.addProperty(
                    "duration",
                    0
            );

            activation.addProperty(
                    "cooldown",
                    0
            );

            activation.addProperty(
                    "threshold",
                    0.0
            );

            activation.addProperty(
                    "retrigger",
                    false
            );

            activation.addProperty(
                    "conditionPolicy",
                    "while_true"
            );

            activation.addProperty(
                    "showNotification",
                    true
            );

            activation.addProperty(
                    "activationMessage",
                    ""
            );

            activation.addProperty(
                    "deactivationMessage",
                    ""
            );

            level.add(
                    "activation",
                    activation
            );

            return activation;
        }

        return level.getAsJsonObject(
                "activation"
        );
    }


    private final List<AttributeEditor>
            attributeEditors =
            new ArrayList<>();

    private final List<StatusEffectEditor>
            statusEffectEditors =
            new ArrayList<>();

    private final List<EnchantmentEditor>
            enchantmentEditors =
            new ArrayList<>();

    private final List<DisplayEffectEditor>
            graphicsEffectEditors =
            new ArrayList<>();

    private final List<StatusEffectEditor>
            graphicsEffectSkillEditors =
            new ArrayList<>();

    private void saveCurrentPage() {

        if (currentSkillJson == null) {
            return;
        }

        syncTopLevelFieldsToJson();

        JsonObject level =
                getSelectedLevel();


        if (levelNumberBox != null) {

            level.addProperty(
                    "level",
                    parseIntOrDefault(
                            levelNumberBox.getValue(),
                            selectedLevelIndex + 1
                    )
            );
        }

        if (bonusButton != null) {

            if (!level.has("bonus")) {

                level.addProperty(
                        "bonus",
                        false
                );
            }
        }

        for (
                AttributeEditor editor :
                attributeEditors
        ) {

            editor.save();
        }

        for (
                StatusEffectEditor editor :
                statusEffectEditors
        ) {

            editor.save();
        }

        for (
                StatusEffectEditor editor :
                graphicsEffectSkillEditors
        ) {

            editor.save();
        }

        for (
                EnchantmentEditor editor :
                enchantmentEditors
        ) {

            editor.save();
        }

        for (
                DisplayEffectEditor editor :
                graphicsEffectEditors
        ) {

            editor.save();
        }

        if (activationEnabled
                && level.has("activation")
                && level.get("activation").isJsonObject()) {

            JsonObject activation =
                    level.getAsJsonObject("activation");

            if (modeBox != null) {

                String mode =
                        modeBox.getValue().trim();

                if (!mode.isEmpty()
                        && !"passive".equalsIgnoreCase(mode)) {

                    activation.addProperty(
                            "mode",
                            mode
                    );
                }
            }

            if (conditionBox != null) {

                String condition =
                        conditionBox.getValue().trim();

                if (!condition.isEmpty()) {

                    activation.addProperty(
                            "condition",
                            condition
                    );
                }
            }

            if (triggerBox != null) {

                String trigger =
                        triggerBox.getValue().trim();

                if (!trigger.isEmpty()) {

                    activation.addProperty(
                            "trigger",
                            trigger
                    );
                }
            }

            if (durationBox != null) {

                int duration =
                        parseIntOrDefault(
                                durationBox.getValue(),
                                0
                        );

                if (duration > 0) {

                    activation.addProperty(
                            "duration",
                            duration
                    );
                }
            }

            if (cooldownBox != null) {

                int cooldown =
                        parseIntOrDefault(
                                cooldownBox.getValue(),
                                0
                        );

                if (cooldown > 0) {

                    activation.addProperty(
                            "cooldown",
                            cooldown
                    );
                }
            }

            if (thresholdBox != null) {

                double threshold =
                        parseDoubleOrDefault(
                                thresholdBox.getValue(),
                                0.0
                        );

                if (threshold != 0.0) {
                    activation.addProperty(
                            "threshold",
                            threshold
                    );
                }
            }

            if (conditionPolicyBox != null) {
                String policy = conditionPolicyBox.getValue().trim();

                if (!policy.isEmpty() && !"while_true".equalsIgnoreCase(policy)) {
                    activation.addProperty(
                            "conditionPolicy",
                            policy
                    );
                }
            }

            if (activationMessageBox != null) {
                String message = activationMessageBox.getValue().trim();

                if (!message.isEmpty()) {
                    activation.addProperty(
                            "activationMessage",
                            message
                    );
                }
            }

            if (deactivationMessageBox != null) {
                String message = deactivationMessageBox.getValue().trim();

                if (!message.isEmpty()) {
                    activation.addProperty(
                            "deactivationMessage",
                            message
                    );
                }
            }
        }
    }

    private EditBox createDynamicEditBox(
            int x,
            int y,
            int width,
            String message
    ) {

        EditBox box =
                new EditBox(
                        this.font,
                        x,
                        y,
                        width,
                        20,
                        Component.literal(
                                message
                        )
                );

        addDynamicWidget(
                box
        );

        return box;
    }

    private void addDynamicWidget(
            AbstractWidget widget
    ) {

        dynamicWidgets.add(widget);

        addRenderableWidget(
                widget
        );
    }

    private void openJsonEditor() {

        saveCurrentPage();

        syncTopLevelFieldsToJson();

        if (this.minecraft != null) {

            this.minecraft.setScreen(
                    new SkillJsonScreen(
                            this
                    )
            );
        }
    }

    private void openPerkEditor() {
        saveCurrentPage();

        syncTopLevelFieldsToJson();

        if (this.minecraft != null) {
            this.minecraft.setScreen(
                    new PerkItemScreen(this)
            );
        }
    }


    public void applyJson(
            JsonObject skill
    ) {

        currentSkillJson =
                skill.deepCopy();

        selectedLevelIndex = 0;

        generatedJson = "";

        errorFields.clear();

        clearEditorReferences();

        if (titleBox != null) {

            syncFieldsFromJson();

            rebuildCurrentPage();
        }
    }

    public String buildJson() {

        saveCurrentPage();

        syncTopLevelFieldsToJson();

        Gson gson =
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create();

        return gson.toJson(
                currentSkillJson
        );
    }

    private void generateJson() {

        errorFields.clear();

        saveCurrentPage();

        syncTopLevelFieldsToJson();

        validateFields();

        if (!errorFields.isEmpty()) {

            generatedJson = "";

            return;
        }

        generatedJson =
                buildJson();

        if (this.minecraft != null) {

            this.minecraft
                    .keyboardHandler
                    .setClipboard(
                            generatedJson
                    );
        }
    }

    private void saveToFilesJson() {

        try {

            errorFields.clear();

            saveCurrentPage();

            syncTopLevelFieldsToJson();

            validateFields();

            if (!errorFields.isEmpty()) {

                generatedJson = "";

                return;
            }

            generatedJson =
                    buildJson();


            Path configDirectory =
                    FMLPaths.CONFIGDIR
                            .get()
                            .resolve("lights_perks");

            Path skillsDirectory =
                    configDirectory.resolve("skills");

            Files.createDirectories(skillsDirectory);

            String skillFileName = skillIdBox.getValue().replace("lights_perks:", "");

            Path skillFile = skillsDirectory.resolve(skillFileName + ".json");

            Files.writeString(skillFile, generatedJson);

        } catch (IOException e) {

            LightsPerks.LOGGER.error(
                    "[PerkItemScreen] Failed to save perk files.",
                    e
            );
        }
    }


    private void validateFields() {

        if (titleBox.getValue()
                .trim()
                .isEmpty()) {

            errorFields.add(
                    titleBox
            );
        }

        if (synopsisBox.getValue()
                .trim()
                .isEmpty()) {

            errorFields.add(
                    synopsisBox
            );
        }

        if (!SKILL_ID_PATTERN
                .matcher(
                        skillIdBox.getValue()
                )
                .matches()) {

            errorFields.add(
                    skillIdBox
            );
        }

        if (!isValidColor(
                colorBox.getValue()
        )) {

            errorFields.add(
                    colorBox
            );
        }

        if (!isValidInteger(
                maxLevelBox.getValue()
        )) {

            errorFields.add(
                    maxLevelBox
            );
        }

        if (!isValidInteger(
                bonusLevelBox.getValue()
        )) {

            errorFields.add(
                    bonusLevelBox
            );
        }
    }

    private boolean isValidInteger(
            String value
    ) {

        if (value == null
                || value.isEmpty()) {

            return false;
        }

        try {

            parseInt(value);

            return true;

        } catch (NumberFormatException ignored) {

            return false;
        }
    }

    private boolean isValidColor(
            String value
    ) {

        if (value == null) {
            return false;
        }

        if (!COLOR_PATTERN
                .matcher(value)
                .matches()) {

            return false;
        }

        try {

            Long.parseLong(
                    value.substring(2),
                    16
            );

            return true;

        } catch (NumberFormatException ignored) {

            return false;
        }
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        this.renderBackground(
                graphics
        );

        graphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                15,
                0xFFFFFFFF
        );

        drawLabel(
                graphics,
                "Title",
                titleBox
        );

        drawLabel(
                graphics,
                "Synopsis",
                synopsisBox
        );

        drawLabel(
                graphics,
                "Skill ID",
                skillIdBox
        );

        drawLabel(
                graphics,
                "Color",
                colorBox
        );

        drawLabel(
                graphics,
                "Max Level",
                maxLevelBox
        );

        drawLabel(
                graphics,
                "Bonus Levels",
                bonusLevelBox
        );

        drawColorPreview(
                graphics,
                colorBox
        );

        JsonArray levels =
                getLevels();

        graphics.drawString(
                this.font,
                "Level "
                        + (selectedLevelIndex + 1)
                        + " / "
                        + levels.size(),
                this.width / 2 + 190,
                72,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "Level Editor",
                this.width / 2 + 10,
                90,
                0xFFFFFFFF
        );

        String pageTitle;

        switch (currentPage) {

            case ENCHANTS:
                pageTitle = "Enchantments";
                break;

            case EFFECTS:
                pageTitle = "Effects";
                break;

            case ATTRIBUTES:
                pageTitle = "Attributes";
                break;

            case CONDITIONS:
                pageTitle = "Conditions";
                break;

            default:
                pageTitle = "";
                break;
        }

        graphics.drawCenteredString(
                this.font,
                pageTitle,
                this.width / 2 + 150,
                155,
                0xFFFFFFFF
        );

        if (!errorFields.isEmpty()) {

            String errorMessage =
                    errorFields.size() == 1
                            ? "1 Error detected!"
                            : errorFields.size()
                            + " Errors detected!";

            graphics.drawCenteredString(
                    this.font,
                    errorMessage,
                    this.width / 2,
                    this.height - 55,
                    0xFFFF5555
            );
        }

        if (!generatedJson.isEmpty()
                && errorFields.isEmpty()) {

            graphics.drawCenteredString(
                    this.font,
                    "JSON copied to clipboard!",
                    this.width / 2,
                    this.height - 55,
                    0xFF55FF55
            );
        }

        drawErrorMarker(
                graphics,
                titleBox
        );

        drawErrorMarker(
                graphics,
                synopsisBox
        );

        drawErrorMarker(
                graphics,
                skillIdBox
        );

        drawErrorMarker(
                graphics,
                colorBox
        );

        drawErrorMarker(
                graphics,
                maxLevelBox
        );

        drawErrorMarker(
                graphics,
                bonusLevelBox
        );

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    private void drawLabel(
            GuiGraphics graphics,
            String text,
            EditBox box
    ) {

        graphics.drawString(
                this.font,
                text,
                box.getX(),
                box.getY() - 10,
                0xFFFFFFFF
        );
    }

    private void drawErrorMarker(
            GuiGraphics graphics,
            EditBox box
    ) {

        if (!errorFields.contains(box)) {
            return;
        }

        graphics.drawString(
                this.font,
                "!!!",
                box.getX()
                        + box.getWidth()
                        + 6,
                box.getY() + 5,
                0xFFFF5555
        );
    }

    private void drawColorPreview(
            GuiGraphics graphics,
            EditBox box
    ) {

        int previewX =
                box.getX() - 30;

        int previewY =
                box.getY();

        int size = 20;

        int border = 1;

        int color =
                parseColor(
                        box.getValue()
                );

        graphics.fill(
                previewX + border,
                previewY + border,
                previewX + size - border,
                previewY + size - border,
                color
        );

        graphics.fill(
                previewX,
                previewY,
                previewX + size,
                previewY + border,
                0xFFFFFFFF
        );

        graphics.fill(
                previewX,
                previewY,
                previewX + border,
                previewY + size,
                0xFFFFFFFF
        );

        graphics.fill(
                previewX,
                previewY + size - border,
                previewX + size,
                previewY + size,
                0xFF000000
        );

        graphics.fill(
                previewX + size - border,
                previewY,
                previewX + size,
                previewY + size,
                0xFF000000
        );
    }

    private int parseColor(
            String value
    ) {

        if (!isValidColor(value)) {
            return 0xFF000000;
        }

        try {

            return (int) Long.parseLong(
                    value.substring(2),
                    16
            );

        } catch (NumberFormatException ignored) {

            return 0xFF000000;
        }
    }

    private JsonArray getArray(
            JsonObject object,
            String name
    ) {

        if (!object.has(name)
                || !object
                .get(name)
                .isJsonArray()) {

            JsonArray array =
                    new JsonArray();

            object.add(
                    name,
                    array
            );

            return array;
        }

        return object.getAsJsonArray(
                name
        );
    }

    private String getString(
            JsonObject object,
            String name,
            String fallback
    ) {

        if (!object.has(name)) {
            return fallback;
        }

        try {

            return object
                    .get(name)
                    .getAsString();

        } catch (Exception ignored) {

            return fallback;
        }
    }

    private int getInt(
            JsonObject object,
            String name,
            int fallback
    ) {

        if (!object.has(name)) {
            return fallback;
        }

        try {

            return object
                    .get(name)
                    .getAsInt();

        } catch (Exception ignored) {

            return fallback;
        }
    }

    private double getDouble(
            JsonObject object,
            String name,
            double fallback
    ) {

        if (!object.has(name)) {
            return fallback;
        }

        try {

            return object
                    .get(name)
                    .getAsDouble();

        } catch (Exception ignored) {

            return fallback;
        }
    }

    private boolean getBoolean(
            JsonObject object,
            String name,
            boolean fallback
    ) {

        if (!object.has(name)) {
            return fallback;
        }

        try {

            return object
                    .get(name)
                    .getAsBoolean();

        } catch (Exception ignored) {

            return fallback;
        }
    }

    private int parseIntOrDefault(
            String value,
            int fallback
    ) {

        try {

            return Integer.parseInt(
                    value
            );

        } catch (NumberFormatException ignored) {

            return fallback;
        }
    }

    private double parseDoubleOrDefault(
            String value,
            double fallback
    ) {

        try {

            return Double.parseDouble(
                    value
            );

        } catch (NumberFormatException ignored) {

            return fallback;
        }
    }

    private void clearEditorReferences() {

        attributeEditors.clear();
        statusEffectEditors.clear();
        enchantmentEditors.clear();
        graphicsEffectEditors.clear();
        graphicsEffectSkillEditors.clear();

        modeBox = null;
        conditionBox = null;
        triggerBox = null;
        durationBox = null;
        cooldownBox = null;
        thresholdBox = null;
        conditionPolicyBox = null;
        activationMessageBox = null;
        deactivationMessageBox = null;
        retriggerButton = null;
        notificationButton = null;
        activationButton = null;
    }

    private static class AttributeEditor {

        private final JsonObject json;
        private final EditBox attributeBox;
        private final EditBox amountBox;
        private final EditBox operationBox;

        private AttributeEditor(
                JsonObject json,
                EditBox attributeBox,
                EditBox amountBox,
                EditBox operationBox
        ) {

            this.json = json;
            this.attributeBox = attributeBox;
            this.amountBox = amountBox;
            this.operationBox = operationBox;
        }

        private void save() {

            json.addProperty(
                    "type",
                    "attribute"
            );

            json.addProperty(
                    "attribute",
                    attributeBox.getValue()
            );

            json.addProperty(
                    "amount",
                    parseDouble(
                            amountBox.getValue(),
                            0.0
                    )
            );

            json.addProperty(
                    "operation",
                    operationBox.getValue()
            );
        }

        private static double parseDouble(
                String value,
                double fallback
        ) {

            try {

                return Double.parseDouble(
                        value
                );

            } catch (NumberFormatException ignored) {

                return fallback;
            }
        }
    }

    private static class StatusEffectEditor {

        private final JsonObject json;
        private final EditBox effectBox;
        private final EditBox durationBox;
        private final EditBox strengthBox;

        private StatusEffectEditor(
                JsonObject json,
                EditBox effectBox,
                EditBox durationBox,
                EditBox strengthBox
        ) {

            this.json = json;
            this.effectBox = effectBox;
            this.durationBox = durationBox;
            this.strengthBox = strengthBox;
        }

        private void save() {

            json.addProperty(
                    "type",
                    "effect"
            );

            json.addProperty(
                    "effect",
                    effectBox.getValue()
            );

            json.addProperty(
                    "duration",
                    parseInt(
                            durationBox.getValue(),
                            0
                    )
            );

            json.addProperty(
                    "strength",
                    parseInt(
                            strengthBox.getValue(),
                            0
                    )
            );
        }

        private static int parseInt(
                String value,
                int fallback
        ) {

            try {

                return Integer.parseInt(
                        value
                );

            } catch (NumberFormatException ignored) {

                return fallback;
            }
        }
    }

    private static class EnchantmentEditor {

        private final JsonObject json;
        private final EditBox enchantmentBox;
        private final EditBox applicationBox;
        private final EditBox levelBox;

        private EnchantmentEditor(
                JsonObject json,
                EditBox enchantmentBox,
                EditBox applicationBox,
                EditBox levelBox
        ) {

            this.json = json;
            this.enchantmentBox = enchantmentBox;
            this.applicationBox = applicationBox;
            this.levelBox = levelBox;
        }

        private void save() {

            json.addProperty(
                    "type",
                    "enchantment"
            );

            json.addProperty(
                    "enchantment",
                    enchantmentBox.getValue()
            );

            json.addProperty(
                    "application",
                    applicationBox.getValue()
            );

            json.addProperty(
                    "level",
                    parseInt(
                            levelBox.getValue(),
                            1
                    )
            );
        }

        private static int parseInt(
                String value,
                int fallback
        ) {

            try {

                return Integer.parseInt(
                        value
                );

            } catch (NumberFormatException ignored) {

                return fallback;
            }
        }
    }

    private record DisplayEffectEditor(JsonArray array, int index, EditBox box) {

        private void save() {

                if (index >= 0
                        && index < array.size()) {

                    array.set(
                            index,
                            new com.google.gson.JsonPrimitive(
                                    box.getValue()
                            )
                    );
                }
            }
        }

    @Override
    public void onClose() {

        if (this.minecraft != null) {

            this.minecraft.setScreen(
                    new net.minecraft.client.gui.screens.TitleScreen()
            );
        }
    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }
}