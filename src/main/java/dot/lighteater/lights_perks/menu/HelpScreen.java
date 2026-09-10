package dot.lighteater.lights_perks.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HelpScreen extends Screen {

    private final Screen parent;

    private static final int WIDTH = 150;
    private static final int HEIGHT = 190;

    private static final String[] HELP_TEXT = {
            "As you explore the world you'll obtain",
            "new items classified as Perks.",
            "",
            "Perks are used to equip onto your",
            "equipment in a new menu. Each equipment has 0-3",
            "slots, and these slots have levels.",
            "",
            "A perk also has levels, and its level needs to be",
            "lesser than or equal to the slot's level to be equipped.",
            "",
            "Equipped perks, armor sets, and individual",
            "armor pieces can contain points for Skills.",
            "",
            "Skills are where the game-changing effects",
            "come in. Skills can change effects,",
            "attributes, or even enchantments for the player.",
            "",
            "A skill is viewable in the perk menu, and",
            "will display its levels, where points are",
            "coming from, and a synopsis of the skill."
    };

    public HelpScreen(Screen parent) {
        super(Component.literal("Perks Help"));

        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        this.renderBackground(graphics);

        int x = (this.width - WIDTH) / 2;
        int y = (this.height - HEIGHT) / 2;

        /*
         * =========================
         * BLACK BORDER
         * =========================
         */

        graphics.fill(
                x - 2,
                y - 2,
                x + WIDTH + 2,
                y + HEIGHT + 2,
                0xFF000000
        );

        /*
         * =========================
         * DARK GREY BOX
         * =========================
         */

        graphics.fill(
                x,
                y,
                x + WIDTH,
                y + HEIGHT,
                0xFF303030
        );

        /*
         * =========================
         * TITLE
         * =========================
         */

        graphics.drawString(
                this.font,
                "Perks Help",
                x + 6,
                y + 6,
                0xFFFFFFFF
        );

        /*
         * =========================
         * TEXT
         * =========================
         */

        float textScale = 0.5F;

        int textY = y + 20;

        for (String line : HELP_TEXT) {

            if (!line.isEmpty()) {

                drawScaledString(
                        graphics,
                        line,
                        x + 6,
                        textY,
                        textScale,
                        0xFFFFFFFF
                );
            }

            textY += 7;
        }

        /*
         * =========================
         * ESC HINT
         * =========================
         */

        graphics.drawString(
                this.font,
                "Press ESC to return",
                x + 6,
                y + HEIGHT - 12,
                0xFFAAAAAA
        );
    }

    public void drawScaledString(
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
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}