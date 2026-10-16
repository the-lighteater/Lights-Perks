package dot.lighteater.lights_perks;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public final class SkillActivationClientHandler {

    /*
     * Maximum number of notifications visible at once.
     */
    private static final int MAX_VISIBLE = 3;

    /*
     * How long a notification stays visible.
     */
    private static final int DISPLAY_TICKS = 70;

    /*
     * How long the slide-in animation lasts.
     */
    private static final int SLIDE_IN_TICKS = 8;

    /*
     * How long the slide-out animation lasts.
     */
    private static final int SLIDE_OUT_TICKS = 8;

    /*
     * Vertical distance between notifications.
     */
    private static final int NOTIFICATION_HEIGHT = 32;

    /*
     * Distance from the right edge of the HUD.
     */
    private static final int RIGHT_MARGIN = 12;

    /*
     * Starting vertical position.
     *
     * This is relative to the top of the HUD.
     */
    private static final int TOP_MARGIN = 55;

    /*
     * Horizontal distance a notification starts off-screen.
     */
    private static final int SLIDE_DISTANCE = 160;

    /*
     * Sound settings.
     */
    private static final float SOUND_VOLUME = 0.18F;
    private static final float SOUND_PITCH_ACTIVATED = 0.75F;
    private static final float SOUND_PITCH_DEACTIVATED = 0.65F;

    /*
     * Currently visible notifications.
     *
     * The front of the deque is the top notification.
     */
    private static final Deque<Notification> NOTIFICATIONS =
            new ArrayDeque<>();

    private SkillActivationClientHandler() {
    }

    /**
     * Called when the server tells the client that a skill
     * has activated or deactivated.
     */
    public static void show(
            String message,
            boolean activated
    ) {
        if (message == null || message.isBlank()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        /*
         * If there are already three notifications,
         * dismiss the bottom one immediately.
         */
        while (NOTIFICATIONS.size() >= MAX_VISIBLE) {
            Notification bottom = NOTIFICATIONS.pollLast();

            if (bottom != null) {
                bottom.startImmediateExit();
            }
        }

        Notification notification =
                new Notification(message, activated);

        NOTIFICATIONS.addFirst(notification);

        playNotificationSound(minecraft, activated);
    }

    /**
     * Draws all active notifications.
     */
    public static void render(
            GuiGraphics graphics,
            int screenWidth,
            int screenHeight
    ) {
        if (NOTIFICATIONS.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        /*
         * Remove notifications that have finished
         * their slide-out animation.
         */
        Iterator<Notification> iterator =
                NOTIFICATIONS.iterator();

        while (iterator.hasNext()) {
            Notification notification = iterator.next();

            if (notification.finished) {
                iterator.remove();
            }
        }

        int slot = 0;

        /*
         * Render from top to bottom.
         */
        for (Notification notification : NOTIFICATIONS) {

            float targetY =
                    TOP_MARGIN
                            + (slot * NOTIFICATION_HEIGHT);

            float y =
                    notification.getAnimatedY(targetY);

            renderNotification(
                    graphics,
                    minecraft,
                    notification,
                    screenWidth,
                    screenHeight,
                    y
            );

            slot++;
        }
    }

    private static void renderNotification(
            GuiGraphics graphics,
            Minecraft minecraft,
            Notification notification,
            int screenWidth,
            int screenHeight,
            float y
    ) {
        Component text = Component.literal(
                notification.message
        ).withStyle(
                notification.activated
                        ? ChatFormatting.GOLD
                        : ChatFormatting.GRAY
        );

        int textWidth =
                minecraft.font.width(text);

        /*
         * Notification dimensions.
         */
        int paddingX = 10;
        int paddingY = 5;

        int width =
                textWidth + (paddingX * 2);

        int height =
                19 + (paddingY * 2);

        /*
         * Target X position is attached to the
         * right side of the HUD.
         */
        float targetX =
                screenWidth
                        - RIGHT_MARGIN
                        - width;

        /*
         * Animate the notification horizontally.
         */
        float x =
                notification.getAnimatedX(
                        targetX
                );

        /*
         * Round the animated position only for
         * final drawing.
         */
        int drawX = Math.round(x);
        int drawY = Math.round(y);

        /*
         * Slightly different background depending
         * on whether this is an activation or
         * deactivation.
         */
        int backgroundColor =
                notification.activated
                        ? 0xB0201810
                        : 0xB0181818;

        /*
         * Main background.
         */
        graphics.fill(
                drawX,
                drawY,
                drawX + width,
                drawY + height,
                backgroundColor
        );

        /*
         * Small accent bar on the left.
         */
        int accentColor =
                notification.activated
                        ? 0xFFE0B04A
                        : 0xFF888888;

        graphics.fill(
                drawX,
                drawY,
                drawX + 2,
                drawY + height,
                accentColor
        );

        /*
         * Text.
         */
        graphics.drawString(
                minecraft.font,
                text,
                drawX + paddingX,
                drawY + paddingY + 1,
                0xFFFFFF,
                false
        );
    }

    /**
     * Called once per client tick.
     */
    public static void tick() {
        if (NOTIFICATIONS.isEmpty()) {
            return;
        }

        Iterator<Notification> iterator =
                NOTIFICATIONS.iterator();

        while (iterator.hasNext()) {
            Notification notification = iterator.next();

            notification.tick();

            if (notification.finished) {
                iterator.remove();
            }
        }
    }

    /**
     * Plays a quiet amethyst sound when a notification
     * appears.
     */
    private static void playNotificationSound(
            Minecraft minecraft,
            boolean activated
    ) {
        if (minecraft.player == null) {
            return;
        }

        float pitch = activated
                ? SOUND_PITCH_ACTIVATED
                : SOUND_PITCH_DEACTIVATED;

        minecraft.player.playSound(
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SOUND_VOLUME,
                pitch
        );
    }

    /**
     * Clears all notifications.
     */
    public static void clear() {
        NOTIFICATIONS.clear();
    }

    private static final class Notification {

        private final String message;
        private final boolean activated;

        private float currentY;
        private float previousY;
        private boolean positionInitialized;

        /*
         * Age of the notification.
         */
        private int age;

        /*
         * When true, the notification is sliding
         * out of the HUD.
         */
        private boolean exiting;

        /*
         * Age of the exit animation.
         */
        private int exitAge;

        /*
         * Once finished, the notification gets
         * removed from the deque.
         */
        private boolean finished;

        private Notification(
                String message,
                boolean activated
        ) {
            this.message = message;
            this.activated = activated;
            this.age = 0;
            this.exiting = false;
            this.exitAge = 0;
            this.finished = false;
            this.currentY = 0.0F;
            this.previousY = 0.0F;
            this.positionInitialized = false;
        }

        private void tick() {
            if (finished) {
                return;
            }

            if (exiting) {
                exitAge++;

                if (exitAge >= SLIDE_OUT_TICKS) {
                    finished = true;
                }

                return;
            }

            age++;

            if (age >= DISPLAY_TICKS) {
                exiting = true;
                exitAge = 0;
            }
        }

        /**
         * Starts an immediate slide-out.
         *
         * Used when a fourth notification arrives.
         */
        private void startImmediateExit() {
            exiting = true;

            /*
             * Start close to the end of the exit animation
             * so the notification disappears quickly.
             */
            exitAge = SLIDE_OUT_TICKS - 2;
        }

        /**
         * Returns the animated horizontal position.
         */
        private float getAnimatedX(float targetX) {

            /*
             * Slide in from the right.
             */
            if (!exiting && age < SLIDE_IN_TICKS) {

                float progress =
                        (float) age
                                / (float) SLIDE_IN_TICKS;

                progress =
                        smoothStep(progress);

                return Mth.lerp(
                        progress,
                        targetX + SLIDE_DISTANCE,
                        targetX
                );
            }

            /*
             * Slide out to the right.
             */
            if (exiting) {

                float progress =
                        (float) exitAge
                                / (float) SLIDE_OUT_TICKS;

                progress =
                        smoothStep(progress);

                return Mth.lerp(
                        progress,
                        targetX,
                        targetX + SLIDE_DISTANCE
                );
            }

            return targetX;
        }

        private float getAnimatedY(float targetY) {

            if (!positionInitialized) {
                currentY = targetY;
                previousY = targetY;
                positionInitialized = true;
                return targetY;
            }

            previousY = currentY;

            /*
             * Smoothly move toward the new slot.
             */
            float difference = targetY - currentY;

            currentY += difference * 0.25F;

            /*
             * Prevent tiny floating point movement forever.
             */
            if (Math.abs(difference) < 0.05F) {
                currentY = targetY;
            }

            return currentY;
        }

        private static float smoothStep(float value) {
            value = Mth.clamp(value, 0.0F, 1.0F);

            return value * value
                    * (3.0F - 2.0F * value);
        }
    }
}