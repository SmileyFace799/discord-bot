package org.smileyface.commands.feedback;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;

/**
 * Shows known issues with the bot.
 */
public class KnownIssuesCommand extends BotCommand {
    public KnownIssuesCommand() {
        super(Commands.slash("knownissues",
                "A list of known issues with the bot that will not be fixed"));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        event.reply("""
                These are all the known issues with the bot that will not be fixed.
                Please do not report any of these issues, I cannot fix them.

                **Play command with a Spotify link is queuing the wrong song**
                """
                + "  Since Spotify is a paid service, "
                + "it means the bot cannot play music directly from there. "
                + "Instead, when a Spotify link is provided, "
                + "the bot will find the same song on a different platform (such as YouTube), "
                + "and play it from there. However, in very rare cases, "
                + "it might find a different song instead, "
                + "and there isn't much I can do to fix this."
        ).setEphemeral(true).queue();
    }
}
