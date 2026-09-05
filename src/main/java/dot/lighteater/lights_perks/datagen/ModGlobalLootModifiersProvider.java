package dot.lighteater.lights_perks.datagen;

import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.loot.AddItemModifier;
import dot.lighteater.lights_perks.loot.AddSuspicousItemModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, UpgradeScrolls.MODID);
    }

    @Override
    protected void start() {
        add("perks_from_mineshafts", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/abandoned_mineshaft")).build()},
                1, 3, .25f, "lights_perks:perk_item_pool"));

        add("perks_from_suspicious_sand", new AddSuspicousItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/desert_pyramid")).build()},
                .25f, "lights_perks:perk_item_pool"));
    }
}