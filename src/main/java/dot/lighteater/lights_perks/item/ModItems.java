package dot.lighteater.lights_perks.item;

import dot.lighteater.lights_perks.LightsPerks;
import dot.lighteater.lights_perks.item.custom.PerkItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


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

    public static void register(IEventBus eventBus) {

        ITEMS.register(eventBus);

        LoadItems.load();

        for (ItemDefinition definition : LoadItems.getDefinitions().values()) {

            ITEMS.register(definition.id,
                    () -> new JsonItem(definition));
        }
    }
}
