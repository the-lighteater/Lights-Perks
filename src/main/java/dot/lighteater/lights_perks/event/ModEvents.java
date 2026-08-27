package dot.lighteater.lights_perks.event;

import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.perk.IPerkItem;
import dot.lighteater.lights_perks.item_config.ItemConfigData;
import dot.lighteater.lights_perks.item_config.ItemConfigManager;
import dot.lighteater.lights_perks.skill.SkillData;
import dot.lighteater.lights_perks.skill.SkillManager;
import dot.lighteater.lights_perks.skill.bonus_skills.BonusSkillChecker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

// Events handled by the mod.

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID)
public class ModEvents {

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

        BonusSkillChecker.update(event.player);
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

        if (data != null) {
            event.getToolTip().add(
                    Component.literal("Built-In Skills")
            );
            for (Map.Entry<String, Integer> entry : data.builtin_skills.entrySet()) {
                SkillData skillData = SkillManager.get(new ResourceLocation(entry.getKey()));

                event.getToolTip().add(
                        Component.literal(skillData.title + ": Level " + entry.getValue())
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
                !tag.contains("lights_perks:sockets", Tag.TAG_LIST)) {
            return;
        }

        ListTag sockets = tag.getList(
                "lights_perks:sockets",
                Tag.TAG_COMPOUND
        );

        boolean hasPerks = false;

        /*
         * First determine whether there are
         * actually any perk items.
         */
        for (int i = 0; i < sockets.size(); i++) {

            CompoundTag socket = sockets.getCompound(i);

            if (!socket.contains(
                    "Item",
                    Tag.TAG_COMPOUND
            )) {
                continue;
            }

            ItemStack perkStack = ItemStack.of(
                    socket.getCompound("Item")
            );

            if (!perkStack.isEmpty()
                    && perkStack.getItem() instanceof IPerkItem) {

                hasPerks = true;
                break;
            }
        }

        /*
         * Don't add an empty "Perk Slots" section.
         */
        if (!hasPerks) {
            return;
        }

        event.getToolTip().add(
                Component.literal("")
        );

        event.getToolTip().add(
                Component.literal("Perk Slots")
        );

        /*
         * Render each perk.
         */
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

            if (perkStack.isEmpty()) {
                continue;
            }

            if (!(perkStack.getItem()
                    instanceof IPerkItem perkItem)) {
                continue;
            }

            /*
             * Get the perk's skill information.
             */
            int level =
                    perkItem.getLevel();

            event.getToolTip().add(
                    Component.literal(
                            "◇ Lv." + level + " "
                    ).append(perkStack.getHoverName())
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
