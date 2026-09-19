package ca.dnamobile.droidbridgelan.mixin;

import ca.dnamobile.droidbridgelan.DroidBridgeLanMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetAddress;

@Mixin(targets = "net.minecraft.server.network.ServerConnectionListener")
public abstract class ServerConnectionListenerMixin {
    @Inject(method = "startTcpServerListener(Ljava/net/InetAddress;I)V", at = @At("TAIL"))
    private void droidbridgeLan$afterTcpListener(InetAddress address, int port, CallbackInfo ci) {
        DroidBridgeLanMod.onLanStarted(port, this);
    }

    @Inject(method = "stop()V", at = @At("HEAD"))
    private void droidbridgeLan$beforeStop(CallbackInfo ci) {
        DroidBridgeLanMod.stopTunnel();
    }
}
