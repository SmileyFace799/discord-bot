package org.smileyface.botlisteners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.smileyface.commands.BotCommand;
import org.smileyface.commands.CommandManager;

/**
 * Listens for slash commands.
 */
public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        BotCommand command = CommandManager.getInstance().getItem(event.getName());
        command.run(event, command.getArgs(event));
    }
}
