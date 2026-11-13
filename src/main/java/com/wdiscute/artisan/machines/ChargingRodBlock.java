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

public class ChargingRodBlock extends AbstractMachineBlock
{
    public ChargingRodBlock()
    {
        super(Properties.of()
                .destroyTime(2)
                .sound(SoundType.METAL)
        );
    }

    private static final VoxelShape SHAPE_IDLE = Shapes.or(
            Block.box(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
            Block.box(6.0, 12.0, 6.0, 10.0, 16.0, 10.0),
            Block.box(6.0, 4.0, 6.0, 10.0, 5.0, 10.0),
            Block.box(6.0, 6.0, 6.0, 10.0, 7.0, 10.0),
            Block.box(6.0, 8.0, 6.0, 10.0, 9.0, 10.0)
    );

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
            Block.box(6.0, 12.0, 6.0, 10.0, 16.0, 10.0),
            Block.box(5.5, 4.0, 5.5, 10.5, 5.0, 10.5),
            Block.box(5.5, 6.0, 5.5, 10.5, 7.0, 10.5),
            Block.box(5.5, 8.0, 5.5, 10.5, 9.0, 10.5)
    );

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
        return ArtisanBlockEntities.CHARGING_ROD.get().create(blockPos, blockState);
    }
}
