package com.plusls.carpet.mixin.rule.dbslabBroken;



import com.plusls.carpet.PcaSettings;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

@Mixin(SlabBlock.class)
public class MixinDbslab extends Block {

	public MixinDbslab(Settings settings) {
		super(settings);	
	}

	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		return super.calcBlockBreakingDelta( state,  player,  world,  pos)*(PcaSettings.separateSlabBreaking?2.0f:1.0f);
	}
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
			@Nullable BlockEntity blockEntity, ItemStack stack) {
		if (PcaSettings.separateSlabBreaking && state.get(SlabBlock.TYPE) == net.minecraft.block.enums.SlabType.DOUBLE) {
			world.setBlockState(pos, Blocks.BARRIER.getDefaultState(),Block.SKIP_LIGHTING_UPDATES,0);
			float f = player.getPitch();
			float g = player.getYaw();
			Vec3d lv = player.getEyePos();
			float h = MathHelper.cos(-g * ((float) Math.PI / 180) - (float) Math.PI);
			float i = MathHelper.sin(-g * ((float) Math.PI / 180) - (float) Math.PI);
			float j = -MathHelper.cos(-f * ((float) Math.PI / 180));
			float k = MathHelper.sin(-f * ((float) Math.PI / 180));
			float l = i * j;
			float m = k;
			float n = h * j;

			Vec3d lv2 = lv.add((double) l * 5.0, (double) m * 5.0, (double) n * 5.0);
			BlockHitResult blres = world.raycast(new RaycastContext(lv, lv2, RaycastContext.ShapeType.OUTLINE,
					RaycastContext.FluidHandling.NONE, player));
			boolean t = blres.getPos().y > 0.5 + pos.getY();
			//System.out.println(blres.getPos().y);

			world.setBlockState(pos, state.with(SlabBlock.TYPE,
					(!t) ? net.minecraft.block.enums.SlabType.TOP : net.minecraft.block.enums.SlabType.BOTTOM));
			state = state.with(SlabBlock.TYPE,
					(t) ? net.minecraft.block.enums.SlabType.TOP : net.minecraft.block.enums.SlabType.BOTTOM);
		}
		super.afterBreak(world, player, pos, state, blockEntity, stack);
	}
}
