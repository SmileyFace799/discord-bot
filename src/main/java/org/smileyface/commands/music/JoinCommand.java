package org.smileyface.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import org.smileyface.audio.LavaPlayerJdaWrapper;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.Checks;
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

    /**
     * Joins a member's voice channel.
     *
     * @param memberToJoin  The member to join
     * @param playerChannel The channel to make the music embed in
     * @throws CommandFailedException If the author is not connected to voice,
     *                                or if the bot is already connected somewhere
     */
    protected static void joinVoiceOfMember(Member memberToJoin, GuildMessageChannel playerChannel)
            throws CommandFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        Guild memberGuild = memberToJoin.getGuild();
        AudioManager audioManager = memberGuild.getAudioManager();
        Checks.botNotConnected(audioManager);

        AudioPlayer player = MusicManager.getInstance().createPlayer(playerChannel);
        audioManager.setSendingHandler(new LavaPlayerJdaWrapper(player));
        audioManager.openAudioConnection(audioChannel);
        MusicManager
                .getInstance()
                .getQueue(memberGuild.getIdLong())
                .getTrackQueueEmbed()
                .setLastCommand(memberToJoin, "Made the bot join a voice channel");
    }

    /**
     * Joins a member's voice channel, if not already connected.
     *
     * @param memberToJoin  The member to join
     * @param playerChannel The channel to make the music embed int
     * @throws CommandFailedException If the author is not connected to voice,
     *                                or if the bot is already in a different voice channel
     */
    protected static void joinIfNotConnected(Member memberToJoin, GuildMessageChannel playerChannel)
            throws CommandFailedException {
        try {
            Checks.botConnectedToAuthorVoice(Checks.authorInVoice(memberToJoin));
        } catch (CommandFailedException cfe) {
            joinVoiceOfMember(memberToJoin, playerChannel);
        }
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        joinVoiceOfMember(Objects.requireNonNull(event.getMember()), event.getGuildChannel());
        event.reply("Joined channel!").setEphemeral(true).queue();
    }
}
