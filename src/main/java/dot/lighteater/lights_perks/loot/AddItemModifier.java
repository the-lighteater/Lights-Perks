package dot.lighteater.lights_perks.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dot.lighteater.lights_perks.Config;
import dot.lighteater.lights_perks.UpgradeScrolls;
import dot.lighteater.lights_perks.event.DropPoolManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddItemModifier extends LootModifier {

    public static final Supplier<Codec<AddItemModifier>> CODEC =
            Suppliers.memoize(() ->
                    RecordCodecBuilder.create(inst ->
                            codecStart(inst)
                                    .and(
                                            Codec.INT
                                                    .fieldOf("min_count")
                                                    .forGetter(m -> m.minCount)
                                    )
                                    .and(
                                            Codec.INT
                                                    .fieldOf("max_count")
                                                    .forGetter(m -> m.maxCount)
                                    )
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
                                    .apply(inst, AddItemModifier::new)
                    )
            );
    private final int minCount;
    private final int maxCount;
    private final float chance;
    private final String perk_pool_id;

    public AddItemModifier(LootItemCondition[] conditionsIn,
                                    int minCount, int maxCount, float chance, String perk_pool_id) {
        super(conditionsIn);

        UpgradeScrolls.LOGGER.debug(
                "[AddSuspicousItemModifier] Created with {} conditions",
                conditionsIn.length
        );

        this.minCount = minCount;
        this.maxCount = maxCount;
        this.chance = chance;
        this.perk_pool_id = perk_pool_id;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext context
    ) {
        UpgradeScrolls.LOGGER.debug(
                "[AddItemModifier] doApply called. Existing loot count: {}",
                generatedLoot.size()
        );

        // Check conditions
        for (LootItemCondition condition : this.conditions) {

            boolean passed = condition.test(context);

            UpgradeScrolls.LOGGER.debug(
                    "[AddItemModifier] Condition {} -> {}",
                    condition.getClass().getSimpleName(),
                    passed
            );

            if (!passed) {
                UpgradeScrolls.LOGGER.debug(
                        "[AddItemModifier] Condition failed. No pool item added."
                );

                return generatedLoot;
            }
        }

        // Drop chance
        double chance = Config.LOOT_TABLE_DROP_CHANCE.get();
        float roll = context.getRandom().nextFloat();

        UpgradeScrolls.LOGGER.debug(
                "[AddItemModifier] Chance roll: {} / required < {}",
                roll,
                chance
        );

        if (roll >= chance) {
            UpgradeScrolls.LOGGER.debug(
                    "[AddItemModifier] Chance failed. No pool item added."
            );

            return generatedLoot;
        }

        UpgradeScrolls.LOGGER.debug(
                "[AddItemModifier] Chance succeeded. Selecting random pool item."
        );

        // Get random item from the pool
        ItemStack drop = DropPoolManager.getRandomDrop(
                context.getRandom()
        );

        if (drop.isEmpty()) {
            UpgradeScrolls.LOGGER.debug(
                    "[AddItemModifier] Drop pool was empty. No item added."
            );

            return generatedLoot;
        }

        UpgradeScrolls.LOGGER.debug(
                "[AddItemModifier] Selected pool item: {} x{}",
                drop.getItem(),
                drop.getCount()
        );

        // Add to existing loot
        generatedLoot.add(drop);

        UpgradeScrolls.LOGGER.debug(
                "[AddItemModifier] Added {} to loot. New loot count: {}",
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