package us.kosdt.chatping.mixin;

import net.minecraft.client.gui.hud.ChatListenerHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.kosdt.chatping.ChatPing;
import us.kosdt.chatping.data.ChatPingData;
import us.kosdt.chatping.data.ChatPingEntry;

@Mixin(ChatListenerHud.class)
public class ChatHandlerMixin {

    @Inject(at = @At("HEAD"), method = "onChatMessage")
    private void onChat(MessageType messageType, Text text, CallbackInfo ci) {
        String message = text.asString();
        for (ChatPingEntry entry : ChatPingData.entries) {
            entry.checkMatch(message);
        }
    }
}
