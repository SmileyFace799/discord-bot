package no.smileyface.discordbot.actions.misc;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.misc.commands.PingCommand;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

/**
 * Basic ping command.
 */
public class PingAction extends BotAction<GenericBotAction.ArgKey> {
    /**
     * Makes the ping action.
     */
    public PingAction(ActionManager manager) {
        super(manager, new PingCommand());
    }

    @Override
    protected void execute(
            IReplyCallback event,
            Node<ArgKey, Object> args
    ) {
        event.reply("pong!").queue();
    }
}
