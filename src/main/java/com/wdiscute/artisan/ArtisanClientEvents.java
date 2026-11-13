package com.wdiscute.artisan;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wdiscute.artisan.machines.AbstractMachineBlockEntity;
import com.wdiscute.artisan.machines.MachineBlockRenderer;
import com.wdiscute.artisan.registry.ArtisanBlockEntities;
import com.wdiscute.artisan.registry.ArtisanDataMaps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = Artisan.MOD_ID, value = Dist.CLIENT)
public class ArtisanClientEvents
{
    @SubscribeEvent
    public static void onHighlight(RenderHighlightEvent.Block event)
    {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        BlockPos pos = event.getTarget().getBlockPos();
        if (!(level.getBlockEntity(pos) instanceof AbstractMachineBlockEntity be) || !be.isWorking()) return;

        float partial = event.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float[] a = MachineBlockRenderer.squish(level, pos, partial, !be.upgrades.isEmpty());
        float sxz = a[0], sy = a[1], hop = a[2];

        Vec3 cam = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());

        VoxelShape shape = be.getBlockState().getShape(level, pos, CollisionContext.of(event.getCamera().getEntity()));

        double ox = pos.getX() - cam.x;
        double oy = pos.getY() - cam.y;
        double oz = pos.getZ() - cam.z;
        PoseStack.Pose pose = poseStack.last();

        shape.forAllEdges((x1, y1, z1, x2, y2, z2) ->
        {
            float ax = (float) (0.5 + (x1 - 0.5) * sxz + ox);
            float ay = (float) (y1 * sy + hop + oy);
            float az = (float) (0.5 + (z1 - 0.5) * sxz + oz);
            float bx = (float) (0.5 + (x2 - 0.5) * sxz + ox);
            float by = (float) (y2 * sy + hop + oy);
            float bz = (float) (0.5 + (z2 - 0.5) * sxz + oz);

            float dx = bx - ax, dy = by - ay, dz = bz - az;
            float len = Mth.sqrt(dx * dx + dy * dy + dz * dz);
            if (len == 0f) return;
            dx /= len; dy /= len; dz /= len;

            consumer.addVertex(pose, ax, ay, az).setColor(0f, 0f, 0f, 0.4f).setNormal(pose, dx, dy, dz);
            consumer.addVertex(pose, bx, by, bz).setColor(0f, 0f, 0f, 0.4f).setNormal(pose, dx, dy, dz);
        });

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ArtisanBlockEntities.LOOM.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.CHEESE_PRESS.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.WINE_KEG.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.AGING_CASK.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.ANCIENT_CASK.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.CRYSTALARIUM.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.DELUXE_WORM_FARM.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.DEHYDRATOR.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.MAYONNAISE_MACHINE.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.PRESERVES_JAR.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.SEED_MAKER.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.RECYCLING_MACHINE.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.BAIT_MAKER.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.OIL_MAKER.get(), MachineBlockRenderer::new);
        event.registerBlockEntityRenderer(ArtisanBlockEntities.CHARGING_ROD.get(), MachineBlockRenderer::new);
    }
}
