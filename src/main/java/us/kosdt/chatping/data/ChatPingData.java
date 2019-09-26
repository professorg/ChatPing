package us.kosdt.chatping.data;

import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.ArrayUtils;
import us.kosdt.chatping.ChatPing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;

public class ChatPingData {

    private static final File FILE = new File(FabricLoader.INSTANCE.getConfigDirectory(), "chatping/entries.json");
    public static ChatPingEntry[] entries;

    public static void init() {
        entries  = JsonUtil.fromJson(TypeToken.get(ChatPingEntry[].class), FILE, new ChatPingEntry[]{});
    }

    public static int remove(String id) {
        List<Integer> matches = new ArrayList<>();
        for (int index = 0; index < entries.length; index++) {
            if (entries[index].matchesId(id))
                matches.add(index);
        }
        if (matches.size() > 1) {
            return 0; // Too many
        }
        if (matches.size() < 1) {
            return -1; // No match
        }
        entries = ArrayUtils.remove(entries, matches.get(0));
        return Command.SINGLE_SUCCESS;
    }

    public static void clear() {
        entries = new ChatPingEntry[]{};
        JsonUtil.toJson(entries, TypeToken.get(ChatPingEntry[].class), FILE);
    }

    public static int addEntry(ChatPingEntry entry) {
        int hashcode = entry.hashCode();
        boolean inList = false;
        for (ChatPingEntry other : entries)
            inList |= hashcode == other.hashCode();
        if (inList)
            return -1;
        entries = ArrayUtils.add(entries, entry);
        JsonUtil.toJson(entries, TypeToken.get(ChatPingEntry[].class), FILE);
        return Command.SINGLE_SUCCESS;
    }


}
