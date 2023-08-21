package org.smileyface.commands.misc;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;

/**
 * Generic say command.
 */
public class SayCommand extends BotCommand {
    /**
     * Makes the say command.
     */
    public SayCommand() {
        super(Commands
                .slash("say", "Bot will say anything you want")
                .addOption(OptionType.STRING, "string",
                        "The string of text the bot should say", true));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        OptionMapping stringOption = event.getOption("string");
        Objects.requireNonNull(stringOption);
        event.reply(stringOption.getAsString()).queue();
    }
}
