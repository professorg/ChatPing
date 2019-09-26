package us.kosdt.chatping;

import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import us.kosdt.chatping.data.ChatPingData;

public class ChatPing implements ClientModInitializer {

    private static FabricKeyBinding keyBinding;

    @Override
    public void onInitializeClient() {
        ChatPingData.init();
    }
}
