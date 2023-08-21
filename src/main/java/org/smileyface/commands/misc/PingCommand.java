package org.smileyface.commands.misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;

/**
 * Basic ping command.
 */
public class PingCommand extends BotCommand {
    public PingCommand() {
        super(Commands.slash("ping", "Bot answers with \"pong!\""));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        event.reply("pong!").queue();
    }
}
