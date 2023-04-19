/*
 * Licensed under GNU GPL.
 * This is a small modification of True Darkness @
 *   https://github.com/grondag/darkness and
 *   https://www.curseforge.com/minecraft/mc-mods/true-darkness,
 * to add config variables to influence the darkness parameters and implement a simple server-side enforcement.
 */
package com.llamastein.tdt;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.List;

public class CommonConfig
{
    private final ConfigOptions configOptions;
    public Options options = null;

    private static final String LANGKEY_CONFIG                  = "config";

    public enum Values {
        inOverworld, inNether, inEnd, inDefault, inSkyless, ignoreMoonPhase, useBlockLightOnly, allowGamma, netherFog, endFog, moonSizeData, nightVisionAmt, conduitAmt, enforceClientUsage;

        @Override
        public String toString() {
            return switch (this) {
                case inOverworld -> "dark_overworld";
                case inNether -> "dark_nether";
                case inEnd -> "dark_end";
                case inDefault -> "dark_default";
                case inSkyless -> "dark_skyless";
                case ignoreMoonPhase -> "ignore_moon_phase";
                case useBlockLightOnly -> "only_affect_block_light";
                case allowGamma -> "allow_gamma";
                case netherFog -> "dark_nether_fog";
                case endFog -> "dark_end_fog";
                case moonSizeData -> "moon_size_data";
                case nightVisionAmt -> "nightvision_amt";
                case conduitAmt -> "conduit_amt";
                case enforceClientUsage -> "enforce_client_usage";
            };
        }
    }

    public enum MoonSizeData { min, mid, max, minmidAdjust, maxAdjust }

    public record ConfigOptions(BooleanValue inOverworld, BooleanValue inNether, BooleanValue inEnd, BooleanValue inDefault, BooleanValue inSkyless,
                                BooleanValue ignoreMoonPhase, BooleanValue useBlockLightOnly, BooleanValue allowGamma, DoubleValue netherFog, DoubleValue endFog,
                                ConfigValue<List<? extends Double>> moonSizeData, DoubleValue nightVisionAmt, DoubleValue conduitAmt, BooleanValue enforceClientUsage) {
        
        public Options setup() {
            return new Options(inOverworld.get(),
                    inNether.get(),
                    inEnd.get(),
                    inDefault.get(),
                    inSkyless.get(),
                    ignoreMoonPhase.get(),
                    useBlockLightOnly.get(),
                    allowGamma.get(),
                    netherFog.get().floatValue(),
                    endFog.get().floatValue(),
                    moonSizeData.get(),
                    nightVisionAmt.get().floatValue(),
                    conduitAmt.get().floatValue(),
                    enforceClientUsage.get());
        }
    }

    public record Options(boolean inOverworld, boolean inNether, boolean inEnd, boolean inDefault, boolean inSkyless,
                          boolean ignoreMoonPhase, boolean useBlockLightOnly, boolean allowGamma, float netherFog, float endFog,
                          float moonSizeMin, float moonSizeMid, float moonSizeMax, float moonSizeMinMidAdjust,
                          float moonSizeMaxAdjust, float nightVisionAmt, float conduitAmt, boolean enforceClientUsage)
    {
        public Options(boolean inOverworld, boolean inNether, boolean inEnd, boolean inDefault, boolean inSkyless,
                       boolean ignoreMoonPhase, boolean useBlockLightOnly, boolean allowGamma, float netherFog, float endFog,
                       List<? extends Double> moonSizeData, float nightVisionAmt, float conduitAmt, boolean enforceClientUsage) {
            this(inOverworld, inNether, inEnd, inDefault, inSkyless, ignoreMoonPhase, useBlockLightOnly, allowGamma, netherFog, endFog,
                    moonSizeData.get(0).floatValue(), moonSizeData.get(1).floatValue(), moonSizeData.get(2).floatValue(),
                    moonSizeData.get(3).floatValue(), moonSizeData.get(4).floatValue(), nightVisionAmt, conduitAmt, enforceClientUsage);
        }

        public String toString(Values values) {
            return switch (values) {
                case inOverworld -> Boolean.toString(inOverworld);
                case inNether -> Boolean.toString(inNether);
                case inEnd -> Boolean.toString(inEnd);
                case inDefault -> Boolean.toString(inDefault);
                case inSkyless -> Boolean.toString(inSkyless);
                case ignoreMoonPhase -> Boolean.toString(ignoreMoonPhase);
                case useBlockLightOnly -> Boolean.toString(useBlockLightOnly);
                case allowGamma -> Boolean.toString(allowGamma);
                case netherFog -> Double.toString(netherFog);
                case endFog -> Double.toString(endFog);
                case moonSizeData -> "[" + moonSizeMin + "," + moonSizeMid + "," + moonSizeMax + "," + moonSizeMinMidAdjust + "," + moonSizeMaxAdjust + "]";
                case nightVisionAmt -> Double.toString(nightVisionAmt);
                case conduitAmt -> Double.toString(conduitAmt);
                case enforceClientUsage -> Boolean.toString(enforceClientUsage);
            };
        }

        public CompoundTag toNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean(Values.inOverworld.name(), inOverworld);
            nbt.putBoolean(Values.inNether.name(), inNether);
            nbt.putBoolean(Values.inEnd.name(), inEnd);
            nbt.putBoolean(Values.inDefault.name(), inDefault);
            nbt.putBoolean(Values.inSkyless.name(), inSkyless);
            nbt.putBoolean(Values.ignoreMoonPhase.name(), ignoreMoonPhase);
            nbt.putBoolean(Values.useBlockLightOnly.name(), useBlockLightOnly);
            nbt.putBoolean(Values.allowGamma.name(), allowGamma);
            nbt.putFloat(Values.netherFog.name(), netherFog);
            nbt.putFloat(Values.endFog.name(), endFog);
            nbt.putFloat(Values.moonSizeData.name() + MoonSizeData.min.name(), moonSizeMin);
            nbt.putFloat(Values.moonSizeData.name() + MoonSizeData.mid.name(), moonSizeMid);
            nbt.putFloat(Values.moonSizeData.name() + MoonSizeData.max.name(), moonSizeMax);
            nbt.putFloat(Values.moonSizeData.name() + MoonSizeData.minmidAdjust.name(), moonSizeMinMidAdjust);
            nbt.putFloat(Values.moonSizeData.name() + MoonSizeData.maxAdjust.name(), moonSizeMaxAdjust);
            nbt.putFloat(Values.nightVisionAmt.name(), nightVisionAmt);
            nbt.putFloat(Values.conduitAmt.name(), conduitAmt);
            nbt.putBoolean(Values.enforceClientUsage.name(), enforceClientUsage);
            return nbt;
        }

        public static Options fromNBT(CompoundTag nbt) {
            return new Options(
                    nbt.getBoolean(Values.inOverworld.name()),
                    nbt.getBoolean(Values.inNether.name()),
                    nbt.getBoolean(Values.inEnd.name()),
                    nbt.getBoolean(Values.inDefault.name()),
                    nbt.getBoolean(Values.inSkyless.name()),
                    nbt.getBoolean(Values.ignoreMoonPhase.name()),
                    nbt.getBoolean(Values.useBlockLightOnly.name()),
                    nbt.getBoolean(Values.allowGamma.name()),
                    nbt.getFloat(Values.netherFog.name()),
                    nbt.getFloat(Values.endFog.name()),
                    nbt.getFloat(Values.moonSizeData.name() + MoonSizeData.min.name()),
                    nbt.getFloat(Values.moonSizeData.name() + MoonSizeData.mid.name()),
                    nbt.getFloat(Values.moonSizeData.name() + MoonSizeData.max.name()),
                    nbt.getFloat(Values.moonSizeData.name() + MoonSizeData.minmidAdjust.name()),
                    nbt.getFloat(Values.moonSizeData.name() + MoonSizeData.maxAdjust.name()),
                    nbt.getFloat(Values.nightVisionAmt.name()),
                    nbt.getFloat(Values.conduitAmt.name()),
                    nbt.getBoolean(Values.enforceClientUsage.name())

            );
        }
    }

    CommonConfig(final Builder builder)
    {

        builder.comment("  TrueDarknessPlus Config").push(TrueDarknessTweakMod.MOD_ID);
        configOptions = new ConfigOptions(
            builder.comment("", "  Allows darkness to be applied to the OVERWORLD")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.inOverworld.toString()))
                    .define(Values.inOverworld.toString(), true),
            builder.comment("", "  Allows darkness to be applied to the NETHER")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.inNether.toString()))
                    .define(Values.inNether.toString(), false),
            builder.comment("", "  Allows darkness to be applied to the END")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.inEnd.toString()))
                    .define(Values.inEnd.toString(), false),
            builder.comment("", "  inDefault  - carried from base code")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.inDefault.toString()))
                    .define(Values.inDefault.toString(), false),
            builder.comment("", "  inSkyless  - carried from base code")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.inSkyless.toString()))
                    .define(Values.inSkyless.toString(), false),
            builder.comment("", "  ignoreMoonPhase  - carried from base code")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.ignoreMoonPhase.toString()))
                    .define(Values.ignoreMoonPhase.toString(), false),
            builder.comment("", "  useBlockLightOnly - carried from base code")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.useBlockLightOnly.toString()))
                    .define(Values.useBlockLightOnly.toString(), false),
            builder.comment("", "  Allows client gamma setting to influence the darkness")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.allowGamma.toString()))
                    .define(Values.allowGamma.toString(), false),
            builder.comment("", "  netherFog - carried from base code")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.netherFog.toString()))
                    .defineInRange(Values.netherFog.toString(), 1.0f/*0.5f*/, 0.0f, 1.0f),
            builder.comment("", "  endFog - carried from base code")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.endFog.toString()))
                    .defineInRange(Values.endFog.toString(), 1.0f/*0.0f*/, 0.0f, 1.0f),
            builder.comment("",
                            "  These values can be tweaked to adjust how the moon size influences the darkness. Current formula is:",
                            "  clamp([moonsize] * minmid_adjust, min, mid) + ([moonsize] > max ? ([moonsize] - max) * max_adjust : 0)",
                            "  (LACKS VALIDATION, ENSURE ARRAY OF 5 DECIMALS)",
                            "  where: ",
                            "    [moonsize] is the current moon brightness, [0 - 1] range",
                            "    [moon_size_data] entries are [min, mid, max, minmid_adjust, max_adjust]")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.moonSizeData.toString()))
                    .defineList(Values.moonSizeData.toString(), List.of(0.0, 0.33, 0.89, 0.67, 0.5), s -> true), //todo validate!
            builder.comment("", "  Night Vision brightness amount")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.nightVisionAmt.toString()))
                    .defineInRange(Values.nightVisionAmt.toString(), 0.5f, 0.0f, 1.0f),
            builder.comment("", "  Conduit brightness amount")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.conduitAmt.toString()))
                    .defineInRange(Values.conduitAmt.toString(), 0.5f, 0.0f, 1.0f),
            builder.comment("", "  Allows a simple server-side verification that the client is using this")
                    .translation(getLangKey(LANGKEY_CONFIG, Values.enforceClientUsage.toString()))
                    .define(Values.enforceClientUsage.toString(), true)
        );
        builder.pop();

    }

//    private static final String VERSION = "arb1";
//    public static SimpleChannel CHANNEL = null;

    public void loadOptionsFromConfigFile(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
        options = configOptions.setup();
//        if(options.enforceClientUsage && CHANNEL == null)
//            CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(TrueDarknessTweakMod.MOD_ID, "network"), () -> VERSION, VERSION::equals, VERSION::equals);
    }

    /**
     * A I18n key helper.
     * -> Sourced originally from FallThru (srsCode) @ github.com/srsCode/FallThru, MIT License
     *
     * @param  keys A list of key elements to be joined.
     * @return      A full I18n key.
     */
    private static String getLangKey(final String... keys)
    {
        return (keys.length > 0) ? String.join(".", TrueDarknessTweakMod.MOD_ID, String.join(".", keys)) : TrueDarknessTweakMod.MOD_ID;
    }

    /**
     * This Event handler syncs a server-side config change with all connected players.
     * This fires when the config file has been changed on disk and only updates on the client if the
     * client is <b>not</b> connected to a remote server, or if the integrated server <b>is</b> running.
     * This will always cause syncing on a dedicated server that will propogate to clients.
     * -> Sourced originally from FallThru (srsCode) @ github.com/srsCode/FallThru, MIT License
     *
     * @param event The {@link ModConfigEvent.Reloading} event
     */
    void onConfigUpdate(final ModConfigEvent.Reloading event)
    {
        if (event.getConfig().getModId().equals(TrueDarknessTweakMod.MOD_ID)) {
            if (FMLEnvironment.dist == Dist.CLIENT && (Minecraft.getInstance().getSingleplayerServer() == null || Minecraft.getInstance().getConnection() != null)) {
                TrueDarknessTweakMod.LOGGER.warn("The config file has changed but the integrated server is not running. Nothing to do.");
            } else {
                TrueDarknessTweakMod.LOGGER.warn("The config file has changed and the server is running. Resyncing config.");
                TrueDarknessTweakMod.commonConfig.loadOptionsFromConfigFile(null);
                DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> NetworkHandler.INSTANCE::updateAll);
            }
        }
    }

}