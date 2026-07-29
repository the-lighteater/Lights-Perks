package dot.lighteater.upgrade_scrolls.datagen;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

// Item Model Generation
// Makes the item models with runData.
// Code credit goes to Kaupenjoe for the simpleItem method and in general this file.

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, UpgradeScrolls.MODID, existingFileHelper);
    }

    record WeaponModelTier(
            String name,
            float predicateValue,
            float rotX,
            float rotY,
            float rotZ,
            float transX,
            float transY,
            float transZ,
            float tp_transX,
            float tp_transY,
            float tp_transZ,
            float fp_scale,
            float tp_scale
    ) {}

    private static final List<WeaponModelTier> MACUAHUITL_TIERS = List.of(

            new WeaponModelTier(
                    "bright",
                    0.0f,
                    0, 90, 55,
                    0, 2, 2,
                    0,8,4,
                    1.3f, 2.0f
            ),

            new WeaponModelTier(
                    "shining",
                    1.0f,
                    0, 90, 55,
                    0, 2, 2,
                    0,8,4,
                    1.3f, 2.0f
            ),

            new WeaponModelTier(
                    "radiant",
                    2.0f,
                    0, 90, 55,
                    0, 2, 2,
                    0,8,4,
                    1.3f, 2.0f
            )
    );

    @Override
    protected void registerModels() {
        registerTieredWeapon(ModItems.ECLIPSED_MACUAHUITL, MACUAHUITL_TIERS);
        simpleItem(ModItems.TESTING_HELMET);
        simpleItem(ModItems.TESTING_CHESTPLATE);
        simpleItem(ModItems.TESTING_LEGGINGS);
        simpleItem(ModItems.TESTING_BOOTS);

    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(UpgradeScrolls.MODID, "item/" + item.getId().getPath()));
    }

    private void registerTieredWeapon(RegistryObject<Item> item,
                                      List<WeaponModelTier> tiers) {

        String baseName = item.getId().getPath();

        // 1. Generate sub-models WITH display transforms
        for (WeaponModelTier tier : tiers) {

            String modelName = baseName + "_" + tier.name();

            ItemModelBuilder model = withExistingParent(
                    modelName,
                    new ResourceLocation("item/handheld")
            );

            model.texture("layer0",
                    new ResourceLocation(UpgradeScrolls.MODID, "item/" + modelName));

            model.transforms()

                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .rotation(tier.rotX(), tier.rotY(), tier.rotZ())
                    .translation(tier.transX(), tier.transY(), tier.transZ())
                    .scale(tier.fp_scale())
                    .end()

                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                    .rotation(tier.rotX(), -tier.rotY(), -tier.rotZ())
                    .translation(tier.transX(), tier.transY(), tier.transZ())
                    .scale(tier.fp_scale())
                    .end()

                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                    .rotation(tier.rotX(), -tier.rotY(), -tier.rotZ())
                    .translation(tier.tp_transX(), tier.tp_transY(), tier.tp_transZ())
                    .scale(tier.tp_scale())
                    .end()

                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    .rotation(tier.rotX(), tier.rotY(), tier.rotZ())
                    .translation(tier.tp_transX(), tier.tp_transY(), tier.tp_transZ())
                    .scale(tier.tp_scale())
                    .end()

                    .transform(ItemDisplayContext.GROUND)
                    .rotation(tier.rotX(), tier.rotY(), tier.rotZ())
                    .translation(tier.tp_transX(), tier.tp_transY(), tier.tp_transZ())
                    .scale(tier.tp_scale())
                    .end()

                    .end();
        }

        // 2. Base model (with overrides)
        ItemModelBuilder base = withExistingParent(
                baseName,
                new ResourceLocation("item/handheld")
        );

        base.texture("layer0",
                new ResourceLocation(UpgradeScrolls.MODID, "item/" + baseName + "_bright"));

        for (WeaponModelTier tier : tiers) {
            base.override()
                    .predicate(new ResourceLocation(UpgradeScrolls.MODID, "tier"), tier.predicateValue())
                    .model(new ModelFile.UncheckedModelFile(
                            UpgradeScrolls.MODID + ":item/" + baseName + "_" + tier.name()
                    ))
                    .end();
        }
    }
}
