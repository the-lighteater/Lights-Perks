package dot.lighteater.upgrade_scrolls.armor.model;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.jetbrains.annotations.NotNull;
public class ModelArmor extends HumanoidModel<LivingEntity> {
    protected static float INNER_MODEL_OFFSET = 0.38F;
    protected static float OUTER_MODEL_OFFSET = 0.45F;

    public ModelArmor(ModelPart p_170677_) {
        super(p_170677_);
    }

    public void setupAnim(@NotNull LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityIn instanceof ArmorStand armorStand) {
            this.head.xRot = 0.017453292F * armorStand.getHeadPose().getX();
            this.head.yRot = 0.017453292F * armorStand.getHeadPose().getY();
            this.head.zRot = 0.017453292F * armorStand.getHeadPose().getZ();
            this.body.xRot = 0.017453292F * armorStand.getBodyPose().getX();
            this.body.yRot = 0.017453292F * armorStand.getBodyPose().getY();
            this.body.zRot = 0.017453292F * armorStand.getBodyPose().getZ();
            this.leftArm.xRot = 0.017453292F * armorStand.getLeftArmPose().getX();
            this.leftArm.yRot = 0.017453292F * armorStand.getLeftArmPose().getY();
            this.leftArm.zRot = 0.017453292F * armorStand.getLeftArmPose().getZ();
            this.rightArm.xRot = 0.017453292F * armorStand.getRightArmPose().getX();
            this.rightArm.yRot = 0.017453292F * armorStand.getRightArmPose().getY();
            this.rightArm.zRot = 0.017453292F * armorStand.getRightArmPose().getZ();
            this.leftLeg.xRot = 0.017453292F * armorStand.getLeftLegPose().getX();
            this.leftLeg.yRot = 0.017453292F * armorStand.getLeftLegPose().getY();
            this.leftLeg.zRot = 0.017453292F * armorStand.getLeftLegPose().getZ();
            this.rightLeg.xRot = 0.017453292F * armorStand.getRightLegPose().getX();
            this.rightLeg.yRot = 0.017453292F * armorStand.getRightLegPose().getY();
            this.rightLeg.zRot = 0.017453292F * armorStand.getRightLegPose().getZ();
            this.hat.copyFrom(this.head);
        } else {
            super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }

    }
}
