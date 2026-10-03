
package dot.lighteater.lights_perks.skill;

import dot.lighteater.lights_perks.ModNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class SkillActivationManager {

    private SkillActivationManager() {}

    private static final Map<UUID, Map<String, State>> STATES =
            new HashMap<>();

    private static final class State {
        boolean active;
        boolean previousCondition;
        long expiresAt;
        long cooldownUntil;
        long lastActivationTick = Long.MIN_VALUE;
    }

    private static String key(ResourceLocation id, int level) {
        return id + ":" + level;
    }

    private static State state(
            Player player,
            ResourceLocation id,
            int level
    ) {
        return STATES.computeIfAbsent(
                player.getUUID(),
                ignored -> new HashMap<>()
        ).computeIfAbsent(
                key(id, level),
                ignored -> new State()
        );
    }

    /**
     * Evaluates skill activation once per logical server tick.
     *
     * Returns true when at least one activation state changes.
     */
    public static boolean tick(ServerPlayer player) {
        boolean changed = false;
        long now = player.serverLevel().getGameTime();

        for (SkillData skill : SkillManager.getAllSkills()) {
            if (skill.levels == null) {
                continue;
            }

            ResourceLocation id =
                    ResourceLocation.parse(skill.skill_id);

            int playerLevel =
                    SkillManager.getPlayerSkillLevel(player, id);

            int accessibleMax = skill.maxLevel;

            if (SkillManager.hasBonusSkill(player, id)) {
                accessibleMax += skill.bonusLevel;
            }

            for (SkillLevelData level : skill.levels) {
                SkillActivationData config = level.activation;

                if (config == null) {
                    continue;
                }

                State state = state(player, id, level.level);

                // A level that is no longer available cannot remain active.
                if (level.level > playerLevel
                        || level.level > accessibleMax) {
                    changed |= transition(
                            player, id, skill, level, state, false
                    );
                    state.previousCondition = false;
                    continue;
                }

                String mode = normalize(config.mode);
                boolean condition = evaluateCondition(player, config);

                if ("conditional".equals(mode)) {
                    boolean shouldBeActive;

                    if (config.condition == null
                            || config.condition.isBlank()) {
                        shouldBeActive = true;
                    } else if ("rising_edge".equals(
                            normalize(config.conditionPolicy))) {
                        shouldBeActive =
                                condition && !state.previousCondition;
                    } else {
                        shouldBeActive = condition;
                    }

                    changed |= transition(
                            player, id, skill, level, state,
                            shouldBeActive
                    );

                } else if ("timed".equals(mode)
                        && config.condition != null
                        && !config.condition.isBlank()) {

                    boolean risingEdge =
                            condition && !state.previousCondition;

                    if (risingEdge) {
                        changed |= activate(
                                player, id, skill, level, state,
                                config, now
                        );
                    }
                }

                state.previousCondition = condition;

                // Timed activations deactivate when their duration expires.
                if (state.active
                        && state.expiresAt > 0
                        && now >= state.expiresAt) {
                    changed |= transition(
                            player, id, skill, level, state, false
                    );
                }
            }
        }

        return changed;
    }

    /**
     * Whether this skill level currently applies its effects.
     */
    public static boolean isActive(
            Player player,
            ResourceLocation id,
            SkillLevelData level
    ) {
        if (level.activation == null) {
            return true;
        }

        String mode = normalize(level.activation.mode);

        if ("passive".equals(mode)) {
            return true;
        }

        State state = state(player, id, level.level);

        // Use the tracked state so effects and notifications agree.
        return state.active;
    }

    /**
     * Activates timed skills configured for the given event trigger.
     * Call on the logical server only.
     */
    public static boolean trigger(
            ServerPlayer player,
            String trigger
    ) {
        boolean changed = false;
        long now = player.serverLevel().getGameTime();

        for (SkillData skill : SkillManager.getAllSkills()) {
            if (skill.levels == null) {
                continue;
            }

            ResourceLocation id =
                    ResourceLocation.parse(skill.skill_id);

            int playerLevel =
                    SkillManager.getPlayerSkillLevel(player, id);

            int accessibleMax = skill.maxLevel;

            if (SkillManager.hasBonusSkill(player, id)) {
                accessibleMax += skill.bonusLevel;
            }

            for (SkillLevelData level : skill.levels) {
                SkillActivationData config = level.activation;

                if (config == null
                        || !"timed".equals(normalize(config.mode))
                        || config.trigger == null
                        || !config.trigger.equalsIgnoreCase(trigger)) {
                    continue;
                }

                if (level.level > playerLevel
                        || level.level > accessibleMax) {
                    continue;
                }

                State state = state(player, id, level.level);

                changed |= activate(
                        player, id, skill, level, state,
                        config, now
                );
            }
        }

        return changed;
    }

    private static boolean activate(
            ServerPlayer player,
            ResourceLocation id,
            SkillData skill,
            SkillLevelData level,
            State state,
            SkillActivationData config,
            long now
    ) {
        if (now < state.cooldownUntil) {
            return false;
        }

        if (state.active && !config.retrigger) {
            return false;
        }

        if (config.duration <= 0) {
            return false;
        }

        boolean wasActive = state.active;

        state.active = true;
        state.expiresAt = now + config.duration;
        state.cooldownUntil =
                now + Math.max(0, config.cooldown);
        state.lastActivationTick = now;

        // Extending an already-active timer is not a new activation.
        if (!wasActive) {
            sendNotification(player, skill, level, true);
            return true;
        }

        return false;
    }

    private static boolean transition(
            ServerPlayer player,
            ResourceLocation id,
            SkillData skill,
            SkillLevelData level,
            State state,
            boolean active
    ) {
        if (state.active == active) {
            return false;
        }

        state.active = active;

        if (!active) {
            state.expiresAt = 0;
        }

        sendNotification(player, skill, level, active);
        return true;
    }

    private static void sendNotification(
            ServerPlayer player,
            SkillData skill,
            SkillLevelData level,
            boolean activated
    ) {
        SkillActivationData config = level.activation;

        if (config == null || !config.showNotification) {
            return;
        }

        String message = activated
                ? config.activationMessage
                : config.deactivationMessage;

        if (message == null || message.isBlank()) {
            message = skillName(skill)
                    + (activated ? " Activated" : " Deactivated");
        }

        ModNetwork.sendSkillActivation(player, message, activated);
    }

    private static String skillName(SkillData skill) {
        if (skill.skill_id == null || skill.skill_id.isBlank()) {
            return "Skill";
        }

        String name = skill.skill_id;
        int separator = name.lastIndexOf(':');

        if (separator >= 0) {
            name = name.substring(separator + 1);
        }

        name = name.replace('_', ' ').replace('-', ' ');

        if (name.isEmpty()) {
            return "Skill";
        }

        return Character.toUpperCase(name.charAt(0))
                + name.substring(1);
    }

    private static boolean evaluateCondition(
            Player player,
            SkillActivationData config
    ) {
        if (config.condition == null
                || config.condition.isBlank()) {
            return true;
        }

        return switch (normalize(config.condition)) {
            case "sprinting" -> player.isSprinting();
            case "sneaking" -> player.isCrouching();
            case "blocking" -> player.isBlocking();
            case "on_ground" -> player.onGround();
            case "underwater" -> player.isUnderWater();
            case "burning" -> player.isOnFire();
            case "moving" ->
                    player.getDeltaMovement().horizontalDistanceSqr()
                            > 0.0001;
            case "low_health" ->
                    player.getHealth()
                            <= player.getMaxHealth()
                            * config.threshold;
            case "full_health" ->
                    player.getHealth() >= player.getMaxHealth();
            default -> false;
        };
    }

    private static String normalize(String value) {
        return value == null
                ? "passive"
                : value.toLowerCase(Locale.ROOT);
    }

    public static void clearPlayer(UUID playerId) {
        STATES.remove(playerId);
    }
}