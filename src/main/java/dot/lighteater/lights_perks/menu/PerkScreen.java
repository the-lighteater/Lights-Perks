package dot.lighteater.lights_perks.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import dot.lighteater.lights_perks.ClientConfig;
import dot.lighteater.lights_perks.ModKeyBindings;
import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.helpers.EquipmentType;
import dot.lighteater.lights_perks.skill.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerkScreen extends AbstractContainerScreen<PerkMenu> {
    private static final ResourceLocation perkMenuTexture =
            new ResourceLocation(
                    "lights_perks",
                    "textures/gui/container/perk_menu.png"
            );

    List<SkillData> skills;

    private boolean submenuOpen = false;

    private SkillData selectedSkill = null;

    private int skillPage = 0;
    private int skillLevelPage = 0;

    private static final int SKILLS_PER_PAGE = 5;
    private static final int LEVELS_PER_PAGE = 4;

    private Map<ResourceLocation, Integer> points =
            new HashMap<>();

    private Map<ResourceLocation, Map<EquipmentType, Integer>>
            equipmentPoints =
            new HashMap<>();

    LocalPlayer player = null;

    public PerkScreen(
            PerkMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);

        imageWidth = 176;
        imageHeight = 200;
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.render(graphics, mouseX, mouseY, partialTick);

        updatePlayerInfo();

        renderEquipmentItems(graphics);

        renderPerkSlotLevels(graphics);

        renderPerkSubmenu(graphics, mouseX, mouseY);

        if (selectedSkill != null) {
            renderSkillSubmenu(
                    graphics,
                    mouseX,
                    mouseY,
                    selectedSkill
            );
        }

        renderHelpButton(graphics);

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private void updatePlayerInfo() {
        skills = new ArrayList<>(SkillManager.getAllSkills());

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {

            player = minecraft.player;

            if (player.tickCount % ClientConfig.SYNC_PER_TICK_SCREEN.get() != 0) {
                return;
            }

            points = SkillManager.getPlayerSkillPointsScreen(player);

            equipmentPoints = SkillManager.getPlayerEquipmentPointsScreen(player);

            if (selectedSkill != null) {
                int skillPoints = points.getOrDefault(
                        new ResourceLocation(selectedSkill.skill_id),
                        0
                );

                if (skillPoints == 0) {
                    selectedSkill = null;
                }
            }
        }
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (ModKeyBindings.TOGGLE_PERK_SUBMENU.matches(
                keyCode,
                scanCode
        )) {

            if (ClientConfig.TOGGLE_SKILLS_SUBMENU.get()) {

                submenuOpen = !submenuOpen;

            } else {

                submenuOpen = true;
            }

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (ModKeyBindings.TOGGLE_PERK_SUBMENU.matches(
                keyCode,
                scanCode
        )) {

            if (!ClientConfig.TOGGLE_SKILLS_SUBMENU.get()) {
                submenuOpen = false;
            }

            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void renderPerkSubmenu(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        if (!submenuOpen) {
            return;
        }

        int width = 80;

        int x = leftPos - width - 4;

        /*
         * =========================
         * SUBMENU BACKGROUND
         * =========================
         */

        graphics.blit(
                perkMenuTexture,
        x,
        topPos,
        327,
        0,
        80,
        201,
        512,
        512
        );

        /*
         * =========================
         * TITLE
         * =========================
         */

        graphics.drawString(
                font,
                "Skills",
                x + 6,
                topPos + 6,
                0xFFFFFF
        );

        /*
         * =========================
         * SKILL BOXES
         * =========================
         */

        int boxX = x + 6;
        int boxY = topPos + 22;
        int boxWidth = width - 12;
        int boxHeight = 30;
        int spacing = 3;

        List<SkillData> visibleSkills =
                getVisibleSkills();

        int startIndex =
                skillPage * SKILLS_PER_PAGE;

        int endIndex =
                Math.min(
                        startIndex + SKILLS_PER_PAGE,
                        visibleSkills.size()
                );

        for (int i = startIndex; i < endIndex; i++) {

            int pageIndex =
                    i - startIndex;

            int currentY =
                    boxY + pageIndex * (boxHeight + spacing);

            renderPerkBox(
                    visibleSkills.get(i),
                    graphics,
                    boxX,
                    currentY,
                    boxWidth,
                    boxHeight,
                    i
            );
        }

        /*
         * =========================
         * PAGE BUTTONS
         * =========================
         */

        int pageCount =
                (int) Math.ceil(
                        (double) visibleSkills.size()
                                / SKILLS_PER_PAGE
                );

        if (pageCount > 1) {

            renderPageButtons(
                    graphics,
                    x,
                    topPos + 200 - 8,
                    width,
                    pageCount,
                    skillPage
            );
        }
    }

    private void renderPageButtons(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int pageCount,
            int currentPage
    ) {
        int buttonSize = 7;
        int spacing = 2;

        int totalWidth =
                pageCount * buttonSize
                        + (pageCount - 1) * spacing;

        int startX =
                x + (width - totalWidth) / 2;

        for (int i = 0; i < pageCount; i++) {

            int buttonX =
                    startX + i * (buttonSize + spacing);

            boolean selected =
                    i == currentPage;

            graphics.blit(
                    perkMenuTexture,
                    buttonX,
                    y,
                    selected ? 407 : 414,
                    36,
                    7,7,
                    512,512

            );
        }
    }

    private void renderPerkBox(
            SkillData skillData,
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int index
    ) {
        /*
         * =========================
         * OUTER BLACK BORDER
         * =========================
         */

        graphics.blit(perkMenuTexture,
                x,y,407,48,68,30,
                512,512);

        /*
         * =========================
         * COLORED BACKGROUND
         * =========================
         */

        int innerX = x + 3;
        int innerY = y + 11;
        int innerWidth = width - 6;
        int innerHeight = height - 14;

        graphics.fill(
                innerX,
                innerY,
                innerX + innerWidth,
                innerY + innerHeight,
                (int) Long.parseLong(
                skillData.color.replace("0x", ""),
                16
                )
        );

        /*
         * =========================
         * TITLE BAR
         * =========================
         */

        int titleHeight = 11;

        int titleBarX = x + 2;
        int titleBarY = y + 2;
        int titleBarRight = x + width - 2;
        int titleBarBottom = y + 2 + titleHeight;

        graphics.fill(
                titleBarX,
                titleBarY,
                titleBarRight,
                titleBarBottom,
                0xFF080808
        );

        /*
         * =========================
         * TITLE
         * =========================
         */

        float titleScale = 0.75F;

        int originalTitleWidth = font.width(skillData.title);
        int scaledTitleWidth = (int)(originalTitleWidth * titleScale);

        int titleX = x + (width - scaledTitleWidth) / 2;
        int titleY = y + 5;

        drawScaledString(
                graphics,
                skillData.title,
                titleX,
                titleY,
                titleScale,
                0xFFFFFF
        );

        /*
         * =========================
         * LEVEL BAR
         * =========================
         */

        int levelBarHeight = 7;

        int levelBarX = x + 3;
        int levelBarY = y + height - levelBarHeight - 3;

        int levelBarWidth = width - 6;


        /*
         * =========================
         * LEVEL COUNTS
         * =========================
         */

        int normalLevels = Math.max(
                skillData.maxLevel,
                0
        );

        ResourceLocation skillId =
                new ResourceLocation(skillData.skill_id);

        Minecraft minecraft =
                Minecraft.getInstance();

        boolean hasBonusSkill =
                minecraft.player != null
                        && SkillManager.hasBonusSkill(
                        minecraft.player,
                        skillId
                );

        int bonusLevels =
                hasBonusSkill
                        ? Math.max(skillData.bonusLevel, 0)
                        : 0;

        int totalLevels =
                Math.max(
                        normalLevels + bonusLevels,
                        1
                );

        int currentLevel =
                Math.max(
                        0,
                        Math.min(
                                points.getOrDefault(
                                        skillId,
                                        0
                                ),
                                normalLevels + bonusLevels
                        )
                );


        /*
         * =========================
         * SECTION MATH
         * =========================
         *
         * All levels, including bonus levels,
         * share the same bar.
         */

        float sectionWidth =
                (float) levelBarWidth / totalLevels;

        /*
         * =========================
         * DRAW SECTIONS
         * =========================
         */

        for (int level = 0; level < totalLevels; level++) {

            float sectionX =
                    levelBarX + level * sectionWidth;

            float sectionEnd;

            /*
             * Force the final section to end
             * exactly at the end of the bar.
             */
            if (level == totalLevels - 1) {

                sectionEnd =
                        levelBarX + levelBarWidth;

            } else {

                sectionEnd =
                        sectionX + sectionWidth;
            }


            int sectionStartInt =
                    (int) sectionX;

            int sectionEndInt =
                    (int) sectionEnd;


            /*
             * =========================
             * DETERMINE SECTION TYPE
             * =========================
             */

            boolean isBonus =
                    hasBonusSkill
                            && level >= normalLevels;

            boolean isCompleted =
                    !isBonus
                            && level < currentLevel;

            /*
             * =========================
             * BLACK SECTION BORDER
             * =========================
             */

            graphics.fill(
                    sectionStartInt,
                    levelBarY,
                    sectionEndInt,
                    levelBarY + levelBarHeight,
                    0xFF000000
            );


            /*
             * =========================
             * SECTION COLOR
             * =========================
             */

            int innerColor;

            if (isBonus) {

                /*
                 * Bonus levels are ALWAYS light blue.
                 */
                innerColor = 0xFF81EBE3;

            } else if (isCompleted) {

                /*
                 * Purchased normal level.
                 */
                innerColor = 0xFF55FF55;

            } else {

                /*
                 * Normal level that has not
                 * been purchased yet.
                 */
                innerColor = (int) Long.parseLong(
                        skillData.color.replace("0x", ""),
                        16
                );
            }


            /*
             * =========================
             * INNER SECTION
             * =========================
             */

            int innerStart =
                    sectionStartInt + 1;

            int innerEnd =
                    sectionEndInt - 1;

            /*
             * Prevent invalid fill dimensions
             * if the section becomes extremely small.
             */
            if (innerEnd > innerStart) {

                graphics.fill(
                        innerStart,
                        levelBarY + 1,
                        innerEnd,
                        levelBarY + levelBarHeight - 1,
                        innerColor
                );
            }
        }
    }

    private void renderSkillSubmenu(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            SkillData skillData
    ) {
        if (!submenuOpen) {
            return;
        }

        int width = 150;

        /*
         * Place it immediately to the right
         * of the main menu.
         */
        int x = leftPos + imageWidth + 4;

        graphics.blit(perkMenuTexture,
                x,topPos, 177, 0,
                150, 201,
                512,512);

        /*
         * =========================
         * TITLE
         * =========================
         */

        graphics.drawString(
                font,
                skillData.title,
                x + 6,
                topPos + 6,
                0xFFFFFF
        );

        /*
         * =========================
         * SYNOPSIS
         * =========================
         */

        int contentX = x + 6;
        int contentWidth = width - 12;

        int synopsisY = topPos + 18;

        List<String> synopsisLines =
                wrapText(
                        skillData.skillSynopsis,
                        contentWidth,
                        0.6F
                );

        for (String line : synopsisLines) {

            drawScaledString(
                    graphics,
                    line,
                    contentX,
                    synopsisY,
                    0.5F,
                    0xFFFFFFFF
            );

            synopsisY += 7;
        }

        /*
         * =========================
         * EQUIPMENT SOURCES
         * =========================
         */

        int equipmentY =
                synopsisY;

        renderSkillEquipmentSources(
                graphics,
                skillData,
                contentX,
                equipmentY,
                contentWidth
        );

        /*
         * =========================
         * LEVELS
         * =========================
         */

        int levelsY =
                equipmentY + 27 + 4;

        renderSkillLevels(
                graphics,
                skillData,
                contentX,
                levelsY,
                contentWidth
        );
    }

    private List<String> wrapText(
            String text,
            int maxWidth,
            float scale
    ) {
        List<String> lines = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return lines;
        }

        String[] words = text.split("\\s+");

        StringBuilder currentLine =
                new StringBuilder();

        for (String word : words) {

            String testLine;

            if (currentLine.length() == 0) {
                testLine = word;
            } else {
                testLine =
                        currentLine
                                + " "
                                + word;
            }

            int scaledWidth =
                    (int) (font.width(testLine) * scale);

            if (scaledWidth <= maxWidth) {

                currentLine = new StringBuilder(testLine);

            } else {

                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }

                /*
                 * If a single word is wider than the
                 * available space, allow it onto its
                 * own line rather than losing it.
                 */
                currentLine =
                        new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private void renderSkillEquipmentSources(
            GuiGraphics graphics,
            SkillData skillData,
            int x,
            int y,
            int width
    ) {
        int columns = 3;
        int rows = 2;

        int spacing = 3;

        int boxWidth =
                (width - spacing * (columns - 1)) / columns;

        int boxHeight = 12;

        /*
         * Equipment order in the UI.
         */
        EquipmentType[] equipmentTypes = {
                EquipmentType.HELMET,
                EquipmentType.CHESTPLATE,
                EquipmentType.LEGGINGS,
                EquipmentType.BOOTS,
                EquipmentType.MAIN_HAND,
                EquipmentType.OFF_HAND
        };

        String[] equipmentNames = {
                "Helmet",
                "Chest",
                "Legs",
                "Boots",
                "Main Hand",
                "Off Hand"
        };

        for (int i = 0; i < equipmentTypes.length; i++) {

            EquipmentType equipmentType = equipmentTypes[i];

            int column = i % columns;
            int row = i / columns;

            int boxX =
                    x + column * (boxWidth + spacing);

            int boxY =
                    y + row * (boxHeight + spacing);

            /*
             * =========================
             * EQUIPMENT LEVEL
             * =========================
             *
             * getOrDefault() means equipment
             * that isn't present in the map
             * automatically has 0 points.
             */
            ResourceLocation skillId =
                    new ResourceLocation(skillData.skill_id);

            Map<EquipmentType, Integer> skillEquipmentPoints =
                    equipmentPoints.getOrDefault(
                            skillId,
                            Map.of()
                    );

            int equipmentLevel =
                    skillEquipmentPoints.getOrDefault(
                            equipmentType,
                            0
                    );

            graphics.blit(perkMenuTexture,
            boxX,boxY, 421,36, 44,12,
            512,512);

            /*
             * =========================
             * EQUIPMENT NAME
             * =========================
             */

            drawScaledString(
                    graphics,
                    equipmentNames[i],
                    boxX + 3,
                    boxY + 3,
                    0.55F,
                    0xFFFFFFFF
            );

            /*
             * =========================
             * LEVEL
             * =========================
             */

            if (equipmentLevel > 0) {

                String levelText =
                        "+" + equipmentLevel;

                int textWidth =
                        (int)(font.width(levelText) * 0.7F);

                drawScaledString(
                        graphics,
                        levelText,
                        boxX + boxWidth - textWidth - 3,
                        boxY + 3,
                        0.7F,
                        0xFF55FF55
                );
            }
        }
    }

    private void renderSkillLevels(
            GuiGraphics graphics,
            SkillData skillData,
            int x,
            int y,
            int width
    ) {
        ResourceLocation skillId =
                new ResourceLocation(skillData.skill_id);

        Minecraft minecraft = Minecraft.getInstance();

        boolean hasBonus =
                minecraft.player != null
                        && SkillManager.hasBonusSkill(
                        minecraft.player,
                        skillId
                );

        /*
         * =========================
         * LEVEL CAP
         * =========================
         */

        int levelCap = skillData.maxLevel;

        if (hasBonus) {
            levelCap += skillData.bonusLevel;
        }

        int currentLevel = Math.max(
                0,
                Math.min(
                        points.getOrDefault(skillId, 0),
                        levelCap
                )
        );

        /*
         * =========================
         * VISIBLE LEVELS
         * =========================
         *
         * Remove bonus levels entirely if
         * the player does not currently have
         * the bonus skill.
         */

        List<SkillLevelData> visibleLevels =
                new ArrayList<>();

        for (SkillLevelData level : skillData.levels) {

            if (level.bonus && !hasBonus) {
                continue;
            }

            visibleLevels.add(level);
        }

        int pageCount =
                (int) Math.ceil(
                        (double) visibleLevels.size()
                                / LEVELS_PER_PAGE
                );

        skillLevelPage =
                Math.min(
                        skillLevelPage,
                        Math.max(0, pageCount - 1)
                );

        /*
         * =========================
         * LEVEL PAGE
         * =========================
         */

        int startIndex =
                skillLevelPage * LEVELS_PER_PAGE;

        int endIndex =
                Math.min(
                        startIndex + LEVELS_PER_PAGE,
                        visibleLevels.size()
                );

        int currentY = y;

        for (int i = startIndex; i < endIndex; i++) {

            SkillLevelData level =
                    visibleLevels.get(i);

            int diff =
                    renderSkillLevel(
                            graphics,
                            level,
                            currentLevel,
                            x,
                            currentY,
                            width
                    );

            currentY += (30 + diff);
        }

        /*
         * =========================
         * LEVEL PAGE BUTTONS
         * =========================
         */

        if (pageCount > 1) {

            renderPageButtons(
                    graphics,
                    x,
                    topPos + 200 - 8,
                    width,
                    pageCount,
                    skillLevelPage
            );
        }
    }

    private void renderHelpButton(GuiGraphics graphics) {

        int buttonSize = 12;

        int x = leftPos + imageWidth - buttonSize - 5;
        int y = topPos + 4;

        /*
         * =========================
         * OUTER BORDER
         * =========================
         */

        graphics.fill(
                x,
                y,
                x + buttonSize,
                y + buttonSize,
                0xFF080808
        );

        /*
         * =========================
         * INTERIOR
         * =========================
         */

        graphics.fill(
                x + 1,
                y + 1,
                x + buttonSize - 1,
                y + buttonSize - 1,
                0xFF707070
        );

        /*
         * =========================
         * TEXT
         * =========================
         */

        String text = "?";

        int textWidth = font.width(text);

        graphics.drawString(
                font,
                text,
                x + (buttonSize - textWidth) / 2,
                y + 2,
                0xFFFFFFFF
        );
    }

    private int renderSkillLevel(
            GuiGraphics graphics,
            SkillLevelData level,
            int currentLevel,
            int x,
            int y,
            int width
    ) {
        int color;

        /*
         * Bonus levels = blue
         */
        if (level.bonus) {

            if (level.level < currentLevel) {
                // Passed Bonus Level
                color = 0xFF709995;
            } else if (level.level == currentLevel) {
                // Current Bonus Level
                color = 0xFF17FFEE;
            } else {
                // Upcoming Bonus Level
                color = 0xFF0f9489;
            }

            /*
             * Current level = bright green
             */
        } else if (level.level == currentLevel) {

            color = 0xFF55FF55;

            /*
             * Previous levels = faded green
             */
        } else if (level.level < currentLevel) {

            color = 0xFF408040;

            /*
             * Future levels = white
             */
        } else {

            color = 0xFFFFFFFF;
        }


        /*
         * =========================
         * LEVEL NUMBER
         * =========================
         */

        String levelText =
                "Lv. " + level.level;

        float scale = .5f;

        drawScaledString(
                graphics,
                levelText,
                x,
                y,
                scale,
                color
        );


        /*
         * =========================
         * EFFECTS
         * =========================
         */

        int diff = -16;

        int effectY = y + 10;

        for (String effect : level.effects) {

            drawScaledString(
                    graphics,
                    effect,
                    x + (int)(8 * scale),
                    effectY,
                    scale,
                    color
            );

            effectY += (int)(8 * scale);
            diff += 8;
        }

        /*
         * =========================
         * DIVIDER
         * =========================
         */

        graphics.fill(
                x,
                effectY + 3,
                x + width,
                effectY + 4,
                0xFFFFFFFF
        );

        return diff;
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != 0) {
            return false;
        }

        /*
         * =========================
         * HELP BUTTON
         * =========================
         */

        int helpButtonSize = 12;

        int helpButtonX =
                leftPos + imageWidth - helpButtonSize - 5;

        int helpButtonY =
                topPos + 4;

        if (mouseX >= helpButtonX
                && mouseX < helpButtonX + helpButtonSize
                && mouseY >= helpButtonY
                && mouseY < helpButtonY + helpButtonSize) {

            Minecraft.getInstance().setScreen(
                    new HelpScreen(this)
            );

            return true;
        }

        /*
         * =========================
         * HELP BOX OPEN
         * =========================
         *
         * Prevent clicking anything underneath
         * the help box.
         */

        /*
         * Everything below this point is your
         * existing mouseClicked() code.
         */

        super.mouseClicked(mouseX, mouseY, button);

        if (!submenuOpen) {
            return false;
        }

        /*
         * Match the exact coordinates used
         * when rendering the perk boxes.
         */
        int width = 80;

        int x = leftPos - width - 4;

        int boxX = x + 6;
        int boxY = topPos + 22;
        int boxWidth = width - 12;
        int boxHeight = 30;
        int spacing = 3;

        List<SkillData> visibleSkills =
                getVisibleSkills();

        int startIndex =
                skillPage * SKILLS_PER_PAGE;

        int endIndex =
                Math.min(
                        startIndex + SKILLS_PER_PAGE,
                        visibleSkills.size()
                );

        for (int i = startIndex; i < endIndex; i++) {

            int pageIndex =
                    i - startIndex;

            int currentY =
                    boxY + pageIndex * (boxHeight + spacing);

            if (mouseX >= boxX
                    && mouseX < boxX + boxWidth
                    && mouseY >= currentY
                    && mouseY < currentY + boxHeight) {

                selectedSkill =
                        visibleSkills.get(i);

                skillLevelPage = 0;

                LightsPerks.LOGGER.debug(
                        "[PerkScreen] Selected skill: {}",
                        selectedSkill.title
                );

                return true;
            }
        }

        int pageCount =
                (int) Math.ceil(
                        (double) visibleSkills.size()
                                / SKILLS_PER_PAGE
                );

        if (pageCount > 1) {

            int buttonSize = 7;
            int pageSpacing = 2;

            int buttonY =
                    topPos + 200 - 8;

            int totalWidth =
                    pageCount * buttonSize
                            + (pageCount - 1) * pageSpacing;

            int startX =
                    x + (width - totalWidth) / 2;

            for (int page = 0; page < pageCount; page++) {

                int buttonX =
                        startX + page * (buttonSize + pageSpacing);

                if (mouseX >= buttonX
                        && mouseX < buttonX + buttonSize
                        && mouseY >= buttonY
                        && mouseY < buttonY + buttonSize) {

                    skillPage = page;

                    /*
                     * If the currently selected skill isn't
                     * on the new page, deselect it.
                     */
                    if (selectedSkill != null) {

                        int selectedIndex =
                                visibleSkills.indexOf(selectedSkill);

                        int newPageStart =
                                skillPage * SKILLS_PER_PAGE;

                        int newPageEnd =
                                Math.min(
                                        newPageStart + SKILLS_PER_PAGE,
                                        visibleSkills.size()
                                );

                        if (selectedIndex < newPageStart
                                || selectedIndex >= newPageEnd) {

                            selectedSkill = null;
                        }
                    }

                    return true;
                }
            }
        }

        if (selectedSkill != null) {

            int skillWidth = 150;

            int skillX =
                    leftPos + imageWidth + 4;

            int levelsX =
                    skillX + 6;

            int levelsWidth =
                    skillWidth - 12;

            int pageLevelsCount =
                    (int) Math.ceil(
                            (double) selectedSkill.levels.size()
                                    / LEVELS_PER_PAGE
                    );

            if (pageLevelsCount > 1) {

                int buttonSize = 7;
                int pageSpacing = 2;

                int buttonY =
                       topPos + 200 - 8;

                int totalWidth =
                        pageLevelsCount * buttonSize
                                + (pageLevelsCount - 1) * pageSpacing;

                int startX =
                        levelsX
                                + (levelsWidth - totalWidth) / 2;

                for (int page = 0; page < pageLevelsCount; page++) {

                    int buttonX =
                            startX
                                    + page * (buttonSize + pageSpacing);

//                    UpgradeScrolls.LOGGER.debug("Button Click at: {} and {}, page buttons at X: {}-{}, and Y: {}-{}",
//                            mouseX, mouseY,
//                            buttonX, buttonX + buttonSize,
//                            buttonY, buttonY + buttonSize
//                    );

                    if (mouseX >= buttonX
                            && mouseX < buttonX + buttonSize
                            && mouseY >= buttonY
                            && mouseY < buttonY + buttonSize) {

                        skillLevelPage = page;

                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void drawScaledString(
            GuiGraphics graphics,
            String text,
            int x,
            int y,
            float scale,
            int color
    ) {
        graphics.pose().pushPose();

        graphics.pose().scale(scale, scale, 1.0F);

        graphics.drawString(
                font,
                text,
                (int)(x / scale),
                (int)(y / scale),
                color
        );

        graphics.pose().popPose();
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.renderBackground(graphics);

        graphics.blit(
                perkMenuTexture,
                leftPos,
                topPos,
                0,
                0,
                176,
                201,
                512,
                512
                );

        renderPlayerModel(graphics, mouseX, mouseY);

        for (Slot slot : menu.slots) {

            boolean enabled = true;

            if (slot instanceof PerkSlot perkSlot) {
                enabled = perkSlot.isActive();
            }

            drawSlotBackground(
                    graphics,
                    leftPos + slot.x - 1,
                    topPos + slot.y - 1,
                    enabled
            );
        }
    }

    private void renderPerkSlotLevels(GuiGraphics graphics) {
        for (Slot slot : menu.slots) {

            if (!(slot instanceof PerkSlot perkSlot)) {
                continue;
            }

            PerkContainer container =
                    perkSlot.getPerkContainer();

            int level =
                    container.getSocketLevel(
                            perkSlot.getContainerSlot()
                    );

            if (level <= 0) {
                continue;
            }

            int uOffset = 407;
            int vOffset = 0;

            switch (level) {
                case 1 -> {
                }
                case 2 ->
                        uOffset += 18;
                case 3 ->
                        uOffset += 36;
                case 4 -> vOffset += 18;
                default -> {
                    continue;
                }
            }

            int x =
                    leftPos + slot.x - 1;

            int y =
                    topPos + slot.y - 1;

            graphics.blit(
                    perkMenuTexture,
                    x,
                    y,
                    uOffset,
                    vOffset,
                    18,
                    18,
                    512,
                    512
            );
        }
    }

    private void renderPlayerModel(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        int x = leftPos + 25;
        int y = topPos + 80;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                x,
                y,
                25,
                (float) (x - mouseX),
                (float) (y - mouseY),
                player
        );
    }

    private void drawSlotBackground(
            GuiGraphics graphics,
            int x,
            int y,
            boolean enabled
    ) {
        graphics.blit(
                perkMenuTexture,
                x,
                y,
                enabled ? 425 : 443,
                18,
                18,
                18,
                512,
                512
        );
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        /*
         * Title
         */
        graphics.drawString(
                font,
                "Perks",
                8,
                6,
                0xFFFFFF
        );

        /*
         * Inventory section
         */
            graphics.drawString(
                    font,
                    "Inventory",
                    8,
                    97,
                    0xFFFFFF
            );
    }

    private void renderEquipmentItems(GuiGraphics graphics) {

        Inventory inventory = menu.getPlayerInventory();

        ItemStack[] equipment = {
                inventory.armor.get(3),
                inventory.armor.get(2),
                inventory.armor.get(1),
                inventory.armor.get(0),
                inventory.player.getMainHandItem(),
                inventory.player.getOffhandItem()
        };

        for (int i = 0; i < equipment.length; i++) {

            ItemStack stack = equipment[i];

            if (stack.isEmpty()) {
                continue;
            }

            graphics.renderItem(
                    stack,
                    leftPos + PerkMenu.EQUIPMENT_X[i],
                    topPos + PerkMenu.EQUIPMENT_Y
            );
        }
    }

    @Override
    protected void renderTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    private List<SkillData> getVisibleSkills() {
        List<SkillData> visibleSkills = new ArrayList<>();

        for (SkillData skill : skills) {

            ResourceLocation skillId =
                    new ResourceLocation(skill.skill_id);

            int currentLevel =
                    points.getOrDefault(skillId, 0);

            if (currentLevel > 0) {
                visibleSkills.add(skill);
            }
        }

        return visibleSkills;
    }
}