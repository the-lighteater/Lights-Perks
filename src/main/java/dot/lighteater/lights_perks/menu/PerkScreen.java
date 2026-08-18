package dot.lighteater.lights_perks.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import dot.lighteater.lights_perks.ClientConfig;
import dot.lighteater.lights_perks.ModKeyBindings;
import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.helpers.EquipmentType;
import dot.lighteater.lights_perks.skill.SkillData;
import dot.lighteater.lights_perks.skill.SkillLevelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class PerkScreen extends AbstractContainerScreen<PerkMenu> {

    private static final ResourceLocation SLOT_TEXTURE =
            new ResourceLocation(
                    "minecraft",
                    "textures/gui/container/inventory.png"
            );

    private boolean submenuOpen = false;
    private SkillData selectedSkill = null;

    private List<SkillData> testSkills = List.of(
            new SkillData("Attack Up",0xFF7A3030, 5, 3, 1,
                    List.of(
                            new SkillLevelData(
                                    1,
                                    false,
                                    "+5 Attack Damage"
                            ),

                            new SkillLevelData(
                                    2,
                                    false,
                                    "+10 Attack Damage",
                                    "+5% Attack Speed"
                            ),

                            new SkillLevelData(
                                    3,
                                    false,
                                    "+15 Attack Damage",
                                    "+10% Attack Speed"
                            )
                    ), Map.of(EquipmentType.HELMET, 1, EquipmentType.CHESTPLATE, 0)),
            new SkillData("Defense Up",0xFF7A4030, 8, 4, 0,
                    List.of(
                            new SkillLevelData(
                                    1,
                                    false,
                                    "+5 Attack Damage"
                            ),

                            new SkillLevelData(
                                    2,
                                    false,
                                    "+10 Attack Damage",
                                    "+5% Attack Speed"
                            ),

                            new SkillLevelData(
                                    3,
                                    false,
                                    "+15 Attack Damage",
                                    "+10% Attack Speed"
                            )
                    ), Map.of(EquipmentType.HELMET, 1, EquipmentType.CHESTPLATE, 0)),
            new SkillData("Recovery",0xFF7A7020, 3, 2, 0,
                    List.of(
                            new SkillLevelData(
                                    1,
                                    false,
                                    "+5 Attack Damage"
                            ),

                            new SkillLevelData(
                                    2,
                                    false,
                                    "+10 Attack Damage",
                                    "+5% Attack Speed"
                            ),

                            new SkillLevelData(
                                    3,
                                    false,
                                    "+15 Attack Damage",
                                    "+10% Attack Speed"
                            )
                    ), Map.of(EquipmentType.HELMET, 1, EquipmentType.CHESTPLATE, 0)),
            new SkillData("Critical Eye",0xFF7A1010, 4, 1, 2,
                    List.of(
                            new SkillLevelData(
                                    1,
                                    false,
                                    "+5 Attack Damage"
                            ),

                            new SkillLevelData(
                                    2,
                                    false,
                                    "+10 Attack Damage",
                                    "+5% Attack Speed"
                            ),

                            new SkillLevelData(
                                    3,
                                    false,
                                    "+15 Attack Damage",
                                    "+10% Attack Speed"
                            ),

                            new SkillLevelData(
                                    4,
                                    false,
                                    "+15 Attack Damage",
                                    "+10% Attack Speed"
                            ),

                            new SkillLevelData(
                                    5,
                                    true,
                                    "+15 Attack Damage",
                                    "+10% Attack Speed"
                            ),

                            new SkillLevelData(
                                    6,
                                    true,
                                    "+15 Attack Damage",
                                    "+10% Attack Speed"
                            )
                    ), Map.of(EquipmentType.HELMET, 1, EquipmentType.CHESTPLATE, 0))
    );

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

        renderEquipmentItems(graphics);

        renderPerkSubmenu(graphics, mouseX, mouseY);

        if (selectedSkill != null) {
            renderSkillSubmenu(
                    graphics,
                    mouseX,
                    mouseY,
                    selectedSkill
            );
        }

        this.renderTooltip(graphics, mouseX, mouseY);
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
        int height = 180;

        int x = leftPos - width - 4;
        int y = topPos + 20;

        /*
         * Submenu background
         */
        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF202020
        );

        graphics.fill(
                x + 2,
                y + 2,
                x + width - 2,
                y + height - 2,
                0xFF303030
        );

        /*
         * Title
         */
        graphics.drawString(
                font,
                "Perks",
                x + 6,
                y + 6,
                0xFFFFFF
        );

        /*
         * Four perk boxes
         */
        int boxX = x + 6;
        int boxY = y + 22;
        int boxWidth = width - 12;
        int boxHeight = 30;
        int spacing = 3;

        for (int i = 0; i < 4; i++) {

            int currentY = boxY + i * (boxHeight + spacing);

            renderPerkBox(
                    testSkills.get(i),
                    graphics,
                    boxX,
                    currentY,
                    boxWidth,
                    boxHeight,
                    i
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

        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF080808
        );

        /*
         * =========================
         * COLORED BACKGROUND
         * =========================
         */

        int innerX = x + 2;
        int innerY = y + 2;
        int innerWidth = width - 4;
        int innerHeight = height - 4;

        graphics.fill(
                innerX,
                innerY,
                innerX + innerWidth,
                innerY + innerHeight,
                skillData.color
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
        int titleY = y + 3;

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

        int bonusLevels = Math.max(
                skillData.bonusLevel,
                0
        );

        int totalLevels = Math.max(
                normalLevels + bonusLevels,
                1
        );

        int currentLevel = Math.max(
                0,
                Math.min(
                        skillData.currLevel,
                        normalLevels
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
                    level >= normalLevels;

            boolean isCompleted =
                    !isBonus && level < currentLevel;

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
                    0xFF080808
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
                innerColor = skillData.color;
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
        int height = 280;

        /*
         * Place it immediately to the right
         * of the main menu.
         */
        int x = leftPos + imageWidth + 4;
        int y = topPos + 20;

        /*
         * =========================
         * OUTER BACKGROUND
         * =========================
         */

        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF080808
        );

        /*
         * =========================
         * INNER BACKGROUND
         * =========================
         */

        graphics.fill(
                x + 2,
                y + 2,
                x + width - 2,
                y + height - 2,
                0xFF303030
        );

        /*
         * =========================
         * TITLE
         * =========================
         */

        graphics.drawString(
                font,
                skillData.title,
                x + 6,
                y + 6,
                0xFFFFFF
        );

        /*
         * =========================
         * EQUIPMENT SOURCES
         * =========================
         */

        renderSkillEquipmentSources(
                graphics,
                skillData,
                x + 6,
                y + 22,
                width - 12
        );

        /*
         * =========================
         * LEVELS
         * =========================
         */

        renderSkillLevels(
                graphics,
                skillData,
                x + 6,
                y + 68,
                width - 12
        );
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

        int boxHeight = 18;

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
            int equipmentLevel =
                    skillData.points.getOrDefault(
                            equipmentType,
                            0
                    );

            /*
             * =========================
             * OUTER BORDER
             * =========================
             */

            graphics.fill(
                    boxX,
                    boxY,
                    boxX + boxWidth,
                    boxY + boxHeight,
                    0xFF080808
            );

            /*
             * =========================
             * INTERIOR
             * =========================
             */

            graphics.fill(
                    boxX + 1,
                    boxY + 1,
                    boxX + boxWidth - 1,
                    boxY + boxHeight - 1,
                    0xFF404040
            );

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
        int currentLevel =
                skillData.currLevel;

        int currentY = y;

        for (int i = 0; i < skillData.levels.size(); i++) {

            SkillLevelData level =
                    skillData.levels.get(i);

            /*
             * Render the level.
             */
            int diff = renderSkillLevel(
                    graphics,
                    level,
                    currentLevel,
                    x,
                    currentY,
                    width
            );

            /*
             * Move down for the next level.
             */
            currentY += (40 + diff);
        }
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

            color = 0xFF81EBE3;

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

        super.mouseClicked(mouseX, mouseY, button);

        if (button != 0) {
            return false;
        }

        if (!submenuOpen) {
            return false;
        }

        /*
         * Match the exact coordinates used
         * when rendering the perk boxes.
         */
        int width = 80;
        int height = 180;

        int x = leftPos - width - 4;
        int y = topPos + 20;

        int boxX = x + 6;
        int boxY = y + 22;
        int boxWidth = width - 12;
        int boxHeight = 30;
        int spacing = 3;

        for (int i = 0; i < testSkills.size(); i++) {

            int currentY =
                    boxY + i * (boxHeight + spacing);

            if (mouseX >= boxX
                    && mouseX < boxX + boxWidth
                    && mouseY >= currentY
                    && mouseY < currentY + boxHeight) {

                selectedSkill = testSkills.get(i);

                UpgradeScrolls.LOGGER.debug(
                        "[PerkScreen] Selected skill: {}",
                        selectedSkill.title
                );

                return true;
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

        /*
         * Main background
         */
        graphics.fill(
                leftPos,
                topPos,
                leftPos + imageWidth,
                topPos + imageHeight,
                0xFF202020
        );

        /*
         * Perk section
         */
        graphics.fill(
                leftPos + 4,
                topPos + 20,
                leftPos + 172,
                topPos + 95,
                0xFF303030
        );

        /*
         * Inventory section
         */
        graphics.fill(
                leftPos + 4,
                topPos + 93,
                leftPos + 172,
                topPos + 196,
                0xFF403030
        );

        renderPlayerModel(graphics, mouseX, mouseY);

        for (Slot slot : menu.slots) {

            boolean enabled = true;

            if (slot instanceof PerkSlot perkSlot) {
                enabled = perkSlot.getPerkContainer().hasEquipment();
            }

            drawSlotBackground(
                    graphics,
                    leftPos + slot.x - 1,
                    topPos + slot.y - 1,
                    enabled
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

        int x = leftPos + 20;
        int y = topPos + 85;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                x,
                y,
                30,
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
        /*
         * Outer border
         */
        graphics.fill(
                x,
                y,
                x + 18,
                y + 18,
                enabled
                        ? 0xFF101010
                        : 0xFF080808
        );

        /*
         * Inner slot
         */
        graphics.fill(
                x + 1,
                y + 1,
                x + 17,
                y + 17,
                enabled
                        ? 0xFF8B8B8B
                        : 0xFF404040
        );

        /*
         * Inner shadow/highlight
         */
        graphics.fill(
                x + 2,
                y + 2,
                x + 16,
                y + 16,
                enabled
                        ? 0xFF373737
                        : 0xFF202020
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

    private ItemStack getEquipmentStack(EquipmentType type) {
        return switch (type) {
            case HELMET -> menu.getPlayerInventory().armor.get(3);
            case CHESTPLATE -> menu.getPlayerInventory().armor.get(2);
            case LEGGINGS -> menu.getPlayerInventory().armor.get(1);
            case BOOTS -> menu.getPlayerInventory().armor.get(0);
            case MAIN_HAND -> menu.getPlayerInventory().player.getMainHandItem();
            case OFF_HAND -> menu.getPlayerInventory().player.getOffhandItem();
        };
    }

    @Override
    protected void renderTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        super.renderTooltip(graphics, mouseX, mouseY);
    }
}