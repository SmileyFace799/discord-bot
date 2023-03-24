package org.smileyface.commands;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import org.smileyface.checks.CheckFailedException;
import org.smileyface.checks.Checks;

/**
 * Contains all music-related commands.
 */
public class Music {
    private Music() {
        throw new IllegalStateException("Utility clas");
    }

    public static Map<String, BotCommand> getCommands() {
        return COMMANDS;
    }

    private static AudioManager joinVoiceOfMember(Member memberToJoin) throws CheckFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        AudioManager audioManager = memberToJoin.getGuild().getAudioManager();
        Checks.botNotConnected(audioManager);
        audioManager.openAudioConnection(audioChannel);
        return audioManager;
    }

    private static AudioManager joinIfNotConnected(Member memberToJoin)
            throws CheckFailedException {
        AudioChannel audioChannel = Checks.authorInVoice(memberToJoin);
        AudioManager audioManager;
        try {
            audioManager = joinVoiceOfMember(memberToJoin);
        } catch (CheckFailedException cfe) {
            Checks.botConnectedToAuthorVoice(audioChannel);
            audioManager = audioChannel.getGuild().getAudioManager();
        }
        return audioManager;
    }

    private static final Map<String, BotCommand> COMMANDS = Stream.of(
            new BotCommand(Commands
                    .slash("join", "Joins a voice channel")
                    .setGuildOnly(true)
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CheckFailedException {
                    joinVoiceOfMember(Objects.requireNonNull(event.getMember()));
                    event.reply("Joined channel!").setEphemeral(true).queue();
                }
            },

            new BotCommand(Commands
                    .slash("leave", "Leaves a voice channel")
                    .setGuildOnly(true)
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CheckFailedException {
                    AudioChannel audioChannel = Checks.authorInVoice(
                            Objects.requireNonNull(event.getMember()));
                    Checks.botConnectedToAuthorVoice(audioChannel);

                    Objects.requireNonNull(event.getGuild())
                            .getAudioManager()
                            .closeAudioConnection();
                    event.reply("Left channel!").setEphemeral(true).queue();
                }
            },

            new BotCommand(Commands
                    .slash("play", "Plays a song")
                    .setGuildOnly(true)
                    .addOption(OptionType.STRING, "input",
                            "A search or URL for the song to play. "
                                    + "Search is done through YouTube", true)
                    .addOption(OptionType.BOOLEAN, "songsearch",
                            "If the YouTube search should search for songs only. "
                                    + "Defaults to false, ignored if input is a URL")
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) throws CheckFailedException {
                    AudioManager audioManager = joinIfNotConnected(
                            Objects.requireNonNull(event.getMember()));

                    event.reply("Working as intended :D").setEphemeral(true).queue();
                }
            }).collect(Collectors.toMap(
                    command -> command.getData().getName(),
                    command -> command
            )
    );
}
