package org.smileyface.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.TrackQueue;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

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
                .addOption(OptionType.INTEGER, "amount",
                        "the amount of songs to skip. "
                                + "If invalid, the closest valid value is used  (Default: 1)")
        );
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        Member author = Objects.requireNonNull(event.getMember());
        Checks.botConnectedToAuthorVoice(Checks.authorInVoice(author));


        Guild guild = author.getGuild();
        TrackQueue queue = Checks.isPlaying(guild.getIdLong());
        OptionMapping amountOption = event.getOption("amount");
        int amount = amountOption == null ? 1
                : Math.max(1, Math.min(queue.getTracks().size() + 1, amountOption.getAsInt()));
        queue.skip(amount);
        String replyMessage = "Skipped %s song".formatted(amount);
        if (amount != 1) {
            replyMessage += "s";
        }
        event.reply(replyMessage).setEphemeral(true).queue();
        queue.getTrackQueueEmbed().setLastCommand(author, replyMessage);
    }
}
