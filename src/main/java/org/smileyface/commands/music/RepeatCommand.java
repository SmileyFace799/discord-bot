package org.smileyface.commands.music;

import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Changes the player's repeat mode.
 */
public class RepeatCommand extends BotCommand {
    /**
     * Makes the command.
     */
    public RepeatCommand() {
        super(
                Commands.slash("repeat", "Changes the repeat mode")
                        .addOption(OptionType.STRING, ArgKeys.REPEAT_MODE.toLowerCase(),
                                "The repeat mode to set. Must be \"song\", "
                                        + "\"queue\" or \"off", true)
                        .setGuildOnly(true),
                Set.of("rpt")
        );
    }

    @Override
    public MultiTypeMap<String> getArgs(SlashCommandInteractionEvent event) {
        MultiTypeMap<String> args = new MultiTypeMap<>();
        args.put(ArgKeys.REPEAT_MODE, event.getOption(ArgKeys.REPEAT_MODE.toLowerCase(),
                repeatStr -> {
                    TrackQueue.Repeat repeat;
                    try {
                        repeat = TrackQueue.Repeat.getRepeat(repeatStr.getAsString()
                                .replace("\"", ""));
                    } catch (IllegalArgumentException iae) {
                        repeat = null;
                    }
                    return repeat;
                }
        ));
        return args;
    }

    @Override
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.botConnectedToAuthorVoice(event);
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        TrackQueue.Repeat repeat = args.get(ArgKeys.REPEAT_MODE, TrackQueue.Repeat.class);
        if (repeat != null) {
            MusicManager.getInstance()
                    .getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
                    .setRepeat(repeat);
            event.reply("Set repeat mode to: " + repeat.getStr()).setEphemeral(true).queue();
        } else {
            event.reply("Unknown repeat mode. "
                            + "The valid modes are \"song\", \"queue\" or \"off\""
                    ).setEphemeral(true)
                    .queue();
        }
    }

    /**
     * Keys for args map.
     */
    public static class ArgKeys {
        public static final String REPEAT_MODE = "repeatMode";

        private ArgKeys() {
            throw new IllegalStateException("Utility class");
        }
    }
}
