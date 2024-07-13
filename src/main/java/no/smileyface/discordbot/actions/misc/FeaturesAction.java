package no.smileyface.discordbot.actions.misc;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.model.apis.DeezerContainer;
import no.smileyface.discordbot.model.apis.SpotifyContainer;
import no.smileyface.discordbot.model.querying.QueryParser;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Shows the available features of the bot.
 */
public class FeaturesAction extends BotAction<BotAction.ArgKey> {
	private final QueryParser queryParser;

	/**
	 * Makes he features action.
	 */
	public FeaturesAction(QueryParser queryParser) {
		super(new ActionCommand<>(Commands.slash("features",
				"Shows what optional features the bot currently has"
		)));
		this.queryParser = queryParser;
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
		String msg = String.format("""
						All optional features for this bot:
						- Can play Spotify links? %s
						- Can play Deezer links? %s
						- Can report issues? %s""",
				boolToString(queryParser.isSupported(SpotifyContainer.class)),
				boolToString(queryParser.isSupported(DeezerContainer.class)),
				boolToString(event.getJDA().getSelfUser().getIdLong() == 651563251896942602L)
		);

		event.reply(msg).setEphemeral(true).queue();
	}
}
