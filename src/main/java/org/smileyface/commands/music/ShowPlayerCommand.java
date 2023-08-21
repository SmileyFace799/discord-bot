package org.smileyface.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

/**
 * Shows the music player in a new message.
 */
public class ShowPlayerCommand extends BotCommand {

    /**
     * Makes the show player command.
     */
    public ShowPlayerCommand() {
        super(Commands
                .slash("showplayer", "Shows the audio player")
                .setGuildOnly(true)
        );
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        Checks.botConnectedToAuthorVoice(
                Checks.authorInVoice(
                        Objects.requireNonNull(event.getMember())
                )
        );
        MusicManager
                .getInstance()
                .getQueue(Objects.requireNonNull(event.getGuild()).getIdLong())
                .getTrackQueueEmbed()
                .showPlayer();
        event.reply("Player shown!").setEphemeral(true).queue();
    }
}
