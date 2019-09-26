package us.kosdt.chatping.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import us.kosdt.chatping.data.ChatPingData;
import us.kosdt.chatping.data.ChatPingEntry;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static io.github.cottonmc.clientcommands.ArgumentBuilders.literal;
import static io.github.cottonmc.clientcommands.ArgumentBuilders.argument;
import static net.minecraft.command.arguments.IdentifierArgumentType.getIdentifier;
import static net.minecraft.command.arguments.IdentifierArgumentType.identifier;

public class ChatPingCommand implements ClientCommandPlugin {

    private static final SuggestionProvider<CottonClientCommandSource> AVAILABLE_SOUNDS = (commandContext_1, suggestionsBuilder_1) -> {
        return CommandSource.suggestIdentifiers((Iterable)((CommandSource)commandContext_1.getSource()).getSoundIds(), suggestionsBuilder_1);
    };
    private static final String DEFAULT_PING_SOUND = SoundEvents.ENTITY_PLAYER_LEVELUP.getId().toString();

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(
            literal("chatping")
                .then(literal("delete")
                    .then(argument("id", string())
                        .executes(ctx -> deletePing(ctx.getSource(), getString(ctx, "id")))))
                .then(literal("clear")
                    .executes(ctx -> clearPings(ctx.getSource())))
                .then(literal("list")
                    .executes(ctx -> pingList(ctx.getSource())))
                .then(literal("register")
                    .then(argument("pattern", string())
                        .then(argument("regex", bool())
                            .then(argument("sound", greedyString()).suggests(AVAILABLE_SOUNDS)
                                .executes(ctx -> registerPing(ctx.getSource(), getString(ctx, "pattern"), getBool(ctx, "regex"), getString(ctx, "sound"))))
                            .executes(ctx -> registerPing(ctx.getSource(), getString(ctx, "pattern"), getBool(ctx, "regex"), DEFAULT_PING_SOUND)))
                        .executes(ctx -> registerPing(ctx.getSource(), getString(ctx, "pattern"), false, DEFAULT_PING_SOUND))))
        );
    }

    private int deletePing(CottonClientCommandSource commandSource, String id) {
        int exitCode = ChatPingData.remove(id);
        if (exitCode == 0)
            commandSource.sendFeedback(new LiteralText("There are multiple matches. Include more characters from the id"));
        if (exitCode == -1)
            commandSource.sendError(new LiteralText("No matching pings. Please try again"));
        if (exitCode == Command.SINGLE_SUCCESS)
            commandSource.sendFeedback(new LiteralText("Successfully deleted ping"));
        return Command.SINGLE_SUCCESS;
    }

    private int pingList(CottonClientCommandSource commandSource) {
        if (ChatPingData.entries.length < 1) {
            commandSource.sendFeedback(new LiteralText("Ping list is empty"));
            return Command.SINGLE_SUCCESS;
        }
        for (ChatPingEntry entry : ChatPingData.entries) {
            String pattern = entry.pattern;
            boolean regex = entry.regex;
            String sound = entry.sound;
            char sep = regex ? '/' : '"';
            Text patternText = new LiteralText(entry.getId() + " " + sep + pattern + sep + " - " + sound);
            commandSource.sendFeedback(patternText);
        }
        return Command.SINGLE_SUCCESS;
    }

    private int registerPing(CottonClientCommandSource commandSource, String pattern, boolean regex, String sound) {
        int exitCode = ChatPingData.addEntry(new ChatPingEntry(pattern, regex, sound));
        if (exitCode == -1) {
            commandSource.sendFeedback(new LiteralText("This entry already exists."));
            return Command.SINGLE_SUCCESS;
        }
        char sep = regex ? '/' : '"';
        commandSource.sendFeedback(new LiteralText("Registered ping for " + sep + pattern + sep + " with sound " + sound));
        return Command.SINGLE_SUCCESS;
    }

    private int clearPings(CottonClientCommandSource commandSource) {
        ChatPingData.clear();
        commandSource.sendFeedback(new LiteralText("Cleared all pings"));
        return Command.SINGLE_SUCCESS;
    }

}
