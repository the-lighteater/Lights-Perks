package dot.lighteater.lights_perks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class JsonItem extends Item {

    private final ItemDefinition definition;

    public JsonItem(ItemDefinition definition) {
        super(new Item.Properties().stacksTo(definition.maxStackSize));

        this.definition = definition;
    }

    public ItemDefinition getDefinition() {
        return definition;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(definition.displayName);
    }
}