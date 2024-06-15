package no.smileyface.discordbot.commands.misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Generic say command.
 */
public class SayAction extends BotAction<SayAction.SayKey> {
    private static class SayCommand extends ActionCommand<SayKey> {
        public SayCommand() {
            super(Commands
                    .slash("say", "Bot will say anything you want")
                    .addOption(OptionType.STRING, SayKey.STRING.str(),
                            "The string of text the bot should say", true)
            );
        }

        @Override
        public MultiTypeMap<SayKey> getSlashArgs(SlashCommandInteractionEvent event) {
            MultiTypeMap<SayKey> args = new MultiTypeMap<>();
            args.put(SayKey.STRING, event.getOption(
                    SayKey.STRING.str(),
                    OptionMapping::getAsString
            ));
            return args;
        }
    }

    /**
     * Makes the say action.
     */
    public SayAction() {
        super(new SayCommand());
    }

    @Override
    protected void execute(
            IReplyCallback event,
            MultiTypeMap<SayKey> args,
            InputRecord inputs
    ) {
        event.reply(args.get(SayKey.STRING, String.class)).queue();
    }

    /**
     * Keys for args map.
     */
    public enum SayKey implements ArgKey {
        STRING
    }
}
