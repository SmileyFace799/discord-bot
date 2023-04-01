package org.smileyface.commands.music;

import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

public class LeaveCommand extends BotCommand {
    public LeaveCommand() {
        super(Commands
                        .slash("leave", "Makes the bot leave the voice channel you're in")
                        .setGuildOnly(true),
                Set.of("stop"));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(
                Objects.requireNonNull(event.getMember()));
        Checks.botConnectedToAuthorVoice(audioChannel);

        Music.leaveVoiceIfConnected(Objects.requireNonNull(event.getGuild()));
        event.reply("Left channel!").setEphemeral(true).queue();
    }
}
