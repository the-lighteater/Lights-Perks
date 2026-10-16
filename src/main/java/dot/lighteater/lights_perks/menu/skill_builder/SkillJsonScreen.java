package dot.lighteater.lights_perks.menu.skill_builder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SkillJsonScreen extends Screen {

    private final SkillBuilderScreen parent;

    private MultiLineEditBox jsonBox;

    private String errorMessage = "";

    public SkillJsonScreen(SkillBuilderScreen parent) {
        super(Component.literal("Skill Builder - JSON"));

        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int editorWidth = 600;
        int editorHeight = this.height - 100;
        int editorX = centerX - editorWidth / 2;
        int editorY = 40;

        jsonBox = new MultiLineEditBox(
                this.font,
                editorX,
                editorY,
                editorWidth,
                editorHeight,
                Component.literal("JSON")
        );

        jsonBox.setValue(parent.buildJson());
        jsonBox.setFocused(true);

        this.addRenderableWidget(jsonBox);

        this.addRenderableWidget(
                Button.builder(Component.literal("Apply JSON"), button -> applyJson()
                        ).bounds(
                                centerX - 105,
                                this.height - 45,
                                100,
                                20
                        ).build()
        );


        this.addRenderableWidget(
                Button.builder(Component.literal("Back"), button -> goBack())
                        .bounds(centerX + 5, this.height - 45, 100, 20).build()
        );
    }

    private void applyJson() {
        errorMessage = "";

        try {
            JsonObject json = JsonParser.parseString(jsonBox.getValue()).getAsJsonObject();

            parent.applyJson(json);

            assert this.minecraft != null;
            this.minecraft.setScreen(parent);

        } catch (JsonParseException | IllegalStateException | UnsupportedOperationException e) {
            errorMessage = "Invalid JSON!";
        }
    }

    private void goBack() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        this.renderBackground(graphics);

        graphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                20,
                0xFFFFFFFF
        );

        if (!errorMessage.isEmpty()) graphics.drawCenteredString(
                    this.font,
                    errorMessage,
                    this.width / 2,
                    this.height - 65,
                    0xFFFF5555
        );

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}