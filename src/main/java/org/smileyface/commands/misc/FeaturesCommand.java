package org.smileyface.commands.misc;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;
import org.smileyface.commands.SpotifyManager;
import org.smileyface.misc.MultiTypeMap;

/**
 * Shows the available features of the bot.
 */
public class FeaturesCommand extends BotCommand {
    public FeaturesCommand() {
        super(Commands.slash("features",
                "Shows what optional features the bot currently has"));
    }

    private String boolToString(boolean bool) {
        return bool ? "Yes" : "No";
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {

        String msg = "All optional features for this bot:" + "\n - Can play Spotify links? "
                + boolToString(SpotifyManager.getInstance().getApi() != null)
                + "\n - Can report issues? "
                + boolToString(event.getJDA().getSelfUser().getIdLong() == 651563251896942602L);

        event.reply(msg).setEphemeral(true).queue();
    }
}
