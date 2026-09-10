package dot.lighteater.lights_perks.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dot.lighteater.lights_perks.LightsPerks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddSuspicousItemModifier extends LootModifier {


    public static final Supplier<Codec<AddSuspicousItemModifier>> CODEC =
            Suppliers.memoize(() ->
                    RecordCodecBuilder.create(inst ->
                            codecStart(inst)
                                    .and(
                                            Codec.FLOAT
                                                    .fieldOf("chance")
                                                    .forGetter(m -> m.chance)
                                    )
                                    .and(
                                            Codec.STRING
                                                    .fieldOf("perk_pool_id")
                                                    .forGetter(m -> m.perk_pool_id)
                                    )
                                    .apply(inst, AddSuspicousItemModifier::new)
                    )
            );
    private final float chance;
    private final String perk_pool_id;

    public AddSuspicousItemModifier(LootItemCondition[] conditionsIn,
                                    float chance, String perk_pool_id) {
        super(conditionsIn);

        LightsPerks.LOGGER.debug(
                "[AddSuspicousItemModifier] Created with {} conditions",
                conditionsIn.length
        );

        this.chance = chance;
        this.perk_pool_id = perk_pool_id;
    }


    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext context
    ) {
        LightsPerks.LOGGER.debug(
                "[AddSuspicousItemModifier] doApply called. Existing loot count: {}",
                generatedLoot.size()
        );

        // Check conditions
        for (LootItemCondition condition : this.conditions) {

            boolean passed = condition.test(context);

            LightsPerks.LOGGER.debug(
                    "[AddSuspicousItemModifier] Condition {} -> {}",
                    condition.getClass().getSimpleName(),
                    passed
            );

            if (!passed) {
                LightsPerks.LOGGER.debug(
                        "[AddSuspicousItemModifier] Condition failed. No pool item supplied."
                );

                return generatedLoot;
            }
        }

        float roll = context.getRandom().nextFloat();

        LightsPerks.LOGGER.debug(
                "[AddSuspicousItemModifier] Chance roll: {} / required < {}",
                roll,
                chance
        );

        if (roll >= chance) {
            LightsPerks.LOGGER.debug(
                    "[AddSuspicousItemModifier] Chance failed. Normal archaeology loot will remain."
            );

            return generatedLoot;
        }

        LightsPerks.LOGGER.debug(
                "[AddSuspicousItemModifier] Chance succeeded. Selecting random pool item."
        );

        // Get random item from the pool
        ItemStack drop = DropPoolManager.getRandomDrop(
                context.getRandom(),
                perk_pool_id
        );

        if (drop.isEmpty()) {
            LightsPerks.LOGGER.debug(
                    "[AddSuspicousItemModifier] Drop pool was empty. Normal archaeology loot will remain."
            );

            return generatedLoot;
        }

        LightsPerks.LOGGER.debug(
                "[AddSuspicousItemModifier] Selected pool item: {} x{}",
                drop.getItem(),
                drop.getCount()
        );

        // Replace archaeology loot
        LightsPerks.LOGGER.debug(
                "[AddSuspicousItemModifier] Clearing {} existing archaeology loot entries.",
                generatedLoot.size()
        );

        generatedLoot.clear();

        generatedLoot.add(drop);

        LightsPerks.LOGGER.debug(
                "[AddSuspicousItemModifier] Replaced archaeology loot with {}. New loot count: {}",
                drop.getItem(),
                generatedLoot.size()
        );

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}