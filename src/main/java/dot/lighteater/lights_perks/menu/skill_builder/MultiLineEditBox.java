package dot.lighteater.lights_perks.menu.skill_builder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MultiLineEditBox extends AbstractWidget {

    private final Font font;
    private String value = "";
    private int cursorPosition = 0;
    private int selectionAnchor = 0;
    private int scrollLine = 0;
    private int scrollX = 0;
    private boolean focused = false;
    private long cursorBlinkTime = 0;
    private boolean dragging = false;
    private static final int LINE_HEIGHT_EXTRA = 2;
    private static final int PADDING = 5;
    private static final int LINE_NUMBER_WIDTH = 38;
    private static final int SCROLL_STEP = 3;
    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_STRING = 0xFFA8D8FF;
    private static final int COLOR_NUMBER = 0xFFFFD479;
    private static final int COLOR_BOOLEAN = 0xFFFF8CFF;
    private static final int COLOR_NULL = 0xFFAAAAAA;
    private static final int COLOR_PUNCTUATION = 0xFFDDDDDD;
    private static final int COLOR_PROPERTY = 0xFF9CDCFE;
    private static final int COLOR_LINE_NUMBER = 0xFF666666;
    private static final int COLOR_SELECTION = 0x805A78A0;

    public MultiLineEditBox(Font font, int x, int y,
            int width, int height, Component message) {
        super(x, y, width, height, message);
        this.font = font;
        resetCursorBlink();
    }

    public String getValue() {return value;}

    public void setValue(String value) {
        this.value = value != null ? value : "";

        cursorPosition = Math.min(
                        cursorPosition,
                        this.value.length());

        selectionAnchor = Math.min(
                        selectionAnchor,
                        this.value.length());

        ensureCursorVisible();
        resetCursorBlink();
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) resetCursorBlink();
    }

    @Override
    public boolean isFocused() {return focused;}

    private boolean hasSelection() {return cursorPosition != selectionAnchor;}

    private int selectionStart() {
        return Math.min(
                cursorPosition,
                selectionAnchor
        );
    }

    private int selectionEnd() {
        return Math.max(
                cursorPosition,
                selectionAnchor
        );
    }

    private void clearSelection() {selectionAnchor = cursorPosition;}

    private void selectAll() {
        selectionAnchor = 0;
        cursorPosition = value.length();

        ensureCursorVisible();
        resetCursorBlink();
    }

    private void deleteSelection() {
        if (!hasSelection()) return;

        int start = selectionStart();
        int end = selectionEnd();

        value = value.substring(0, start) + value.substring(end);

        cursorPosition = start;
        selectionAnchor = start;

        ensureCursorVisible();
        resetCursorBlink();
    }

    private void resetCursorBlink() {
        cursorBlinkTime = System.currentTimeMillis();
    }

    private boolean shouldDrawCursor() {
        if (!focused) return false;
        long elapsed = System.currentTimeMillis() - cursorBlinkTime;
        return (elapsed / 500L) % 2L == 0L;
    }

    private int getCursorLine() {
        int line = 0;

        for (int i = 0; i < cursorPosition; i++) {
            if (value.charAt(i) == '\n') line++;
        }

        return line;
    }

    private int getCursorColumn() {
        int lastNewLine = value.lastIndexOf('\n',
                        Math.max(0,
                                cursorPosition - 1
                        ));

        if (lastNewLine < 0) return cursorPosition;

        return cursorPosition - lastNewLine - 1;
    }

    private int getLineStart(int line) {
        if (line <= 0) return 0;

        int currentLine = 0;

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\n') {
                currentLine++;
                if (currentLine == line) return i + 1;
            }
        }

        return value.length();
    }

    private int getLineEnd(int line) {
        int start = getLineStart(line);
        int end = value.indexOf('\n', start);

        if (end < 0) return value.length();

        return end;
    }

    private String[] getLines() {
        return value.split("\n", -1);
    }

    private void moveCursor(int newPosition, boolean selecting) {
        newPosition = Math.max(0,
                        Math.min(newPosition,
                                value.length()));

        cursorPosition = newPosition;
        if (!selecting) selectionAnchor = cursorPosition;

        ensureCursorVisible();
        resetCursorBlink();
    }

    private void moveCursorLeft(boolean selecting) {
        if (!selecting && hasSelection()) {
            moveCursor(selectionStart(), false);
            return;
        }

        if (cursorPosition > 0) moveCursor(cursorPosition - 1, selecting);
    }

    private void moveCursorRight(boolean selecting) {
        if (!selecting && hasSelection()) {
            moveCursor(selectionEnd(), false);
            return;
        }

        if (cursorPosition < value.length()) moveCursor(cursorPosition + 1, selecting);
    }

    private void moveCursorHome(boolean selecting) {
        moveCursor(getLineStart(getCursorLine()), selecting);
    }

    private void moveCursorEnd(boolean selecting) {
        moveCursor(getLineEnd(getCursorLine()), selecting);
    }

    private void moveCursorUp(boolean selecting) {
        int line = getCursorLine();

        if (line <= 0) return;

        int column = getCursorColumn();
        int previousLine = line - 1;
        int start = getLineStart(previousLine);
        int end = getLineEnd(previousLine);

        moveCursor(Math.min(start + column, end), selecting);
    }

    private void moveCursorDown(boolean selecting) {
        int line = getCursorLine();
        String[] lines = getLines();

        if (line >= lines.length - 1) return;

        int column = getCursorColumn();
        int nextLine = line + 1;
        int start = getLineStart(nextLine);
        int end = getLineEnd(nextLine);

        moveCursor(Math.min(start + column, end), selecting);
    }

    private int getVisibleLineCount() {
        return Math.max(1, (getHeight() - 8) / getLineHeight());
    }

    private int getLineHeight() {
        return font.lineHeight + LINE_HEIGHT_EXTRA;
    }

    private int getMaxScrollLine() {
        String[] lines = getLines();

        return Math.max(0,
                lines.length - getVisibleLineCount());
    }

    private int getMaxScrollX() {
        String[] lines = getLines();
        int maxWidth = 0;

        for (String line : lines) {
            maxWidth = Math.max(maxWidth, font.width(line));
        }

        int availableWidth = getWidth() - LINE_NUMBER_WIDTH - PADDING - 5;

        return Math.max(0, maxWidth - availableWidth);
    }

    private void scrollVertical(int amount) {
        scrollLine = Math.max(0, Math.min(
                                getMaxScrollLine(),
                                scrollLine + amount));
    }

    private void scrollHorizontal(int amount) {
        scrollX = Math.max(0, Math.min(
                                getMaxScrollX(),
                                scrollX + amount));
    }

    private void ensureCursorVisible() {
        int line = getCursorLine();
        int visibleLines = getVisibleLineCount();

        if (line < scrollLine) scrollLine = line;
        if (line >= scrollLine + visibleLines) scrollLine = line - visibleLines + 1;

        int cursorColumn = getCursorColumn();
        String[] lines = getLines();

        if (line < lines.length) {
            String before =
                    lines[line].substring(0,
                            Math.min(
                                    cursorColumn,
                                    lines[line].length()));

            int cursorX = font.width(before);
            int availableWidth = getWidth() - LINE_NUMBER_WIDTH - PADDING - 5;

            if (cursorX < scrollX) scrollX = cursorX;

            if (cursorX > scrollX + availableWidth) scrollX =
                        cursorX - availableWidth + 10;
        }

        scrollX = Math.max(0, Math.min(scrollX, getMaxScrollX()));
    }

    private void insertText(String text) {
        if (text == null || text.isEmpty()) return;

        deleteSelection();

        value = value.substring(0, cursorPosition)
                        + text + value.substring(cursorPosition);

        cursorPosition += text.length();
        selectionAnchor = cursorPosition;

        ensureCursorVisible();
        resetCursorBlink();
    }

    private int getPositionFromMouse(
            double mouseX,
            double mouseY
    ) {
        int localX = (int) mouseX - getX() - LINE_NUMBER_WIDTH - PADDING + scrollX;
        int localY = (int) mouseY - getY() - PADDING;
        int line = scrollLine + Math.max(0, localY / getLineHeight());
        String[] lines = getLines();

        if (line >= lines.length) return value.length();

        String text = lines[line];

        if (localX <= 0) return getLineStart(line);

        int bestColumn = 0;
        int bestDistance = Integer.MAX_VALUE;

        for (int i = 0; i <= text.length(); i++) {
            int width = font.width(text.substring(0, i));
            int distance = Math.abs(width - localX);

            if (distance < bestDistance) {
                bestDistance = distance;
                bestColumn = i;
            }
        }

        return getLineStart(line) + bestColumn;
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!isMouseOver(mouseX, mouseY)) return false;

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

        setFocused(true);

        int position = getPositionFromMouse(mouseX, mouseY);

        cursorPosition = position;
        selectionAnchor = position;

        dragging = true;

        resetCursorBlink();
        ensureCursorVisible();

        return true;
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        if (!dragging || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

        cursorPosition = getPositionFromMouse(
                        Math.max(getX() + 2, Math.min(getX() + getWidth() - 2, mouseX)),
                        Math.max(getY() + 2, Math.min(getY() + getHeight() - 2, mouseY)));

        ensureCursorVisible();
        resetCursorBlink();

        return true;
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double delta
    ) {

        if (!isMouseOver(mouseX, mouseY)) return false;

        boolean shift =
                (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(),
                        GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) ||
                        (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(),
                                GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS);

        int amount = delta > 0 ? -SCROLL_STEP : SCROLL_STEP;

        if (shift) scrollHorizontal(amount * 8);
        else scrollVertical(amount);

        return true;
    }


    @Override
    public boolean charTyped(
            char codePoint,
            int modifiers
    ) {
        if (!focused) return false;

        if (codePoint >= 32 || codePoint == '\n' || codePoint == '\t') {
            insertText(String.valueOf(codePoint));
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (!focused) return false;

        boolean control = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;

        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;

        if (control && keyCode == GLFW.GLFW_KEY_A) {
            selectAll();
            return true;
        }

        if (control && keyCode == GLFW.GLFW_KEY_C) {
            if (hasSelection()) Minecraft.getInstance().keyboardHandler
                    .setClipboard(value.substring(selectionStart(), selectionEnd()));
             else Minecraft.getInstance().keyboardHandler.setClipboard(value);

            return true;
        }

        if (control && keyCode == GLFW.GLFW_KEY_X) {
            if (hasSelection()) {
                Minecraft.getInstance().keyboardHandler.setClipboard(
                        value.substring(selectionStart(), selectionEnd()));
                deleteSelection();
            }

            return true;
        }

        if (control && keyCode == GLFW.GLFW_KEY_V) {
            String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
            insertText(clipboard);

            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (hasSelection()) deleteSelection();
            else if (cursorPosition > 0) {
                value = value.substring(0, cursorPosition - 1) + value.substring(cursorPosition);
                cursorPosition--;
                selectionAnchor = cursorPosition;

                ensureCursorVisible();
                resetCursorBlink();
            }

            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_DELETE) {
            if (hasSelection()) deleteSelection();
            else if (cursorPosition < value.length()) {
                value = value.substring(0, cursorPosition)
                                + value.substring(cursorPosition + 1);

                resetCursorBlink();
            }

            return true;
        }

        switch (keyCode) {
            case (GLFW.GLFW_KEY_LEFT) -> {
                moveCursorLeft(shift);
                return true;
            }
            case (GLFW.GLFW_KEY_RIGHT) -> {
                moveCursorRight(shift);
                return true;
            }
            case (GLFW.GLFW_KEY_UP) -> {
                moveCursorUp(shift);
                return true;
            }
            case (GLFW.GLFW_KEY_DOWN) -> {
                moveCursorDown(shift);
                return true;
            }
            case (GLFW.GLFW_KEY_HOME) -> {
                moveCursorHome(shift);
                return true;
            }
            case (GLFW.GLFW_KEY_END) -> {
                moveCursorEnd(shift);
                return true;
            }
            default -> {
                return super.keyPressed(
                        keyCode,
                        scanCode,
                        modifiers
                );
            }
        }
    }

    @Override
    public void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        graphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                0xFF101010
        );

        graphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + 1,
                0xFFFFFFFF
        );

        graphics.fill(
                getX(),
                getY(),
                getX() + 1,
                getY() + getHeight(),
                0xFFFFFFFF
        );

        graphics.fill(
                getX(),
                getY() + getHeight() - 1,
                getX() + getWidth(),
                getY() + getHeight(),
                0xFF000000
        );

        graphics.fill(
                getX() + getWidth() - 1,
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                0xFF000000
        );

        graphics.enableScissor(
                getX() + 1,
                getY() + 1,
                getX() + getWidth() - 1,
                getY() + getHeight() - 1
        );

        String[] lines = getLines();
        int lineHeight = getLineHeight();
        int drawY = getY() + PADDING;

        graphics.fill(
                getX() + 1,
                getY() + 1,
                getX() + LINE_NUMBER_WIDTH,
                getY() + getHeight() - 1,
                0xFF151515
        );

        graphics.fill(
                getX() + LINE_NUMBER_WIDTH,
                getY() + 1,
                getX() + LINE_NUMBER_WIDTH + 1,
                getY() + getHeight() - 1,
                0xFF303030
        );

        for (int lineIndex = scrollLine; lineIndex < lines.length; lineIndex++) {
            if (drawY + lineHeight > getY() + getHeight()) break;

            String line = lines[lineIndex];

            String lineNumber = String.valueOf(lineIndex + 1);

            int lineNumberWidth = font.width(lineNumber);

            graphics.drawString(
                    font,
                    lineNumber,
                    getX() + LINE_NUMBER_WIDTH - lineNumberWidth - 5,
                    drawY,
                    COLOR_LINE_NUMBER
            );

            drawSelectionForLine(
                    graphics,
                    lineIndex,
                    line,
                    drawY
            );

            drawSyntaxHighlightedLine(
                    graphics,
                    line,
                    drawY
            );

            if (shouldDrawCursor() && getCursorLine() == lineIndex) {
                int column = getCursorColumn();

                String before = line.substring(0, Math.min(column, line.length()));

                int cursorX = getX() + LINE_NUMBER_WIDTH + PADDING + font.width(before) - scrollX;

                graphics.fill(
                        cursorX,
                        drawY - 1,
                        cursorX + 1,
                        drawY + font.lineHeight,
                        COLOR_TEXT
                );
            }
            drawY += lineHeight;
        }

        graphics.disableScissor();
    }

    private void drawSelectionForLine(
            GuiGraphics graphics,
            int lineIndex,
            String line,
            int drawY
    ) {
        if (!hasSelection()) return;

        int start = selectionStart();
        int end = selectionEnd();
        int lineStart = getLineStart(lineIndex);
        int lineEnd = getLineEnd(lineIndex);
        int selectionStart = Math.max(start, lineStart);
        int selectionEnd = Math.min(end, lineEnd);

        if (selectionStart > selectionEnd) return;

        int localStart = selectionStart - lineStart;
        int localEnd = selectionEnd - lineStart;

        String beforeStart = line.substring(0, Math.min(localStart, line.length()));
        String selectedText = line.substring(Math.min(localStart, line.length()), Math.min(localEnd, line.length()));

        int startX = getX() + LINE_NUMBER_WIDTH + PADDING + font.width(beforeStart) - scrollX;

        int selectionWidth = font.width(selectedText);

        if (selectionWidth == 0) selectionWidth = 4;

        graphics.fill(
                startX,
                drawY - 1,
                startX + selectionWidth,
                drawY + font.lineHeight,
                COLOR_SELECTION
        );
    }

    private void drawSyntaxHighlightedLine(
            GuiGraphics graphics,
            String line,
            int drawY
    ) {
        int x = getX() + LINE_NUMBER_WIDTH + PADDING - scrollX;

        List<Token> tokens = tokenizeJsonLine(line);

        for (Token token : tokens) {
            graphics.drawString(
                    font,
                    token.text,
                    x,
                    drawY,
                    token.color
            );

            x += font.width(token.text);
        }
    }

    private List<Token> tokenizeJsonLine(String line) {
        List<Token> tokens = new ArrayList<>();

        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);

            if (c == '"') {
                int start = i;
                i++;

                boolean escaped = false;
                while (i < line.length()) {
                    char current = line.charAt(i);

                    if (current == '\\' && !escaped) {
                        escaped = true;
                        i++;
                        continue;
                    }

                    if (current == '"' && !escaped) {
                        i++;
                        break;
                    }

                    escaped = false;
                    i++;
                }

                String text = line.substring(start, i);

                int next = i;

                while (next < line.length() && Character.isWhitespace(line.charAt(next))) next++;

                int color = next < line.length() &&
                        line.charAt(next) == ':' ? COLOR_PROPERTY : COLOR_STRING;

                tokens.add(new Token(text, color));

                continue;
            }

            if (c == '-' || Character.isDigit(c)) {
                int start = i;
                i++;

                while (i < line.length()) {
                    char current = line.charAt(i);

                    if (Character.isDigit(current)
                            || current == '.'
                            || current == 'e'
                            || current == 'E'
                            || current == '+'
                            || current == '-') {

                        i++;

                    } else break;
                }

                tokens.add(new Token(line.substring(start, i), COLOR_NUMBER));

                continue;
            }

            if (Character.isLetter(c)) {
                int start = i;

                while (i < line.length() && Character.isLetter(line.charAt(i))) i++;

                String word = line.substring(start, i);

                int color;

                if ("true".equals(word) || "false".equals(word))
                    color = COLOR_BOOLEAN;
                else if ("null".equals(word))
                    color = COLOR_NULL;
                else
                    color = COLOR_TEXT;

                tokens.add(new Token(word, color));

                continue;
            }

            tokens.add(new Token(String.valueOf(c), isJsonPunctuation(c) ? COLOR_PUNCTUATION : COLOR_TEXT));

            i++;
        }

        return tokens;
    }

    private boolean isJsonPunctuation(char c) {
        return c == '{' || c == '}'
                || c == '[' || c == ']'
                || c == ':' || c == ',';
    }

    private static class Token {
        final String text;
        final int color;

        Token(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}