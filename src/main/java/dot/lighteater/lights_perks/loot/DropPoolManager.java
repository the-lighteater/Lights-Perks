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

    public static boolean canDrop(
            LivingEntity entity,
            String entityTagId
    ) {
        TagKey<EntityType<?>> tag = TagKey.create(
                Registries.ENTITY_TYPE,
                new ResourceLocation(entityTagId)
        );

        return entity.getType().is(tag);
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