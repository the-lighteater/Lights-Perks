package dot.lighteater.lights_perks.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dot.lighteater.lights_perks.Config;
import dot.lighteater.lights_perks.UpgradeScrolls;
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

        UpgradeScrolls.LOGGER.debug(
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
        UpgradeScrolls.LOGGER.debug(
                "[AddSuspicousItemModifier] doApply called. Existing loot count: {}",
                generatedLoot.size()
        );

        // Check conditions
        for (LootItemCondition condition : this.conditions) {

            boolean passed = condition.test(context);

            UpgradeScrolls.LOGGER.debug(
                    "[AddSuspicousItemModifier] Condition {} -> {}",
                    condition.getClass().getSimpleName(),
                    passed
            );

            if (!passed) {
                UpgradeScrolls.LOGGER.debug(
                        "[AddSuspicousItemModifier] Condition failed. No pool item supplied."
                );

                return generatedLoot;
            }
        }

        float roll = context.getRandom().nextFloat();

        UpgradeScrolls.LOGGER.debug(
                "[AddSuspicousItemModifier] Chance roll: {} / required < {}",
                roll,
                chance
        );

        if (roll >= chance) {
            UpgradeScrolls.LOGGER.debug(
                    "[AddSuspicousItemModifier] Chance failed. Normal archaeology loot will remain."
            );

            return generatedLoot;
        }

        UpgradeScrolls.LOGGER.debug(
                "[AddSuspicousItemModifier] Chance succeeded. Selecting random pool item."
        );

        // Get random item from the pool
        ItemStack drop = DropPoolManager.getRandomDrop(
                context.getRandom(),
                perk_pool_id
        );

        if (drop.isEmpty()) {
            UpgradeScrolls.LOGGER.debug(
                    "[AddSuspicousItemModifier] Drop pool was empty. Normal archaeology loot will remain."
            );

            return generatedLoot;
        }

        UpgradeScrolls.LOGGER.debug(
                "[AddSuspicousItemModifier] Selected pool item: {} x{}",
                drop.getItem(),
                drop.getCount()
        );

        // Replace archaeology loot
        UpgradeScrolls.LOGGER.debug(
                "[AddSuspicousItemModifier] Clearing {} existing archaeology loot entries.",
                generatedLoot.size()
        );

        generatedLoot.clear();

        generatedLoot.add(drop);

        UpgradeScrolls.LOGGER.debug(
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