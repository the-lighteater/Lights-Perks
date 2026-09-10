package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.ServerConfig;
import dot.lighteater.lights_perks.loot.DropPoolManager;
import dot.lighteater.lights_perks.perk.IPerkItem;
import dot.lighteater.lights_perks.item_config.ItemConfigData;
import dot.lighteater.lights_perks.item_config.ItemConfigManager;
import dot.lighteater.lights_perks.skill.SkillData;
import dot.lighteater.lights_perks.skill.SkillManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

// Events handled by the mod.

@Mod.EventBusSubscriber(modid = LightsPerks.MODID)
public class ModEvents {

    private static final Map<String, String> mobPools = Map.of(
            "minecraft:enderman", "lights_perks:rare_pool",
            "minecraft:zombie", "lights_perks:common_pool",
            "minecraft:skeleton", "lights_perks:common_pool",
            "minecraft:warden", "lights_perks:rare_pool"
    );

    private static final Map<String, Double> mobChances = Map.of(
            "minecraft:enderman", 0.05,
            "minecraft:zombie", 0.10,
            "minecraft:skeleton", 0.10,
            "minecraft:warden", 1.0
    );

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {

        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) {
            return;
        }

        /*
         * Get the entity ID.
         */
        String entityId = entity.getEncodeId();

        /*
         * Get the item pool for this mob.
         */
        String poolId = mobPools.get(entityId);

        /*
         * This mob isn't configured to drop anything.
         */
        if (poolId == null) {
            return;
        }

        /*
         * Get the configured chance.
         */
        double chance =
                mobChances.getOrDefault(entityId, 0.0);

        /*
         * Roll drop chance.
         */
        float roll =
                entity.getRandom().nextFloat();

        if (roll >= chance) {
            return;
        }

        /*
         * Get random item from the configured pool.
         */
        ItemStack drop =
                DropPoolManager.getRandomDrop(
                        entity.getRandom(),
                        poolId
                );

        /*
         * Nothing in the pool.
         */
        if (drop.isEmpty()) {
            return;
        }

        /*
         * Add the drop.
         */
        event.getDrops().add(
                new ItemEntity(
                        entity.level(),
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        drop
                )
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        if (event.player.tickCount % ServerConfig.SYNC_PER_TICK_MANAGER.get() != 0) {
            return;
        }

        for (ItemStack stack : player.getInventory().items) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        if (event.player.level().isClientSide) {
            return;
        }

        SkillManager.updatePlayerSkills(event.player);
    }

    public static void initializeSockets(ItemStack item) {

        if (item.isEmpty()) {
            return;
        }

        if (!isPerkable(item)) {
            return;
        }

        boolean dataLoaded = false;

        CompoundTag tag = item.getOrCreateTag();

        if (!tag.contains("lights_perks:sockets")) {

            ListTag sockets = new ListTag();

            int slotSize = 1;

            LightsPerks.LOGGER.debug(
                    "[ModEvents] Initializing sockets for item: {} | ID: {}",
                    item,
                    BuiltInRegistries.ITEM.getKey(item.getItem())
            );

            ItemConfigData slotData = null;

            try {

                LightsPerks.LOGGER.debug(
                        "[ModEvents] Attempting to load ItemConfigData for: {}",
                        BuiltInRegistries.ITEM.getKey(item.getItem())
                );

                slotData = ItemConfigManager.get(item);

                if (slotData == null) {

                    LightsPerks.LOGGER.debug(
                            "[ModEvents] ItemConfigData is NULL for: {}",
                            BuiltInRegistries.ITEM.getKey(item.getItem())
                    );

                } else {

                    LightsPerks.LOGGER.debug(
                            "[ModEvents] ItemConfigData loaded for: {} | slots={}",
                            BuiltInRegistries.ITEM.getKey(item.getItem()),
                            slotData.slots
                    );

                    if (slotData.slots == null) {

                        LightsPerks.LOGGER.debug(
                                "[ModEvents] ItemConfigData.slots is NULL for: {}",
                                BuiltInRegistries.ITEM.getKey(item.getItem())
                        );

                    } else {

                        LightsPerks.LOGGER.debug(
                                "[ModEvents] ItemConfigData.slots size={} for: {}",
                                slotData.slots.size(),
                                BuiltInRegistries.ITEM.getKey(item.getItem())
                        );

                        slotSize = slotData.slots.size();
                        dataLoaded = true;
                    }
                }

            } catch (Exception e) {

                LightsPerks.LOGGER.error(
                        "[ModEvents] FAILED to load ItemConfigData for: {}",
                        BuiltInRegistries.ITEM.getKey(item.getItem()),
                        e
                );
            }

            for (int i = 0; i < slotSize; i++) {

                int socketLevel = 1;

                if (dataLoaded) {

                    socketLevel = slotData.slots.get(i);

                    LightsPerks.LOGGER.debug(
                            "[ModEvents] Loaded {} with socket #{} that has level {}",
                            item,
                            i,
                            socketLevel
                    );

                } else {

                    LightsPerks.LOGGER.debug(
                            "[ModEvents] Using default socket #{} level {} for {}",
                            i,
                            socketLevel,
                            BuiltInRegistries.ITEM.getKey(item.getItem())
                    );
                }

                CompoundTag socket = new CompoundTag();

                socket.putInt(
                        "Slot",
                        i
                );

                socket.putInt(
                        "Level",
                        socketLevel
                );

                // empty itemstack
                socket.put("Item", new CompoundTag());

                sockets.add(socket);
            }

            tag.put("lights_perks:sockets", sockets);
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();

        ItemConfigData data = ItemConfigManager.get(stack);

        /*
         * Built-in skills
         */
        if (data != null) {
            if (data.builtin_skills != null) {
                if (!data.builtin_skills.isEmpty()) {

                    event.getToolTip().add(
                            Component.literal("Built-In Skills")
                    );

                    for (Map.Entry<String, Integer> entry :
                            data.builtin_skills.entrySet()) {

                        SkillData skillData =
                                SkillManager.get(
                                        new ResourceLocation(entry.getKey())
                                );

                        if (skillData == null) {
                            continue;
                        }

                        int color = (int) Long.parseLong(
                                skillData.color.replace("0x", ""),
                                16
                        );

                        event.getToolTip().add(
                                Component.literal(
                                        skillData.title +
                                                ": Level " +
                                                entry.getValue()
                                ).withStyle(
                                        style -> style.withColor(color)
                                )
                        );
                    }
                }
            }
        }

        /*
         * Only show perk information for perkable equipment.
         */
        if (!isPerkable(stack)) {
            return;
        }

        CompoundTag tag = stack.getTag();

        if (tag == null ||
                !tag.contains(
                        "lights_perks:sockets",
                        Tag.TAG_LIST
                )) {
            return;
        }

        ListTag sockets = tag.getList(
                "lights_perks:sockets",
                Tag.TAG_COMPOUND
        );

        /*
         * Determine whether at least one socket
         * actually contains a perk.
         */
        boolean hasPerks = false;

        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket =
                    sockets.getCompound(i);

            if (!socket.contains(
                    "Item",
                    Tag.TAG_COMPOUND
            )) {
                continue;
            }

            ItemStack perkStack =
                    ItemStack.of(
                            socket.getCompound("Item")
                    );

            if (!perkStack.isEmpty() &&
                    perkStack.getItem() instanceof IPerkItem) {

                hasPerks = true;
                break;
            }
        }

        /*
         * Don't add an empty "Perk Slots" section.
         */
        if (!hasPerks) {
            //return;
        }

        event.getToolTip().add(
                Component.literal("")
        );

        event.getToolTip().add(
                Component.literal("Perk Slots")
        );

        /*
         * Display each socket that actually
         * contains a perk.
         */
        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket =
                    sockets.getCompound(i);

            /*
             * Get the maximum level of this socket.
             */
            int maxLevel =
                    socket.getInt("Level");

            /*
             * Get the perk stored in this socket.
             */
            if (!socket.contains(
                    "Item",
                    Tag.TAG_COMPOUND
            )) {
                continue;
            }

            ItemStack perkStack =
                    ItemStack.of(
                            socket.getCompound("Item")
                    );

            /*
             * The Lv. value is the socket's
             * maximum level, NOT the perk's level.
             */
            MutableComponent levelText =
                    Component.literal(
                            "◇ Lv." + maxLevel + " "
                    );

            if (perkStack.isEmpty()) {
                event.getToolTip().add(
                        levelText);
                continue;
            }

            if (!(perkStack.getItem()
                    instanceof IPerkItem perkItem)) {
                event.getToolTip().add(
                        levelText);
                continue;
            }


            /*
             * The perk item's name uses
             * the color provided by the perk item.
             */
            Component perkName =
                    perkStack.getHoverName()
                            .copy()
                            .withStyle(
                                    style -> style.withColor(
                                            perkItem.getColor()
                                    )
                            );

            event.getToolTip().add(
                    levelText.append(perkName)
            );
        }
    }

    private static CompoundTag createSlot(int level, String id, String translatable) {
        CompoundTag slot = new CompoundTag();

        slot.putInt("Level", level);
        slot.putString("Perk", id);
        slot.putString("Tooltip", translatable);

        return slot;
    }
    public static boolean isPerkable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof ShieldItem;
    }
}
