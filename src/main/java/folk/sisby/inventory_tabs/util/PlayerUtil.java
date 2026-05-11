package folk.sisby.inventory_tabs.util;

import folk.sisby.inventory_tabs.InventoryTabs;
import folk.sisby.inventory_tabs.TabManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerUtil {
    public static final int REACH = 5;
    public static final double BLOCK_REACH_SQUARE = REACH * REACH;

    public static boolean inRange(Player player, BlockPos pos) {
        if (pos.getCenter().distanceToSqr(player.getEyePosition()) > BLOCK_REACH_SQUARE) return false;
        BlockHitResult result = raycast(player, pos);
        return pos.equals(result.getBlockPos());
    }

    public static boolean inRange(Player player, Entity entity) {
        if (entity.distanceToSqr(player.getEyePosition()) > BLOCK_REACH_SQUARE) return false;
        if (InventoryTabs.CONFIG.ignoreWalls) return true;
        EntityHitResult result = raycast(player, entity);
        return result != null && entity.equals(result.getEntity());
    }

    public static BlockHitResult raycast(Player player, BlockPos pos) {
        List<Vec3> blockOffsets = new ArrayList<>();
        RaycastCache raycastCache = TabManager.blockRaycastCache.get(pos);
        if (raycastCache != null && raycastCache.lastValidOffset != null) {
            blockOffsets.add(raycastCache.lastValidOffset);
        }
        blockOffsets.addAll(generateRandomVec3dList(9, new Vec3(0.0D, 0.0D, 0.0D), new Vec3(1.0D, 1.0D, 1.0D)));
        for (Vec3 offset : blockOffsets) {
            BlockHitResult hitResult = player.level().clip(new ClipContext(player.getEyePosition(), Vec3.atLowerCornerOf(pos).add(offset), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            if (hitResult.getType() != HitResult.Type.MISS && hitResult.getBlockPos().equals(pos)) {
                TabManager.blockRaycastCache.computeIfAbsent(pos, p -> new RaycastCache()).hit(offset);
                return hitResult;
            }
        }
        return BlockHitResult.miss(new Vec3(player.getX(), player.getY(), player.getZ()), Direction.EAST, player.blockPosition());
    }

    //Rewrote raycast
    public static EntityHitResult raycast(Player player, Entity target) {
        Vec3 start = player.getEyePosition();
        Vec3 end = target.getBoundingBox().getCenter(); // stable replacement for getPos()

        // A box that covers the segment start->end (plus a little padding)
        var box = player.getBoundingBox()
                .expandTowards(end.subtract(start))
                .inflate(1.0, 1.0, 1.0);

        return ProjectileUtil.getEntityHitResult(player.level(), player, start, end, box, e -> e == target, (float) BLOCK_REACH_SQUARE);
    }

    public static List<Vec3> generateRandomVec3dList(int count, Vec3 min, Vec3 max) {
        List<Vec3> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(generateRandomVec3d(min, max));
        }
        return list;
    }

    private static Vec3 generateRandomVec3d(Vec3 min, Vec3 max) {
        Random random = new Random();
        double x = min.x + (max.x - min.x) * random.nextDouble();
        double y = min.y + (max.y - min.y) * random.nextDouble();
        double z = min.z + (max.z - min.z) * random.nextDouble();
        return new Vec3(x, y, z);
    }
}
