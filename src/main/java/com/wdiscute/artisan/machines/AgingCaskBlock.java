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

public class AgingCaskBlock extends AbstractMachineBlock
{
    public AgingCaskBlock()
    {
        super(Properties.of()
                .destroyTime(2)
                .sound(SoundType.WOOD)
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        Direction dir = state.getOptionalValue(FACING).orElse(Direction.NORTH);
        if (dir.equals(Direction.NORTH) || dir.equals(Direction.SOUTH))
            return WineKegBlock.SHAPE_NS;
        else
            return WineKegBlock.SHAPE_WE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return ArtisanBlockEntities.AGING_CASK.get().create(blockPos, blockState);
    }
}
