package org.smileyface.commands;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.smileyface.checks.ChecksFailedException;
import org.smileyface.misc.MultiTypeMap;

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
                public MultiTypeMap<String> getArgs(SlashCommandInteractionEvent event) {
                    return BotCommand.this.getArgs(event);
                }

                @Override
                protected void runChecks(IReplyCallback event) throws ChecksFailedException {
                    BotCommand.this.runChecks(event);
                }

                @Override
                protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
                    BotCommand.this.execute(event, args);
                }
            });
        }
        return variations;
    }

    /**
     * Commands can override this to organize slash command options into a map.
     * <p>
     * This map can be passed alongside the event to {@link #execute(IReplyCallback, MultiTypeMap)},
     * which can otherwise not access slash command arguments,
     * due to maintaining compatibility with buttons & modals.
     * </p><p>
     * This returns an empty map by default.
     * </p>
     *
     * @param event The command event containing contextual information on the executed command
     * @return Slash command arguments, organized into a map.
     *         There is no guarantee that this map is modifiable
     */
    public MultiTypeMap<String> getArgs(SlashCommandInteractionEvent event) {
        return new MultiTypeMap<>();
    }

    /**
     * Checks if the command can be executed in the context it was invoked in.
     * If calling this method does not throw an exception, the command can be invoked safely.
     *
     * @param event The {@link IReplyCallback} containing the command's invocation context
     * @throws ChecksFailedException If the command cannot be executed in the invoked context
     */
    protected void runChecks(IReplyCallback event) throws ChecksFailedException {
        //Does nothing by default
    }

    /**
     * The code to execute when the command is run.
     *
     * @param event The command event containing contextual information on the executed command
     */
    protected abstract void execute(IReplyCallback event, MultiTypeMap<String> args);

    /**
     * Runs the command.
     * <p>
     *     Running the command consists of 2 steps: Checking & Executing.
     *     Checking checks if the command can be executed in the invoked context,
     *     and Executing executes the command if the checking process did not yield any exceptions.
     * </p>
     *
     * @param event The {@link IReplyCallback} containing the command's invocation context
     * @param args Any additional arguments passed by the user in the invocation process
     */
    public void run(IReplyCallback event, MultiTypeMap<String> args) {
        try {
            runChecks(event);
            execute(event, args);
        } catch (ChecksFailedException cfe) {
            if (event.isAcknowledged()) {
                event.getHook().sendMessage(cfe.getMessage()).queue();
            } else {
                event.reply(cfe.getMessage()).setEphemeral(true).queue();
            }
        }
    }

    public void run(IReplyCallback event) {
        run(event, new MultiTypeMap<>());
    }
}
