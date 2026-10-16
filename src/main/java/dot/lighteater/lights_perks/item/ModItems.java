package dot.lighteater.lights_perks.item;

import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.item.custom.PerkItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, LightsPerks.MODID);


    public static final RegistryObject<Item> ATTACK_UP_PERK = ITEMS.register("attack_up_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "attack_up_perk")));
    public static final RegistryObject<Item> FIRE_ASPECT_PERK = ITEMS.register("fire_aspect_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "fire_aspect_perk")));
    public static final RegistryObject<Item> FORTUNE_PERK = ITEMS.register("fortune_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "fortune_perk")));
    public static final RegistryObject<Item> HASTE_PERK = ITEMS.register("haste_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "haste_perk")));
    public static final RegistryObject<Item> HEAVY_HITTER_PERK = ITEMS.register("heavy_hitter_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "heavy_hitter_perk")));
    public static final RegistryObject<Item> IRON_SKIN_PERK = ITEMS.register("iron_skin_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "iron_skin_perk")));
    public static final RegistryObject<Item> LOOTING_PERK = ITEMS.register("looting_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "looting_perk")));
    public static final RegistryObject<Item> MENDING_PERK = ITEMS.register("mending_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "mending_perk")));
    public static final RegistryObject<Item> NIGHT_VISION_PERK = ITEMS.register("night_vision_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "night_vision_perk")));
    public static final RegistryObject<Item> REGENERATION_PERK = ITEMS.register("regeneration_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "regeneration_perk")));
    public static final RegistryObject<Item> RESISTANCE_PERK = ITEMS.register("resistance_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "resistance_perk")));
    public static final RegistryObject<Item> SPEED_BOOST_PERK = ITEMS.register("speed_boost_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "speed_boost_perk")));
    public static final RegistryObject<Item> STAR_POWER_PERK = ITEMS.register("star_power_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "star_power_perk")));
    public static final RegistryObject<Item> UNBREAKING_PERK = ITEMS.register("unbreaking_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "unbreaking_perk")));
    public static final RegistryObject<Item> MINING_SURGE_PERK = ITEMS.register("mining_surge_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "mining_surge_perk")));
    public static final RegistryObject<Item> SPRINT_ATTACK_PERK = ITEMS.register("sprint_attack_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "sprint_attack_perk")));
    public static final RegistryObject<Item> COALESCENCE_PERK = ITEMS.register("coalescence_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "coalescence_perk")));
    public static final RegistryObject<Item> PURIFIED_BLOOD_PERK = ITEMS.register("purified_blood_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "purified_blood_perk")));
    public static final RegistryObject<Item> WITHERED_FURY_PERK = ITEMS.register("withered_fury_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "withered_fury_perk")));
    public static final RegistryObject<Item> PEAK_PERFORMANCE_PERK = ITEMS.register("peak_performance_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "peak_performance_perk")));
    public static final RegistryObject<Item> AIRBORNE_PERK = ITEMS.register("airborne_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "airborne_perk")));
    public static final RegistryObject<Item> FLAWLESS_ARMOR_PERK = ITEMS.register("flawless_armor_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "flawless_armor_perk")));
    public static final RegistryObject<Item> FLEETFOOT_PERK = ITEMS.register("fleetfoot_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "fleetfoot_perk")));
    public static final RegistryObject<Item> HEROICS_PERK = ITEMS.register("heroics_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "heroics_perk")));
    public static final RegistryObject<Item> JUMP_MASTER_PERK = ITEMS.register("jump_master_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "jump_master_perk")));
    public static final RegistryObject<Item> MAXIMUM_MIGHT_PERK = ITEMS.register("maximum_might_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "maximum_might_perk")));
    public static final RegistryObject<Item> RESUSCITATE_PERK = ITEMS.register("resuscitate_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "resuscitate_perk")));
    public static final RegistryObject<Item> VOID_CLEANSED_PERK = ITEMS.register("void_cleansed_perk",
            () -> new PerkItem(new Item.Properties(), new ResourceLocation(LightsPerks.MODID, "void_cleansed_perk")));

    public static void register(IEventBus eventBus) {

        LoadItems.register(eventBus, ITEMS);
        ITEMS.register(eventBus);
    }

    public static List<RegistryObject<Item>> getPerkItems() {
        return LoadItems.getItems();
    }
}
