package org.smileyface.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.checks.Checks;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

public class SkipCommand extends BotCommand {
    public SkipCommand() {
        super(Commands
                .slash("skip", "Skips the current song")
                .setGuildOnly(true)
        );
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        Checks.botConnectedToAuthorVoice(Checks.authorInVoice(
                Objects.requireNonNull(event.getMember())));

        Checks.isPlaying(Objects.requireNonNull(event.getGuild()).getId()).skip();
        event.reply("Song skipped!").queue();
    }
}
