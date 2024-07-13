package no.smileyface.discordbot.model.apis.generic;

import no.smileyface.discordbot.model.apis.ApiException;

/**
 * {@link java.util.function.Consumer Consumer} that can throw
 * an API exception in its {@link #accept(Object)} method.
 *
 * @param <T> The type to consume
 */
public interface ApiConsumer<T> {
	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t The input argument.
	 *          This should always be an object representing an API for semantically correct usage
	 * @throws ApiException If this operation results in one
	 */
	void accept(T t) throws ApiException;
}
