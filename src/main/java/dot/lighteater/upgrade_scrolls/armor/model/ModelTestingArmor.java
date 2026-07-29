package dot.lighteater.upgrade_scrolls.armor.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ModelTestingArmor extends ModelArmor{

    private static final ModelPart INNER_MODEL;
    private static final ModelPart OUTER_MODEL;

    public ModelTestingArmor(boolean inner){super(getBakedModel(inner));}

    public static MeshDefinition createMesh(CubeDeformation deformation, float offset) {
        MeshDefinition meshDefinition = HumanoidModel.createMesh(deformation, offset);
        PartDefinition partDefinition = meshDefinition.getRoot();

        // partDefinition.getChild("left_arm").addOrReplaceChild("left_arm_armor", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        // /partDefinition.getChild("right_arm").addOrReplaceChild("right_arm_armor", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
//        partDefinition.getChild("body").addOrReplaceChild("body_armor", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
//        partDefinition.getChild("head").addOrReplaceChild("head_armor", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
//                .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        // partDefinition.getChild("right_leg").addOrReplaceChild("right_leg_armor", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        // partDefinition.getChild("left_leg").addOrReplaceChild("left_leg_armor", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_1", CubeListBuilder.create().texOffs(0, 32).mirror().addBox(-2.0F, -7.0F, -12.8822F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(21, 44).mirror().addBox(-1.0F, -3.8822F, -12.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(28, 29).addBox(0.0F, -11.0F, 0.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(28, 29).addBox(0.0F, -11.0F, -4.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        partDefinition.getChild("hat").addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 29).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 4.0F, -1.5708F, 0.0F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(28, 29).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 4.0F, -1.5708F, 0.0F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 32).mirror().addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -7.0F, 1.0F, -0.6222F, 0.1782F, -0.1265F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(12, 32).mirror().addBox(-1.7472F, -5.3409F, -0.7124F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)).mirror(false), PartPose.offsetAndRotation(-2.5028F, -10.6591F, 2.7124F, -1.0695F, 0.3858F, -0.4745F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(12, 32).addBox(-0.2528F, -5.3409F, -0.7124F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(2.5028F, -10.6591F, 2.7124F, -1.0695F, -0.3858F, 0.4745F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(20, 32).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -7.0F, 1.0F, -0.6222F, -0.1782F, 0.1265F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(37, 31).mirror().addBox(-1.0F, -2.0F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.4142F, 0.0F, 1.4142F, 0.0F, -0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(37, 31).mirror().addBox(-1.0F, -2.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 1.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(37, 31).mirror().addBox(-1.0F, -2.0F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.4142F, 0.0F, 5.4142F, 0.0F, -0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(37, 31).mirror().addBox(-1.0F, -2.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 1.0F, 4.0F, 0.0F, -0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(37, 31).mirror().addBox(-1.0F, -2.0F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.4142F, 0.0F, -0.5858F, 0.0F, -0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(37, 31).mirror().addBox(-1.0F, -2.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 1.0F, -2.0F, 0.0F, -0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(37, 31).mirror().addBox(-1.0F, -2.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 1.0F, 2.0F, 0.0F, -0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(37, 31).mirror().addBox(-1.0F, -2.0F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.4142F, 0.0F, 3.4142F, 0.0F, -0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(37, 31).addBox(1.0F, -2.0F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4142F, 0.0F, 1.4142F, 0.0F, 0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(37, 31).addBox(1.0F, -2.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 1.0F, 4.0F, 0.0F, 0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(37, 31).addBox(1.0F, -2.0F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4142F, 0.0F, 5.4142F, 0.0F, 0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(37, 31).addBox(1.0F, -2.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 1.0F, -2.0F, 0.0F, 0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(37, 31).addBox(1.0F, -2.0F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4142F, 0.0F, -0.5858F, 0.0F, 0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(37, 31).addBox(1.0F, -2.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 1.0F, 2.0F, 0.0F, 0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(37, 31).addBox(1.0F, -2.0F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4142F, 0.0F, 3.4142F, 0.0F, 0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(37, 31).addBox(1.0F, -2.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 1.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(38, 32).addBox(-0.7247F, -4.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7753F, -1.0F, -4.7071F, 0.0F, -0.2618F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(28, 34).addBox(-0.5247F, -3.0F, -2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7753F, -3.0F, -4.7071F, 0.0F, -0.3927F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(0, 41).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -9.0F, 0.4363F, 0.0F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(0, 62).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9387F, -0.7679F, -4.483F, 0.1332F, -0.2261F, -0.5387F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(0, 62).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9387F, -0.7679F, -4.483F, 0.1332F, 0.2261F, 0.5387F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(28, 34).mirror().addBox(-1.4753F, -3.0F, -2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.7753F, -3.0F, -4.7071F, 0.0F, 0.3927F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(38, 32).mirror().addBox(-1.2753F, -4.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.7753F, -1.0F, -4.7071F, 0.0F, 0.2618F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(0, 52).mirror().addBox(-1.0F, -3.0F, -8.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.7753F, 0.0F, -4.7071F, 0.0F, 0.2618F, 0.0F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(33, 39).mirror().addBox(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.9387F, -1.7679F, -4.483F, 0.1332F, 0.2261F, 0.5387F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(33, 39).mirror().addBox(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.9387F, -1.7679F, -4.483F, 0.1332F, -0.2261F, -0.5387F));
        partDefinition.getChild("hat").addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(0, 52).addBox(-1.0F, -3.0F, -8.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7753F, 0.0F, -4.7071F, 0.0F, -0.2618F, 0.0F));

        return meshDefinition;
    }

    public static ModelPart getBakedModel(boolean inner) {
        return inner ? INNER_MODEL : OUTER_MODEL;
    }

    static {
        INNER_MODEL = createMesh(CubeDeformation.NONE.extend(INNER_MODEL_OFFSET), 0.0F).getRoot().bake(128, 128);
        OUTER_MODEL = createMesh(CubeDeformation.NONE.extend(OUTER_MODEL_OFFSET), 0.0F).getRoot().bake(128, 128);
    }
}
