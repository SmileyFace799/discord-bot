package org.smileyface.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Skips the current song.
 */
public class SkipCommand extends BotCommand {
    /**
     * Makes the skip command.
     */
    public SkipCommand() {
        super(Commands
                .slash("skip", "Skips the current song")
                .setGuildOnly(true)
                .addOption(OptionType.INTEGER, ArgKeys.AMOUNT,
                        "the amount of songs to skip. "
                                + "If invalid, the closest valid value is used  (Default: 1)")
        );
    }

    @Override
    public MultiTypeMap<String> getArgs(SlashCommandInteractionEvent event) {
        MultiTypeMap<String> args = new MultiTypeMap<>();
        args.put(ArgKeys.AMOUNT, event.getOption(ArgKeys.AMOUNT, 1, OptionMapping::getAsInt));
        return args;
    }

    @Override
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        Checks.botConnectedToAuthorVoice(event);
        Checks.isPlaying(event);
    }

    @Override
    public void execute(IReplyCallback event, MultiTypeMap<String> args) {
        Member author = Objects.requireNonNull(event.getMember());
        Guild guild = author.getGuild();
        TrackQueue queue = MusicManager.getInstance().getQueue(guild.getIdLong());
        int amount = Math.max(1, Math.min(queue.getTracks().size() + 1,
                args.get(ArgKeys.AMOUNT, Integer.class)
        ));
        queue.skip(amount);
        String replyMessage = "Skipped %s song".formatted(amount);
        if (amount != 1) {
            replyMessage += "s";
        }
        event.reply(replyMessage).setEphemeral(true).queue();
        queue.getTrackQueueEmbed().setLastCommand(author, replyMessage);
    }

    /**
     * Keys for args map.
     */
    public static class ArgKeys {
        public static final String AMOUNT = "amount";

        private ArgKeys() {
            throw new IllegalStateException("Utility class");
        }
    }
}
