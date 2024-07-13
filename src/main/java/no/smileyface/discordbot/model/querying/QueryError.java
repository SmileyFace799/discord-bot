package no.smileyface.discordbot.model.querying;

/**
 * A query that wasn't able to be parsed.
 */
public class QueryError extends Query {
	private final String errorMessage;
	
	public QueryError(String str, String errorMessage) {
		super(str);
		this.errorMessage = errorMessage;
	}

	public String errorMessage() {
		return errorMessage;
	}
}
