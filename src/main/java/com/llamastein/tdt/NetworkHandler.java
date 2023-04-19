/*
 * BASED ON: FallThru (srsCode) @ github.com/srsCode/FallThru, MIT License
 *
 * Project      : FallThru
 * File         : NetworkHandler.java
 *
 * Copyright (c) 2019-2021 srsCode, srs-bsns (forfrdm [at] gmail.com)
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.llamastein.tdt;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = TrueDarknessTweakMod.MOD_ID)
public enum NetworkHandler
{
    INSTANCE;

    private static final ResourceLocation NET_CHANNEL_NAME = new ResourceLocation(TrueDarknessTweakMod.MOD_ID, "tdt_enabled");
    private static final String           NET_VERSION      = "tdt1";

    private SimpleChannel    CHANNEL;

    /**
     * An Event handler to register the {@link S2CConfigPacket} packet for server -> client config sync.
     *
     * @param event The {@link FMLCommonSetupEvent}.
     */
    public void registerPackets(@SuppressWarnings("unused") final FMLCommonSetupEvent event)
    {
        CHANNEL = NetworkRegistry.newSimpleChannel(NET_CHANNEL_NAME, () -> NET_VERSION, NET_VERSION::equals, NET_VERSION::equals);
        CHANNEL.messageBuilder(S2CConfigPacket.class, 0)
                .decoder(S2CConfigPacket::decode)
                .encoder(S2CConfigPacket::encode)
                .consumer(S2CConfigPacket::handle)
                .add();
    }

    /**
     * A helper for syncing a single client from a remote server. Useful for when a player connects to the server.
     */
    public void updatePlayer(final ServerPlayer player)
    {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new S2CConfigPacket());
    }

    /**
     * A helper for syncing all clients from the server. Useful for when the configuration changes on the server and
     * all clients need to be updated.
     */
    public void updateAll()
    {
        CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CConfigPacket());
    }

    /**
     * A Network event handler for syncing the client with a remote server upon connection.
     */
    @SubscribeEvent
    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        final var player = event.getPlayer();
        final var server = player.getServer();
        if (server != null && server.isDedicatedServer()) {
            server.execute(() -> INSTANCE.updatePlayer((ServerPlayer) player));
        }
    }
}
