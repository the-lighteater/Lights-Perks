package dot.lighteater.lights_perks.loot;

import dot.lighteater.lights_perks.UpgradeScrolls;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DropPoolManager {

    public static final TagKey<Item> DROP_ITEMS =
            ItemTags.create(
                    new ResourceLocation(
                            UpgradeScrolls.MODID,
                            "perk_drop_pool"
                    )
            );

    public static final TagKey<EntityType<?>> DROP_ENTITIES =
            TagKey.create(
                    Registries.ENTITY_TYPE,
                    new ResourceLocation(
                            UpgradeScrolls.MODID,
                            "mob_drop_entities"
                    )
            );


    public static boolean canDrop(LivingEntity entity) {
        return entity.getType().is(DROP_ENTITIES);
    }

    public static ItemStack getRandomDrop(RandomSource random, String poolId) {

        TagKey<Item> dropItems = ItemTags.create(
                new ResourceLocation(
                        poolId
                )
        );

        List<Item> possibleItems = new ArrayList<>();

        for (Holder<Item> holder :
                BuiltInRegistries.ITEM.getOrCreateTag(dropItems)) {

            possibleItems.add(holder.value());
        }

        if (possibleItems.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Item selectedItem = possibleItems.get(
                random.nextInt(possibleItems.size())
        );


        return new ItemStack(selectedItem);
    }
}