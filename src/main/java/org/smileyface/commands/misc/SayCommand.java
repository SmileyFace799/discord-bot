package org.smileyface.commands.misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

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
                .addOption(OptionType.STRING, ArgKeys.STRING,
                        "The string of text the bot should say", true));
    }

    @Override
    public MultiTypeMap<String> getArgs(SlashCommandInteractionEvent event) {
        MultiTypeMap<String> args = new MultiTypeMap<>();
        args.put(ArgKeys.STRING, event.getOption(ArgKeys.STRING, OptionMapping::getAsString));
        return args;
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        event.reply(args.get(ArgKeys.STRING, String.class)).queue();
    }

    /**
     * Keys for args map.
     */
    public static class ArgKeys {
        public static final String STRING = "string";

        private ArgKeys() {
            throw new IllegalStateException("Utility class");
        }
    }
}
