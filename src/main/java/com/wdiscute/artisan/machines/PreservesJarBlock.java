package com.wdiscute.artisan.machines;

import com.wdiscute.artisan.registry.ArtisanBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PreservesJarBlock extends AbstractMachineBlock
{
    public PreservesJarBlock()
    {
        super(Properties.of()
                .destroyTime(2)
                .sound(SoundType.WOOD)
        );
    }

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 15.0, 14.0),
            Block.box(1.0, 15.0, 1.0, 15.0, 17.0, 15.0)
    );

    private static final VoxelShape SHAPE_IDLE = Block.box(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if (state.getOptionalValue(STATE).orElse(State.IDLE).equals(State.IDLE))
            return SHAPE_IDLE;
        else
            return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return ArtisanBlockEntities.PRESERVES_JAR.get().create(blockPos, blockState);
    }
}
