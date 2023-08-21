package org.smileyface.botlisteners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.smileyface.checks.CommandFailedException;
import org.smileyface.commands.CommandManager;

/**
 * Listens for slash commands.
 */
public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {
            CommandManager.getInstance().getItem(event.getName()).run(event);
        } catch (CommandFailedException cfe) {
            if (event.isAcknowledged()) {
                event.getHook().sendMessage(cfe.getMessage()).queue();
            } else {
                event.reply(cfe.getMessage()).setEphemeral(true).queue();
            }
        }
    }
}
