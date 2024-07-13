package no.smileyface.discordbot.model.querying;

/**
 * A query that can be directly queued by the bot.
 */
public class Query {
	private final String str;

	public Query(String str) {
		this.str = str;
	}

	public String str() {
		return str;
	}
}
