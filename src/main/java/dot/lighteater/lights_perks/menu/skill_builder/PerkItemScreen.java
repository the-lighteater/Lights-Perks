package dot.lighteater.lights_perks.menu.skill_builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.List;

public class PerkItemScreen extends Screen {

    private final SkillBuilderScreen parent;

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    // =========================================================
    // Perk Item
    // =========================================================

    private EditBox perkItemID;
    private EditBox perkItemName;
    private EditBox itemType;
    private EditBox maxStackSize;
    private EditBox itemTexture;

    private JsonObject currentPerkItemJson;
    private String generatedItemJson;

    private String itemJsonStatus = "";
    private String perkJsonStatus = "";

    private long itemJsonStatusTime = 0;
    private long perkJsonStatusTime = 0;

    private EditBox perkDataID;
    private EditBox perkLevel;

    private final List<SkillEntry> skillEntries =
            new ArrayList<>();


    private static class SkillEntry {

        private EditBox skillID;
        private EditBox skillLevel;
        private Button removeButton;
    }

    private static final int SKILL_ROW_HEIGHT = 30;
    private static final int SKILL_ROW_SPACING = 4;


    protected PerkItemScreen(SkillBuilderScreen parent) {
        super(Component.literal("Perk Item Builder"));

        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        if (currentPerkItemJson == null) currentPerkItemJson = createDefaultItem();

        int centerX = this.width / 2;

        /*
         * Two columns.
         */
        int leftX = 20;
        int rightX = centerX + 10;

        int columnWidth = centerX - 35;


        int y = 45;

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Copy Item JSON"),
                                button -> generatePerkItemJSON()
                        )
                        .bounds(
                                leftX,
                                y,
                                columnWidth / 2 - 5,
                                20
                        )
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Save to Files"),
                                button -> saveToFiles()
                        )
                        .bounds(
                                leftX + (columnWidth / 2) + 5,
                                y,
                                columnWidth / 2 - 5,
                                20
                        )
                        .build()
        );

        y += 28;

        this.perkItemName =
                createEditBox(
                        leftX,
                        y,
                        columnWidth,
                        "Item Name"
                );

        setTooltip(perkItemName, "The perk item name in-game.");
        perkItemName.setMaxLength(50);

        y += 28;

        this.perkItemID =
                createEditBox(
                        leftX,
                        y,
                        columnWidth,
                        "Item ID"
                );

        setTooltip(perkItemID, "The perk item ID this item uses, needs to match JSON name of perk data.");
        perkItemID.setMaxLength(30);

        y += 28;

        this.itemType =
                createEditBox(
                        leftX,
                        y,
                        columnWidth,
                        "Item Type"
                );

        setTooltip(itemType, "The item type, only use 'perk' for now.");
        itemType.setMaxLength(20);

        y += 28;

        this.maxStackSize =
                createEditBox(
                        leftX,
                        y,
                        columnWidth,
                        "Max Stack Size"
                );

        setTooltip(maxStackSize, "The max stack size.");
        maxStackSize.setMaxLength(3);

        y += 28;

        this.itemTexture =
                createEditBox(
                        leftX,
                        y,
                        columnWidth,
                        "Texture ID"
                );

        setTooltip(itemTexture, "The itemTexture Resource Location. Perk Items use 'lights_perks:item/perks/[itemTexture name]");
        itemTexture.setMaxLength(100);

        y = 45;

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Copy Perk Data JSON"),
                                button -> generatePerkDataJSON()
                        )
                        .bounds(
                                rightX,
                                y,
                                columnWidth / 2 - 5,
                                20
                        )
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Save to Files"),
                                button -> saveToFiles()
                        )
                        .bounds(
                                rightX + (columnWidth / 2) + 5,
                                y,
                                columnWidth / 2 - 5,
                                20
                        )
                        .build()
        );

        y += 28;

        this.perkDataID = createEditBox(
                rightX,
                y,
                columnWidth,
                "Perk ID"
        );

        this.perkDataID.setValue("haste_2");

        y += 28;

        this.perkLevel = createEditBox(
                rightX,
                y,
                columnWidth,
                "Perk Level"
        );

        this.perkLevel.setValue("1");

        y += 32;

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("+ Add Skill"),
                                button -> addSkillEntry("", "1")
                        )
                        .bounds(
                                rightX,
                                y,
                                columnWidth,
                                20
                        )
                        .build()
        );

        if (skillEntries.isEmpty()) {

            addSkillEntry(
                    "lights_perks:haste",
                    "4"
            );

        } else {

            rebuildSkillEntriesForScreen();
        }

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Back"),
                                button -> this.minecraft.setScreen(parent)
                        )
                        .bounds(
                                centerX - 50,
                                this.height - 35,
                                100,
                                20
                        )
                        .build()
        );

        syncFieldsFromItemJson();
    }

    private int getSkillStartY() {
        return 161;
    }

    private int getNextSkillY() {
        return getSkillStartY()
                + (skillEntries.size() * SKILL_ROW_HEIGHT);
    }

    private void addSkillEntry(
            String defaultSkill,
            String defaultLevel
    ) {
        int centerX = this.width / 2;
        int rightX = centerX + 10;
        int columnWidth = centerX - 35;

        int y = getNextSkillY();

        createSkillEntry(
                rightX,
                y,
                columnWidth,
                defaultSkill,
                defaultLevel
        );
    }

    private void createSkillEntry(
            int x,
            int y,
            int width,
            String defaultSkill,
            String defaultLevel
    ) {
        int removeWidth = 20;
        int spacing = 4;

        int skillWidth =
                (width - removeWidth - spacing * 2) * 2 / 3;

        int levelWidth =
                width
                        - skillWidth
                        - removeWidth
                        - spacing * 2;

        SkillEntry entry = new SkillEntry();

        entry.skillID = createEditBox(
                x,
                y,
                skillWidth,
                "Skill ID"
        );

        entry.skillID.setValue(defaultSkill);
        entry.skillID.setMaxLength(100);

        entry.skillLevel = createEditBox(
                x + skillWidth + spacing,
                y,
                levelWidth,
                "Level"
        );

        entry.skillLevel.setValue(defaultLevel);
        entry.skillLevel.setMaxLength(3);

        entry.removeButton = Button.builder(
                Component.literal("X"),
                button -> removeSkillEntry(entry)
        ).bounds(
                x + width - removeWidth,
                y,
                removeWidth,
                20
        ).build();

        this.addRenderableWidget(entry.removeButton);

        skillEntries.add(entry);
    }

    private void removeSkillEntry(SkillEntry entry) {

        this.removeWidget(entry.skillID);
        this.removeWidget(entry.skillLevel);
        this.removeWidget(entry.removeButton);

        skillEntries.remove(entry);

        rebuildSkillEntries();
    }

    private void rebuildSkillEntries() {

        List<String[]> values = new ArrayList<>();

        for (SkillEntry entry : skillEntries) {

            values.add(new String[]{
                    entry.skillID.getValue(),
                    entry.skillLevel.getValue()
            });
        }

        for (SkillEntry entry : skillEntries) {

            this.removeWidget(entry.skillID);
            this.removeWidget(entry.skillLevel);
            this.removeWidget(entry.removeButton);
        }

        skillEntries.clear();

        for (String[] value : values) {

            addSkillEntry(
                    value[0],
                    value[1]
            );
        }
    }

    private void rebuildSkillEntriesForScreen() {

        List<String[]> values = new ArrayList<>();

        for (SkillEntry entry : skillEntries) {

            values.add(new String[]{
                    entry.skillID.getValue(),
                    entry.skillLevel.getValue()
            });
        }

        skillEntries.clear();

        for (String[] value : values) {

            addSkillEntry(
                    value[0],
                    value[1]
            );
        }
    }

    private EditBox createEditBox(
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
                        Component.literal(message)
                );

        box.setHint(
                Component.literal(message)
        );

        this.addRenderableWidget(box);

        return box;
    }

    private void generatePerkItemJSON() {

        try {

            String name = perkItemName.getValue().trim();
            String id = perkItemID.getValue().trim();
            String type = itemType.getValue().trim();
            String texture = itemTexture.getValue().trim();

            int stackSize =
                    parseInt(
                            maxStackSize.getValue(),
                            -1
                    );

            /*
             * Basic validation.
             */
            if (id.isEmpty()
                    || type.isEmpty()
                    || stackSize < 1) {

                itemJsonStatus = "- Failed";
                itemJsonStatusTime = System.currentTimeMillis();
                return;
            }

            syncTopLevelItemFieldsToJson();

            generatedItemJson = buildItemJson();

            if (this.minecraft == null) {

                itemJsonStatus = "- Failed";
                itemJsonStatusTime = System.currentTimeMillis();
                return;
            }

            this.minecraft.keyboardHandler.setClipboard(
                    generatedItemJson
            );

            itemJsonStatus = "- Successfully Saved";
            itemJsonStatusTime = System.currentTimeMillis();

            LightsPerks.LOGGER.info(
                    "[PerkItemScreen] Generated item JSON for '{}':\n{}",
                    id,
                    generatedItemJson
            );

        } catch (Exception e) {

            itemJsonStatus = "- Failed";
            itemJsonStatusTime = System.currentTimeMillis();

            LightsPerks.LOGGER.error(
                    "[PerkItemScreen] Failed to generate item JSON.",
                    e
            );
        }
    }

    private void generatePerkDataJSON() {

        try {

            String id =
                    perkDataID.getValue().trim();

            int level =
                    parseInt(
                            perkLevel.getValue(),
                            -1
                    );

            if (id.isEmpty() || level < 1) {

                perkJsonStatus = "- Failed";
                perkJsonStatusTime = System.currentTimeMillis();
                return;
            }

            JsonObject skills =
                    new JsonObject();

            for (SkillEntry entry : skillEntries) {

                String skillId =
                        entry.skillID.getValue().trim();

                if (skillId.isEmpty()) {
                    continue;
                }

                int skillLevel =
                        parseInt(
                                entry.skillLevel.getValue(),
                                -1
                        );

                if (skillLevel < 1) {
                    continue;
                }

                skills.addProperty(
                        skillId,
                        skillLevel
                );
            }

            if (skills.size() <= 0) {

                perkJsonStatus = "- Failed";
                perkJsonStatusTime = System.currentTimeMillis();
                return;
            }

            JsonObject json =
                    new JsonObject();

            json.addProperty(
                    "level",
                    level
            );

            json.add(
                    "skills",
                    skills
            );

            String generatedJson =
                    GSON.toJson(json);

            if (this.minecraft == null) {

                perkJsonStatus = "- Failed";
                perkJsonStatusTime = System.currentTimeMillis();
                return;
            }

            this.minecraft.keyboardHandler.setClipboard(
                    generatedJson
            );

            perkJsonStatus = "- Successfully Saved";
            perkJsonStatusTime = System.currentTimeMillis();

            LightsPerks.LOGGER.info(
                    "[PerkItemScreen] Generated perk data JSON for '{}':\n{}",
                    id,
                    generatedJson
            );

        } catch (Exception e) {

            perkJsonStatus = "- Failed";
            perkJsonStatusTime = System.currentTimeMillis();

            LightsPerks.LOGGER.error(
                    "[PerkItemScreen] Failed to generate perk data JSON.",
                    e
            );
        }
    }

    private void saveToFiles() {

        try {


            String itemId =
                    perkItemID.getValue().trim();

            String itemTypeValue =
                    itemType.getValue().trim();

            int stackSize =
                    parseInt(
                            maxStackSize.getValue(),
                            -1
                    );

            if (itemId.isEmpty()
                    || itemTypeValue.isEmpty()
                    || stackSize < 1) {

                itemJsonStatus = "- Failed";
                perkJsonStatus = "- Failed";

                LightsPerks.LOGGER.warn(
                        "[PerkItemScreen] Failed to save files: invalid item data."
                );

                return;
            }


            String perkId =
                    perkDataID.getValue().trim();

            int perkLevelValue =
                    parseInt(
                            perkLevel.getValue(),
                            -1
                    );

            if (perkId.isEmpty()
                    || perkLevelValue < 1) {

                itemJsonStatus = "- Failed";
                perkJsonStatus = "- Failed";

                LightsPerks.LOGGER.warn(
                        "[PerkItemScreen] Failed to save files: invalid perk data."
                );

                return;
            }


            syncTopLevelItemFieldsToJson();

            String itemJson =
                    buildItemJson();


            JsonObject skills =
                    new JsonObject();

            for (SkillEntry entry : skillEntries) {

                String skillId =
                        entry.skillID.getValue().trim();

                if (skillId.isEmpty()) {
                    continue;
                }

                int skillLevel =
                        parseInt(
                                entry.skillLevel.getValue(),
                                -1
                        );

                if (skillLevel < 1) {
                    continue;
                }

                skills.addProperty(
                        skillId,
                        skillLevel
                );
            }

            if (skills.size() <= 0) {

                itemJsonStatus = "- Failed";
                perkJsonStatus = "- Failed";

                LightsPerks.LOGGER.warn(
                        "[PerkItemScreen] Failed to save files: no skills were provided."
                );

                return;
            }

            JsonObject perkJson =
                    new JsonObject();

            perkJson.addProperty(
                    "level",
                    perkLevelValue
            );

            perkJson.add(
                    "skills",
                    skills
            );

            String generatedPerkJson =
                    GSON.toJson(perkJson);


            Path configDirectory =
                    FMLPaths.CONFIGDIR
                            .get()
                            .resolve("lights_perks");

            Path itemsDirectory =
                    configDirectory.resolve("items");

            Path perksDirectory =
                    configDirectory.resolve("perks");

            Files.createDirectories(itemsDirectory);
            Files.createDirectories(perksDirectory);


            Path itemFile =
                    itemsDirectory.resolve(
                            itemId + "_perk.json"
                    );

            Path perkFile =
                    perksDirectory.resolve(
                            perkId + ".json"
                    );


            Files.writeString(
                    itemFile,
                    itemJson
            );

            Files.writeString(
                    perkFile,
                    generatedPerkJson
            );


            itemJsonStatus = "- Successfully Saved";
            perkJsonStatus = "- Successfully Saved";

            LightsPerks.LOGGER.info(
                    "[PerkItemScreen] Saved perk item JSON: {}",
                    itemFile
            );

            LightsPerks.LOGGER.info(
                    "[PerkItemScreen] Saved perk data JSON: {}",
                    perkFile
            );

        } catch (IOException e) {

            itemJsonStatus = "- Failed";
            perkJsonStatus = "- Failed";

            LightsPerks.LOGGER.error(
                    "[PerkItemScreen] Failed to save perk files.",
                    e
            );
        }
    }

    private void syncFieldsFromItemJson() {
        if (currentPerkItemJson == null) return;
        if (currentPerkItemJson.has("displayName")) perkItemName.setValue(
                currentPerkItemJson.get("displayName").getAsString());
        if (currentPerkItemJson.has("id")) perkItemID.setValue(
                currentPerkItemJson.get("id").getAsString());
        if (currentPerkItemJson.has("type")) itemType.setValue(
                currentPerkItemJson.get("type").getAsString());
        if (currentPerkItemJson.has("maxStackSize")) maxStackSize.setValue(
                currentPerkItemJson.get("maxStackSize").getAsString());
        if (currentPerkItemJson.has("itemTexture")) itemTexture.setValue(
                currentPerkItemJson.get("itemTexture").getAsString());
    }

    private void syncTopLevelItemFieldsToJson() {
        if (currentPerkItemJson == null) currentPerkItemJson = createDefaultItem();

        currentPerkItemJson.addProperty("displayName", perkItemName.getValue());
        currentPerkItemJson.addProperty("id", perkItemID.getValue());
        currentPerkItemJson.addProperty("type", itemType.getValue());
        currentPerkItemJson.addProperty("maxStackSize", parseInt(maxStackSize.getValue(), 64));
        currentPerkItemJson.addProperty("itemTexture", itemTexture.getValue());
    }

    private String buildItemJson() {
        syncTopLevelItemFieldsToJson();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(currentPerkItemJson);
    }

    private JsonObject createDefaultItem() {
        JsonObject perkItem = new JsonObject();

        perkItem.addProperty("displayName", "Haste 2 Perk");
        perkItem.addProperty("id", "haste_2");
        perkItem.addProperty("type", "perk");
        perkItem.addProperty("maxStackSize", "64");
        perkItem.addProperty("itemTexture", "lights_perks:item/perks/example_perk");

        return perkItem;
    }

    private int parseInt(String value, int fallback) {

        try {
            return Integer.parseInt(
                    value.trim()
            );

        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String escapeJSON(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        this.renderBackground(graphics);

        int centerX = this.width / 2;

        int columnWidth = centerX - 35;

        graphics.drawCenteredString(
                this.font,
                Component.literal("Perk Item"),
                centerX / 2,
                20,
                0xFFFFFF
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal("Perk Data"),
                centerX + centerX / 2,
                20,
                0xFFFFFF
        );


        if (!itemJsonStatus.isEmpty()) {

            graphics.drawString(
                    this.font,
                    Component.literal(itemJsonStatus),
                    centerX / 2 - 20,
                    30,
                    0xFFFFFF
            );
        }

        if (!perkJsonStatus.isEmpty()) {

            graphics.drawString(
                    this.font,
                    Component.literal(perkJsonStatus),
                    centerX + centerX / 2 - 20,
                    30,
                    0xFFFFFF
            );
        }

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    private void setTooltip(AbstractWidget widget, String text) {
        widget.setTooltip(Tooltip.create(Component.literal(text)));
    }
}