package org.smileyface.listeners;

import java.util.Map;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.smileyface.commands.AllCommands;
import org.smileyface.commands.BotCommand;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Map<String, BotCommand> commandMap = AllCommands.get();
        String commandName = event.getName();
        if (commandMap.containsKey(commandName)) {
            commandMap.get(commandName).run(event);
        } else {
            event.reply(
                    "Command not implemented (This is most likely a bug, please report this)"
            ).setEphemeral(true).queue();
        }
    }
}
