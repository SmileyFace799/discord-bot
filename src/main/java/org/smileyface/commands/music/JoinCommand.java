package org.smileyface.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

/**
 * Makes the bot join the user's current voice channel.
 */
public class JoinCommand extends BotCommand {

    /**
     * Makes the join command.
     */
    public JoinCommand() {
        super(Commands
                .slash("join", "Joins a voice channel")
                .setGuildOnly(true)
        );
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        Member author = Objects.requireNonNull(event.getMember());
        Music.joinVoiceOfMember(author,
                event.getGuildChannel());
        MusicManager
                .getInstance()
                .getQueue(author.getGuild().getIdLong())
                .getTrackQueueEmbed()
                .setLastCommand(author, "Made the bot join a voice channel");
        event.reply("Joined channel!").setEphemeral(true).queue();
    }
}
