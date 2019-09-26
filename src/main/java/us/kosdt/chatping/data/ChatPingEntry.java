package us.kosdt.chatping.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatPingEntry {

    public String pattern = "";
    public boolean regex = false;
    public String sound = "";

    public ChatPingEntry(String pattern, boolean regex, String sound) {
        this.pattern = pattern;
        this.regex = regex;
        this.sound = sound;
    }

    public String getId() {
        return Integer.toHexString(hashCode());
    }

    public boolean matchesId(String otherId) {
        String id = getId();
        return StringUtils.startsWith(id, otherId);
    }

    public void tryPlaySound() {
        System.out.println("Tried to play sound from ping: " + toString());
        Identifier identifier = new Identifier(sound);
        if (identifier.getPath().isEmpty()) identifier = new Identifier("minecraft", identifier.getNamespace());
        SoundEvent event = Registry.SOUND_EVENT.get(identifier);
        if (event != null) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(event, 1.0f));
        } else { // Fallback
            System.out.println("Sound not found. Fallback sound played");
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f));
        }
    }

    public void checkMatch(String message) {
        if (regex) {
            Pattern regexPattern = Pattern.compile(pattern);
            Matcher matcher = regexPattern.matcher(message);
            if (matcher.find()) {
                tryPlaySound();
            }
        } else {
            if (message.contains(pattern)) {
                tryPlaySound();
            }
        }
    }

    @Override
    public String toString() {
        char sep = regex ? '/' : '"';
        return sep + pattern + sep + " : " + sound;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (pattern.isEmpty() ? 0 : pattern.hashCode());
        hash = 31 * hash + (regex ? 41 : 47);
        hash = 31 * hash + (sound.isEmpty() ? 0 : sound.hashCode());
        return hash;
    }

}
