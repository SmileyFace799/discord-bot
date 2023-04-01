package org.smileyface.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.BotCommand;

public class JoinCommand extends BotCommand {
    public JoinCommand() {
        super(Commands
                .slash("join", "Joins a voice channel")
                .setGuildOnly(true)
        );
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws CommandFailedException {
        Music.joinVoiceOfMember(Objects.requireNonNull(event.getMember()),
                event.getGuildChannel());
        event.reply("Joined channel!").setEphemeral(true).queue();
    }
}
