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

public class OilMakerBlock extends AbstractMachineBlock
{
    public OilMakerBlock()
    {
        super(Properties.of()
                .destroyTime(2)
                .sound(SoundType.COPPER_BULB)
        );
    }

    private static final VoxelShape SHAPE_S = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), //base
            Block.box(11.0, 2.0, 5.5, 16.0, 13.0, 10.5), //glass thing
            Block.box(0.0, 2.0, 3.5, 10.0, 11.0, 12.5), //oil thing
            Block.box(1.0, 11.0, 4.5, 9.0, 12.0, 11.5), //oil thing top indent
            Block.box(4.5, 12.0, 7.5, 5.5, 16.0, 8.5), //antenna
            Block.box(3.5, 16.0, 6.5, 6.5, 19.0, 9.5) //antenna top
    );

    private static final VoxelShape SHAPE_N = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),   // base
            Block.box(0.0, 2.0, 5.5, 5.0, 13.0, 10.5),   // glass thing
            Block.box(6.0, 2.0, 3.5, 16.0, 11.0, 12.5),  // oil thing
            Block.box(7.0, 11.0, 4.5, 15.0, 12.0, 11.5), // oil thing top indent
            Block.box(10.5, 12.0, 7.5, 11.5, 16.0, 8.5), // antenna
            Block.box(9.5, 16.0, 6.5, 12.5, 19.0, 9.5)   // antenna top
    );

    private static final VoxelShape SHAPE_W = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), //base
            Block.box(5.5, 2.0, 11.0, 10.5, 13.0, 16.0), //glass thing
            Block.box(3.5, 2.0, 0.0, 12.5, 11.0, 10.0), //oil thing
            Block.box(4.5, 11.0, 1.0, 11.5, 12.0, 9.0), //oil thing top indent
            Block.box(7.5, 12.0, 4.5, 8.5, 16.0, 5.5), //antenna
            Block.box(6.5, 16.0, 3.5, 9.5, 19.0, 6.5) //antenna top
    );

    private static final VoxelShape SHAPE_E = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),   // base
            Block.box(5.5, 2.0, 0.0, 10.5, 13.0, 5.0),   // glass thing
            Block.box(3.5, 2.0, 6.0, 12.5, 11.0, 16.0),  // oil thing
            Block.box(4.5, 11.0, 7.0, 11.5, 12.0, 15.0), // oil thing top indent
            Block.box(7.5, 12.0, 10.5, 8.5, 16.0, 11.5), // antenna
            Block.box(6.5, 16.0, 9.5, 9.5, 19.0, 12.5)   // antenna top
    );

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getOptionalValue(FACING).orElse(Direction.NORTH))
        {
            case NORTH -> SHAPE_N;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_S;
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return ArtisanBlockEntities.OIL_MAKER.get().create(blockPos, blockState);
    }
}
