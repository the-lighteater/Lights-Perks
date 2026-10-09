package dot.lighteater.lights_perks.skill;

import dot.lighteater.lights_perks.ModNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.system.Checks;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/*
   This class is used to enable skills with conditionals or event triggers.
   Also sends notifications through the network.
 */

public final class SkillActivationManager {

    private SkillActivationManager() {}

    private static final Map<UUID, Map<String, State>> STATES =
            new HashMap<>();

    // State is per-skill.
    private static final class State {
        boolean active;
        boolean previousCondition;
        long expiresAt;
        long cooldownUntil;
        long lastActivationTick = Long.MIN_VALUE;

        boolean hadHarmfulEffects;
        Map<String, Boolean> previousEffects = new HashMap<>();
    }

    private static String key(ResourceLocation id, int level) {
        return id + ":" + level;
    }

    //Retrieves skill state
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


    public static boolean tick(ServerPlayer player) {
        boolean changed = false;
        long now = player.serverLevel().getGameTime();

        changed |= processEffectEvents(player);

        for (SkillData skill : SkillManager.getAllSkills()) {
            if (skill.levels == null) continue;

            ResourceLocation id = ResourceLocation.parse(skill.skill_id);
            int playerLevel = SkillManager.getPlayerSkillLevel(player, id);
            int accessibleMax = skill.maxLevel;

            if (SkillManager.hasBonusSkill(player, id)) accessibleMax += skill.bonusLevel;

            for (SkillLevelData level : skill.levels) {
                SkillActivationData config = level.activation;

                if (config == null) continue;

                State state = state(player, id, level.level);

                // A level that is no longer available cannot remain active.
                if (level.level != playerLevel || level.level > accessibleMax) {
                    changed |= transition(player, id, skill, level, state, false);
                    state.previousCondition = false;
                    continue;
                }

                String mode = normalize(config.mode);
                boolean condition = evaluateCondition(player, config);

                if ("conditional".equals(mode)) {
                    boolean shouldBeActive = isShouldBeActive(config, condition, state);
                    changed |= transition(player, id, skill, level, state, shouldBeActive);

                } else if ("timed".equals(mode)
                        && config.condition != null
                        && !config.condition.isBlank()) {

                    boolean risingEdge = condition && !state.previousCondition;

                    if (risingEdge) changed |= activate(
                                player, id, skill, level, state, config, now);

                }

                state.previousCondition = condition;

                if (state.active
                        && state.expiresAt > 0
                        && now >= state.expiresAt) {
                    changed |= transition(player, id, skill, level, state, false);
                }
            }
        }

        return changed;
    }

    private static boolean isShouldBeActive(SkillActivationData config, boolean condition, State state) {
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
        return shouldBeActive;
    }

    public static boolean isActive(
            Player player,
            ResourceLocation id,
            SkillLevelData level
    ) {
        if (level.activation == null) return true;
        String mode = normalize(level.activation.mode);

        if ("passive".equals(mode)) return true;
        State state = state(player, id, level.level);

        return state.active;
    }

    public static boolean trigger(
            ServerPlayer player,
            String trigger
    ) {
        boolean changed = false;
        long now = player.serverLevel().getGameTime();

        for (SkillData skill : SkillManager.getAllSkills()) {
            if (skill.levels == null) continue;

            ResourceLocation id = ResourceLocation.parse(skill.skill_id);
            int playerLevel = SkillManager.getPlayerSkillLevel(player, id);
            int accessibleMax = skill.maxLevel;

            if (SkillManager.hasBonusSkill(player, id)) accessibleMax += skill.bonusLevel;

            for (SkillLevelData level : skill.levels) {
                SkillActivationData config = level.activation;

                if (config == null
                        || !"timed".equals(normalize(config.mode))
                        || config.trigger == null
                        || !config.trigger.equalsIgnoreCase(trigger)) continue;

                if (level.level > playerLevel
                        || level.level > accessibleMax) continue;

                State state = state(player, id, level.level);

                changed |= activate(
                        player, id, skill, level, state, config, now);
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
        if (now < state.cooldownUntil) return false;
        if (state.active && !config.retrigger) return false;
        if (config.duration <= 0) return false;

        boolean wasActive = state.active;

        state.active = true;
        state.expiresAt = now + config.duration;
        state.cooldownUntil = now + Math.max(0, config.cooldown);
        state.lastActivationTick = now;

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
        if (state.active == active) return false;

        state.active = active;

        if (!active) state.expiresAt = 0;

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

        if (config == null || !config.showNotification) return;

        String message = activated
                ? config.activationMessage
                : config.deactivationMessage;

        if (message == null || message.isBlank()) message = skillName(skill)
                    + (activated ? " Activated" : " Deactivated");

        ModNetwork.sendSkillActivation(player, message, activated);
    }

    private static String skillName(SkillData skill) {
        if (skill.skill_id == null || skill.skill_id.isBlank()) return "Skill";

        String name = skill.skill_id;
        int separator = name.lastIndexOf(':');

        if (separator >= 0) name = name.substring(separator + 1);

        name = name.replace('_', ' ').replace('-', ' ');

        if (name.isEmpty()) return "Skill";

        return Character.toUpperCase(name.charAt(0))
                + name.substring(1);
    }

    private static boolean evaluateCondition(
            Player player,
            SkillActivationData config
    ) {
        if (config.condition == null
                || config.condition.isBlank()) return true;

        String condition = normalize(config.condition);

        switch (condition) {
            case "sprinting": return player.isSprinting();
            case "sneaking": return player.isCrouching();
            case "blocking": return player.isBlocking();
            case "on_ground": return player.onGround();
            case "airborne": return !player.onGround();
            case "underwater": return player.isUnderWater();
            case "burning": return player.isOnFire();
            case "moving": return player.getDeltaMovement().horizontalDistanceSqr() > 0.0001;
            case "low_health": return player.getHealth() <= player.getMaxHealth() * config.threshold;
            case "high_health": return player.getHealth() >= player.getMaxHealth() * config.threshold;
            case "full_health": return player.getHealth() >= player.getMaxHealth();
            case "full_stamina": return player.getFoodData().getFoodLevel() >= 20;
            case "empty_stamina": return player.getFoodData().getFoodLevel() == 0;
            case "high_stamina": return player.getFoodData().getFoodLevel() >= config.threshold;
            case "low_stamina": return player.getFoodData().getFoodLevel() <= config.threshold;
            default: break;
        }

        if (condition.startsWith("effect:")) {
            String effectId = condition.substring("effect:".length());
            return hasEffect(player, effectId);
        }

        return false;
    }

    private static boolean hasEffect(
            Player player,
            String effectId
    ) {
        ResourceLocation id;

        try {
            id = ResourceLocation.parse(effectId);
        } catch (Exception ignored) {
            return false;
        }

        var effect = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                .get(id);

        if (effect == null) return false;
        return player.hasEffect(effect);
    }

    // Processes current effects for the player, and runs effect-conditional skills off of it.
    private static boolean processEffectEvents(ServerPlayer player) {
        boolean changed = false;
        Map<String, Boolean> currentEffects = new HashMap<>();

        for (var effect : player.getActiveEffects()) {
            ResourceLocation id =
                    net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                            .getKey(effect.getEffect());

            if (id != null) {
                currentEffects.put(id.toString(), true);
            }
        }

        boolean hasHarmfulEffects = hasHarmfulEffect(player);

        Map<String, State> playerStates =
                STATES.computeIfAbsent(
                        player.getUUID(),
                        ignored -> new HashMap<>()
                );

        State eventState = playerStates.computeIfAbsent(
                "__effect_events__",
                ignored -> new State()
        );

        // If a state is based on harmful effects and no harmful effects found.
        if (eventState.hadHarmfulEffects
                && !hasHarmfulEffects) {

            changed |= trigger(
                    player,
                    "on_cured_all"
            );
        }

        // If a state is based on harmful effects and any harmful effects found.
        if (hasHarmfulEffects) {

            changed = trigger(
                    player,
                    "on_afflicted_all"
            );
        }

        // Checks to see if effect wasn't active before.
        for (String effectId : currentEffects.keySet()) {
            boolean previouslyActive =
                    eventState.previousEffects.getOrDefault(
                            effectId,
                            false
                    );

            if (!previouslyActive) {
                changed |= trigger(
                        player,
                        "on_begin:" + effectId
                );
            }
        }

        // Checks to see if effect is gone now.
        for (String previousEffect :
                eventState.previousEffects.keySet()) {

            boolean currentlyActive =
                    currentEffects.getOrDefault(
                            previousEffect,
                            false
                    );

            if (!currentlyActive) {
                changed |= trigger(
                        player,
                        "on_ended:" + previousEffect
                );
            }
        }

        eventState.hadHarmfulEffects = hasHarmfulEffects;
        eventState.previousEffects = currentEffects;

        return changed;
    }

    private static boolean hasHarmfulEffect(Player player) {
        for (var effectInstance : player.getActiveEffects()) {
            if (!effectInstance.getEffect().isBeneficial()) {
                return true;
            }
        }

        return false;
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