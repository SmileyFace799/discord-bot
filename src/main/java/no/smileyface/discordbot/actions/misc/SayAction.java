package no.smileyface.discordbot.actions.misc;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.misc.commands.SayCommand;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;

/**
 * Generic say command.
 */
public class SayAction extends BotAction<SayAction.Key> {
    /**
     * Makes the say action.
     */
    public SayAction(ActionManager manager) {
        super(manager, new SayCommand());
    }

    @Override
    protected void execute(
            IReplyCallback event,
            Node<Key, Object> args
    ) {
        event.reply(args.getValue(Key.STRING, String.class)).queue();
    }

    /**
     * Keys for args map.
     */
    public enum Key implements ArgKey {
        STRING
    }
}
