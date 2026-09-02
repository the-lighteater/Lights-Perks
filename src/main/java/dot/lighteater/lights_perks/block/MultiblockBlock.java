package dot.lighteater.lights_perks.block;

import dot.lighteater.lights_perks.ModNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MultiblockBlock extends Block {

    public static final BooleanProperty ON = BooleanProperty.create("on");

    public MultiblockBlock(Properties properties) {
        super(properties);

        registerDefaultState(
                stateDefinition.any()
                        .setValue(ON, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(ON);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return super.getPistonPushReaction(state);
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {

        if (!level.isClientSide) {

            MultiblockManager manager =
                    MultiblockManager.findManager(level, pos);

            if (manager != null) {

                boolean complete =
                        manager.isComplete(level);

                System.out.println(
                        "[Multiblock] Manager: "
                                + manager.getBlockPos()
                );

                System.out.println(
                        "[Multiblock] Complete: "
                                + complete
                );

                if (complete) {
                    ModNetwork.sendOpenPerkMenu();
                }

            } else {

                System.out.println(
                        "[Multiblock] No manager found."
                );
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}