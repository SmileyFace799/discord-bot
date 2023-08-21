package org.smileyface.commands;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.smileyface.checks.CommandFailedException;

/**
 * Represents a basic bot command. All bot commands are slash commands.
 */
public abstract class BotCommand {
    private final SlashCommandData data;
    private final Collection<String> nicknames;

    /**
     * Makes a bot command.
     *
     * @param data The command data that specifies how it should be implemented into the bot.
     */
    protected BotCommand(SlashCommandData data) {
        this(data, null);
    }

    /**
     * Makes a bot command.
     *
     * @param data      The command data that specifies how it should be implemented into the bot.
     * @param nicknames Alternative nicknames for the command.
     */
    protected BotCommand(SlashCommandData data, Collection<String> nicknames) {
        this.data = data;
        this.nicknames = nicknames != null ? nicknames : Set.of();
    }

    public SlashCommandData getData() {
        return data;
    }

    /**
     * Get all variants of the command.
     * These variants will be identical to the original command, except the name.
     *
     * @return A list of command variants, one for each nickname in the `nicknames`-collection
     *         passed to the constructor.
     *         The returned collection will also include this command itself
     */
    public Collection<BotCommand> getAllVariants() {
        Collection<BotCommand> variations = new HashSet<>();
        variations.add(this);
        for (String nickname : nicknames) {
            SlashCommandData commandData = getData();
            variations.add(new BotCommand(Commands
                    .slash(nickname, "Shortcut for /" + commandData.getName())
                    .addOptions(commandData.getOptions())
                    .setGuildOnly(commandData.isGuildOnly())
                    .setDefaultPermissions(commandData.getDefaultPermissions())
                    .setNSFW(commandData.isNSFW())
            ) {
                @Override
                public void run(SlashCommandInteractionEvent event)
                        throws CommandFailedException {
                    BotCommand.this.run(event);
                }
            });
        }
        return variations;
    }

    /**
     * The code to execute when the command is ran.
     *
     * @param event The command event containing contextual information on the executed command.
     * @throws CommandFailedException If the command is ran in an invalid context.
     */
    public abstract void run(SlashCommandInteractionEvent event) throws CommandFailedException;
}
