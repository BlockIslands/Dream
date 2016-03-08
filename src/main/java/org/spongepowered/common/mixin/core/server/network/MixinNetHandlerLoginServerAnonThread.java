/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.server.network;

import net.minecraft.server.network.NetHandlerLoginServer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.interfaces.IMixinNetHandlerLoginServer;

@Mixin(targets = "net/minecraft/server/network/NetHandlerLoginServer$2")
public class MixinNetHandlerLoginServerAnonThread extends Thread {

    @Shadow(aliases = "this$0")
    @Final
    private NetHandlerLoginServer field_180221_a;

    private IMixinNetHandlerLoginServer outerRef = (IMixinNetHandlerLoginServer) this.field_180221_a;

    @Inject(method = "run()V", at = @At(value = "JUMP", opcode = Opcodes.IFNULL, ordinal = 0, shift = At.Shift.AFTER),
            remap = false, cancellable = true)
    public void fireAuthEvent(CallbackInfo ci) {
        if (this.outerRef.fireAuthEvent()) {
            ci.cancel();
        }
    }

}
