package org.smileyface.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.smileyface.audio.LavaPlayerJdaWrapper;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.Category;

/**
 * Contains all music-related commands.
 */
public class Music extends Category {
    private static Music instance;

    public static synchronized Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    public Music() {
        super(List.of(
                new JoinCommand(),
                new LeaveCommand(),
                new PlayCommand(),
                new SkipCommand(),
                new QueueCommand()
        ));
    }

    protected static void joinVoiceOfMember(Member memberToJoin, GuildMessageChannel playerChannel)
            throws CommandFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        Guild memberGuild = memberToJoin.getGuild();
        AudioManager audioManager = memberGuild.getAudioManager();
        Checks.botNotConnected(audioManager);

        AudioPlayer player = MusicManager.getInstance().createPlayer(playerChannel);
        audioManager.setSendingHandler(new LavaPlayerJdaWrapper(player));
        audioManager.openAudioConnection(audioChannel);
    }

    protected static void joinIfNotConnected(Member memberToJoin, GuildMessageChannel playerChannel)
            throws CommandFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        try {
            joinVoiceOfMember(memberToJoin, playerChannel);
        } catch (CommandFailedException cfe) {
            Checks.botConnectedToAuthorVoice(audioChannel);
            audioChannel.getGuild().getAudioManager();
        }
    }

    /**
     * Leaves voice if the bot is in one.
     *
     * @param guild The guild where the bot should leave voice.
     */
    public static void leaveVoiceIfConnected(Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            guild.getAudioManager().closeAudioConnection();
        }
        try {
            Checks.isPlaying(guild.getId());
            MusicManager.getInstance().stop(guild.getId());
        } catch (CommandFailedException cfe) {
            //Do nothing.
        }
    }
}
