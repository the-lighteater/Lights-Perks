package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.Config;
import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.loot.DropPoolManager;
import dot.lighteater.lights_perks.perk.IPerkItem;
import dot.lighteater.lights_perks.item_config.ItemConfigData;
import dot.lighteater.lights_perks.item_config.ItemConfigManager;
import dot.lighteater.lights_perks.skill.SkillData;
import dot.lighteater.lights_perks.skill.SkillManager;
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

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {

        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) {
            return;
        }

        if (!DropPoolManager.canDrop(entity)) {
            return;
        }

        // Drop chance
        float roll = entity.getRandom().nextFloat();
        double chance = Config.MOB_DROP_CHANCE.get();

        if (roll >= chance) {
            return;
        }

        // Get random item from the pool
        ItemStack drop = DropPoolManager.getRandomDrop(
                entity.getRandom(),
                "lights_perks:perk_drop_pool"
        );

        // Nothing in the pool
        if (drop.isEmpty()) {
            return;
        }


        // Add it to the drops
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

        for (ItemStack stack : player.getInventory().items) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (isPerkable(stack)) initializeSockets(stack);
        }

        applyPerks(player);

        if (event.player.level().isClientSide) {
            return;
        }

        SkillManager.updatePlayerSkills(event.player);
    }

    private static void applyPerks(Player player) {

        applyStack(player, player.getMainHandItem());
        applyStack(player, player.getOffhandItem());

        for (ItemStack armor : player.getInventory().armor) {
            applyStack(player, armor);
        }
    }

    private static void applyStack(Player player, ItemStack stack) {

        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("lights_perks:sockets"))
            return;

        ListTag sockets = tag.getList("lights_perks:sockets", Tag.TAG_COMPOUND);

        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket = sockets.getCompound(i);

            ItemStack socketItem = ItemStack.of(socket.getCompound("Item"));

            if (socketItem.isEmpty()) continue;

            if (socketItem.getItem() instanceof IPerkItem perkItem) {
            }
        }
    }

    public static void initializeSockets(ItemStack item) {

        if (item.isEmpty()) return;
        if (!isPerkable(item)) return;

        boolean dataLoaded = false;

        CompoundTag tag = item.getOrCreateTag();

        if (!tag.contains("lights_perks:sockets")) {

            ListTag sockets = new ListTag();

            int slotSize = 2;

            ItemConfigData slotData = ItemConfigManager.get(item);
            if (slotData != null && slotData.slots != null) {
                slotSize = slotData.slots.size();
                dataLoaded = true;
            }

            for (int i = 0; i < slotSize; i++) {

                int socketLevel = 1;

                if (dataLoaded) {
                    socketLevel = slotData.slots.get(i);
                    UpgradeScrolls.LOGGER.debug("[ModEvents] Loaded {} with socket #{} that has level {}",
                            item,
                            i,
                            socketLevel);
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
