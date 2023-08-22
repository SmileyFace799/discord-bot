package org.smileyface.commands.music;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import org.smileyface.audio.MusicManager;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.Category;

/**
 * Contains all music-related commands.
 */
public class Music extends Category {
    private static Music instance;

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static synchronized Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    private Music() {
        super(List.of(
                new JoinCommand(),
                new LeaveCommand(),
                new PlayCommand(),
                new SkipCommand(),
                new QueueCommand(),
                new ShowPlayerCommand()
        ));
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
            Checks.isPlaying(guild.getIdLong());
            MusicManager.getInstance().stop(guild.getIdLong());
        } catch (CommandFailedException cfe) {
            //Do nothing.
        }
    }
}
