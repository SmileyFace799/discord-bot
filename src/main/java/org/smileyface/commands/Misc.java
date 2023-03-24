package org.smileyface.commands;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Contains all miscellaneous commands that don't fit anywhere else.
 */
public class Misc {
    private Misc() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, BotCommand> getCommands() {
        return COMMANDS;
    }

    private static final Map<String, BotCommand> COMMANDS = Stream.of(
            new BotCommand(Commands.slash("ping", "Bot answers with \"pong!\"")) {
                @Override
                public void run(SlashCommandInteractionEvent event) {
                    event.reply("pong!").queue();
                }
            },
            new BotCommand(Commands
                    .slash("say", "Bot will say anything you want")
                    .addOption(OptionType.STRING, "string",
                            "The string of text the bot should say", true)
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event) {
                    OptionMapping stringOption = event.getOption("string");
                    Objects.requireNonNull(stringOption);
                    event.reply(stringOption.getAsString()).queue();
                }
            }).collect(Collectors.toMap(
                    command -> command.getData().getName(),
                    command -> command
            )
    );
}
