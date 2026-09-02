package dot.lighteater.lights_perks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockManager {

    private final BlockPos blockPos;

    public MultiblockManager(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public boolean isComplete(Level level) {

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {

                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    BlockPos checkPos =
                            blockPos.offset(x, y, z);

                    if (level.getBlockState(checkPos).getBlock()
                            != ModBlocks.MULTIBLOCK_BLOCK.get()) {

                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void updateStates(Level level) {

        boolean complete = isComplete(level);

        BlockState managerState =
                level.getBlockState(blockPos);

        if (managerState.getBlock() ==
                ModBlocks.MULTIBLOCK_CENTER.get()) {

            boolean active =
                    managerState.getValue(
                            MultiblockManagerBlock.ACTIVE
                    );

            if (active != complete) {

                level.setBlock(
                        blockPos,
                        managerState.setValue(
                                MultiblockManagerBlock.ACTIVE,
                                complete
                        ),
                        3
                );

                if (complete) {
                    activateEffects(level);
                } else {
                    deactivateEffects(level);
                }
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {

                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    BlockPos checkPos =
                            blockPos.offset(x, y, z);

                    BlockState state =
                            level.getBlockState(checkPos);

                    if (state.getBlock()
                            != ModBlocks.MULTIBLOCK_BLOCK.get()) {
                        continue;
                    }

                    boolean current =
                            state.getValue(MultiblockBlock.ON);

                    if (current != complete) {

                        level.setBlock(
                                checkPos,
                                state.setValue(
                                        MultiblockBlock.ON,
                                        complete
                                ),
                                3
                        );
                    }
                }
            }
        }
    }

    private void activateEffects(Level level) {

        // Activation sound
        level.playSound(
                null,
                blockPos,
                net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE,
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        // Activation particles
        spawnActivationParticles(level);
    }

    private void spawnActivationParticles(Level level) {

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {

                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    BlockPos particlePos =
                            blockPos.offset(x, y, z);

                    serverLevel.sendParticles(
                            ParticleTypes.END_ROD,
                            particlePos.getX() + 0.5,
                            particlePos.getY() + 0.5,
                            particlePos.getZ() + 0.5,
                            15,
                            0.2,
                            0.2,
                            0.2,
                            0.02
                    );
                }
            }
        }
    }

    private void deactivateEffects(Level level) {

        // Deactivation sound
        level.playSound(
                null,
                blockPos,
                net.minecraft.sounds.SoundEvents.BEACON_DEACTIVATE,
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0F,
                1.0F
        );
    }

    public static MultiblockManager findManager(
            Level level,
            BlockPos componentPos
    ) {

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {

                    BlockPos checkPos =
                            componentPos.offset(x, y, z);

                    if (level.getBlockState(checkPos).getBlock()
                            == ModBlocks.MULTIBLOCK_CENTER.get()) {

                        return new MultiblockManager(checkPos);
                    }
                }
            }
        }

        return null;
    }

    public static boolean isComplete(
            Level level,
            BlockPos position
    ) {

        MultiblockManager manager =
                findManager(level, position);

        if (manager == null) {
            return false;
        }

        return manager.isComplete(level);
    }
}