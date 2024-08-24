package no.smileyface.discordbot.actions.misc;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.misc.commands.FeaturesCommand;
import no.smileyface.discordbot.model.apis.DeezerContainer;
import no.smileyface.discordbot.model.apis.SpotifyContainer;
import no.smileyface.discordbot.model.querying.QueryParser;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

/**
 * Shows the available features of the bot.
 */
public class FeaturesAction extends BotAction<GenericBotAction.ArgKey> {
	private final QueryParser queryParser;

	/**
	 * Makes he features action.
	 */
	public FeaturesAction(ActionManager manager, QueryParser queryParser) {
		super(manager, new FeaturesCommand());
		this.queryParser = queryParser;
	}

	private String boolToString(boolean bool) {
		return bool ? "Yes" : "No";
	}

	@Override
	protected void execute(
			IReplyCallback event,
			Node<ArgKey, Object> args
	) {
		String msg = String.format("""
						All optional features for this bot:
						- Can play Spotify links? %s
						- Can play Deezer links? %s
						- Can fetch lyrics? %s
						- Can report issues? %s""",
				boolToString(queryParser.isSupported(SpotifyContainer.class)),
				boolToString(queryParser.isSupported(DeezerContainer.class)),
				boolToString(true),
				boolToString(event.getJDA().getSelfUser().getIdLong() == 651563251896942602L)
		);

		event.reply(msg).setEphemeral(true).queue();
	}
}
