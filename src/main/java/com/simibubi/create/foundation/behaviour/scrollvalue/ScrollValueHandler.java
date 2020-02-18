package com.simibubi.create.foundation.behaviour.scrollvalue;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.behaviour.ValueBoxTransform.Sided;
import com.simibubi.create.foundation.behaviour.base.TileEntityBehaviour;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ScrollValueHandler {

	@OnlyIn(Dist.CLIENT)
	public static boolean onScroll(double delta) {
		RayTraceResult objectMouseOver = Minecraft.getInstance().objectMouseOver;
		if (!(objectMouseOver instanceof BlockRayTraceResult))
			return false;

		BlockRayTraceResult result = (BlockRayTraceResult) objectMouseOver;
		Minecraft mc = Minecraft.getInstance();
		ClientWorld world = mc.world;
		BlockPos blockPos = result.getPos();

		ScrollValueBehaviour scrolling = TileEntityBehaviour.get(world, blockPos, ScrollValueBehaviour.TYPE);
		if (scrolling == null)
			return false;
		if (!mc.player.isAllowEdit())
			return false;
		if (scrolling.needsWrench && !AllItems.WRENCH.typeOf(mc.player.getHeldItemMainhand()))
			return false;
		if (scrolling.slotPositioning instanceof Sided)
			((Sided) scrolling.slotPositioning).fromSide(result.getFace());
		if (!scrolling.testHit(objectMouseOver.getHitVec()))
			return false;

		scrolling.ticksUntilScrollPacket = 10;
		scrolling.scrollableValue = (int) MathHelper.clamp(
				scrolling.scrollableValue
						+ Math.signum(delta) * scrolling.step.apply(scrolling.scrollableValue, delta > 0),
				scrolling.min, scrolling.max);

		return true;
	}

}
