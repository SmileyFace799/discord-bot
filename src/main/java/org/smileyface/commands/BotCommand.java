package org.smileyface.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.smileyface.checks.CheckFailedException;

/**
 * Represents a basic bot command. All bot commands are slash commands.
 */
public abstract class BotCommand {
    private final SlashCommandData data;

    /**
     * Makes a bot command.
     *
     * @param data The command data that specifies how it should be implemented into the bot.
     */
    protected BotCommand(SlashCommandData data) {
        this.data = data;
    }

    public SlashCommandData getData() {
        return data;
    }

    /**
     * The code to execute when the command is ran.
     *
     * @param event The command event containing contextual information on the executed command.
     * @throws CheckFailedException If the command is ran in an invalid context.
     */
    public abstract void run(SlashCommandInteractionEvent event) throws CheckFailedException;
}
