package com.wdiscute.artisan.machines;

import com.wdiscute.artisan.registry.ArtisanBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DehydratorBlock extends AbstractMachineBlock
{
    public DehydratorBlock()
    {
        super(Properties.of()
                .destroyTime(2)
                .sound(SoundType.WOOD)
        );
    }

    private static final VoxelShape SHAPE_NS = Block.box(1.0, 0.0, 3.0, 15.0, 16.0, 13.0);

    private static final VoxelShape SHAPE_WE = Block.box(3.0, 0.0, 1.0, 13.0, 16.0, 15.0);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        Direction dir = state.getOptionalValue(FACING).orElse(Direction.NORTH);
        if (dir.equals(Direction.NORTH) || dir.equals(Direction.SOUTH))
            return SHAPE_NS;
        else
            return SHAPE_WE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return ArtisanBlockEntities.DEHYDRATOR.get().create(blockPos, blockState);
    }
}
