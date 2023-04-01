package org.smileyface.commands.misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;
import org.smileyface.commands.SpotifyManager;

public class FeaturesCommand extends BotCommand {
    public FeaturesCommand() {
        super(Commands.slash("features",
                "Shows what optional features the bot currently has"));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        StringBuilder msg = new StringBuilder("All optional features for this bot:");

        msg.append("\n - Can play Spotify links? ");
        msg.append(SpotifyManager.getInstance().getApi() != null ? "Yes" : "No");

        event.reply(msg.toString()).setEphemeral(true).queue();
    }
}
