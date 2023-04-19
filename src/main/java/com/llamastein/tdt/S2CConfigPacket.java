/*
 * BASED ON: FallThru (srsCode) @ github.com/srsCode/FallThru, MIT License
 *
 * Project      : FallThru
 * File         : S2CConfigPacket.java
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

import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;

/**
 * The packet used for syncing the server config with the client config.
 */
final class S2CConfigPacket
{
    /**
     * The serialized message to be sent to a client.
     */
    private final CompoundTag config;

    S2CConfigPacket()
    {
        this.config = TrueDarknessTweakMod.commonConfig.options.toNBT();
    }

    private S2CConfigPacket(final CompoundTag nbt)
    {
        this.config = nbt;
    }

    /**
     * A S2CConfigPacket decoder.
     *
     * @param  input A {@link FriendlyByteBuf} containing a serialized Options Compound tag.
     * @return       A new populated S2CConfigPacket.
     */
    static S2CConfigPacket decode(final FriendlyByteBuf input)
    {
        return new S2CConfigPacket(Objects.requireNonNull(input.readNbt(), "FriendlyByteBuf is null. This should be impossible."));
    }

    /**
     * A S2CConfigPacket encoder for sending a serialized Options Compound tag.
     *
     * @param output The {@link FriendlyByteBuf} to write the serialized BlockConfigMap to.
     */
    void encode(final FriendlyByteBuf output)
    {
        output.writeNbt(config);
    }

    /**
     * A handler for processing a S2CConfigPacket on the client.
     *
     * @param packet The received packet to be handled.
     * @param ctx    The {@link NetworkEvent.Context} handling the communication.
     */
    static void handle(final S2CConfigPacket packet, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> TrueDarknessTweakMod.commonConfig.options = CommonConfig.Options.fromNBT(packet.config));
        ctx.get().setPacketHandled(true);
    }
}
