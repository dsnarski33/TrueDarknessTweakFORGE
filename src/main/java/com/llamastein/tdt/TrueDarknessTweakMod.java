/*
 * Licensed under GNU GPL.
 * This is a small modification of True Darkness @
 *   https://github.com/grondag/darkness and
 *   https://www.curseforge.com/minecraft/mc-mods/true-darkness,
 * to add config variables to influence the darkness parameters and implement a simple server-side enforcement.
 */
package com.llamastein.tdt;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TrueDarknessTweakMod.MOD_ID)
public final class TrueDarknessTweakMod
{
    public static final String MOD_ID = "darknesstweak";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static CommonConfig commonConfig;

    public TrueDarknessTweakMod()
    {
        LOGGER.info("Initializing TrueDarknessTweakMod");

        final var config = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        commonConfig = config.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.getRight(), MOD_ID + ".toml");

        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(commonConfig::loadOptionsFromConfigFile);
        bus.addListener(NetworkHandler.INSTANCE::registerPackets);
        bus.addListener(commonConfig::onConfigUpdate);
    }
}