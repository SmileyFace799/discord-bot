package no.smileyface.discordbot.actions.misc;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Basic ping command.
 */
public class PingAction extends BotAction<BotAction.ArgKey> {
    /**
     * Makes the ping action.
     */
    public PingAction() {
        super(new ActionCommand<>(Commands.slash(
                "ping", "Bot answers with \"pong!\""
        )));
    }

    @Override
    protected void execute(
            IReplyCallback event,
            MultiTypeMap<ArgKey> args,
            InputRecord inputs
    ) {
        event.reply("pong!").queue();
    }
}
