/*
 * Licensed under GNU GPL.
 * This is a small modification of True Darkness @
 *   https://github.com/grondag/darkness and
 *   https://www.curseforge.com/minecraft/mc-mods/true-darkness,
 * to add config variables to influence the darkness parameters and implement a simple server-side enforcement.
 * ------------------------------------------------------------------------------
 * This file is part of True Darkness and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.llamastein.mixin;

import com.llamastein.tdt.DarknessHandler;
import com.llamastein.tdt.LightmapAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	@Final @Shadow private Minecraft minecraft;
	@Final @Shadow private LightTexture lightTexture;

	@Inject(at = @At("HEAD"), method = "renderLevel")
	public void renderWorld(float tickDelta, long limitTime, PoseStack matrices, CallbackInfo ci) {
		final LightmapAccess lightmap = (LightmapAccess)lightTexture;
		if (lightmap.darkness_isDirty()) {
			minecraft.getProfiler().push("lightTex");
			DarknessHandler.updateLuminance(tickDelta, minecraft, (GameRenderer)(Object)this, lightmap.darkness_prevFlicker());
			minecraft.getProfiler().pop();
		}
	}
}
