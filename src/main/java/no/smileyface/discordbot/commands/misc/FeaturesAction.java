package no.smileyface.discordbot.commands.misc;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.commands.SpotifyManager;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Shows the available features of the bot.
 */
public class FeaturesAction extends BotAction<BotAction.ArgKey> {
    /**
     * Makes he features action.
     */
    public FeaturesAction() {
        super(new ActionCommand<>(Commands.slash("features",
                "Shows what optional features the bot currently has"
        )));
    }

    private String boolToString(boolean bool) {
        return bool ? "Yes" : "No";
    }

    @Override
    protected void execute(
            IReplyCallback event,
            MultiTypeMap<ArgKey> args,
            InputRecord inputs
    ) {
        String msg = "All optional features for this bot:" + "\n - Can play Spotify links? "
                + boolToString(SpotifyManager.getInstance().getApi() != null)
                + "\n - Can report issues? "
                + boolToString(event.getJDA().getSelfUser().getIdLong() == 651563251896942602L);

        event.reply(msg).setEphemeral(true).queue();
    }
}
