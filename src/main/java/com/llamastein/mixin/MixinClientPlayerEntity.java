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
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class MixinClientPlayerEntity {

	private static ClientLevel clientWorld;
	private static LivingEntity livingEntity;

	@Inject(method = "<init>*", at = @At(value = "RETURN"))
	private void afterInit(Minecraft p_108621_, ClientLevel world, ClientPacketListener p_108623_, StatsCounter p_108624_, ClientRecipeBook p_108625_, boolean p_108626_, boolean p_108627_, CallbackInfo ci) {
		clientWorld = world;
		livingEntity = (LivingEntity)(Object)this;
	}

	@Inject(method = "getWaterVision", at = @At(value = "RETURN"), cancellable = true)
	private void getUnderwaterVisibility(CallbackInfoReturnable<Float> ci) {
		if(ci.getReturnValue() < 0.02 || DarknessHandler.dimSkyFactor > 0.98f || clientWorld.dimension() != Level.OVERWORLD || !DarknessHandler.isDark(clientWorld))
			return;
		if(livingEntity.hasEffect(MobEffects.CONDUIT_POWER))
			return;
		ci.setReturnValue( Math.min(ci.getReturnValue(), Math.min(DarknessHandler.dimSkyFactor, 0.02f)) );
	}
}
