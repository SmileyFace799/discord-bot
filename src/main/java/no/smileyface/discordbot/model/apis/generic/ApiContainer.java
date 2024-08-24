package no.smileyface.discordbot.model.apis.generic;

import no.smileyface.discordbot.model.apis.ApiException;
import no.smileyface.discordbot.model.apis.UnsupportedApiException;

/**
 * A generic container for any optional external APIs, that standardize access to them.
 *
 * @param <A> The type of the object that serves as an access point to the API
 */
public interface ApiContainer<A> {

	/**
	 * Does something with the API.
	 *
	 * @param apiConsumer A consumer that does something with the API
	 * @throws ApiException            If the API is supported,
	 *                                 but an exception is encountered during execution
	 * @throws UnsupportedApiException If the API is not supported
	 */
	void withApi(ApiConsumer<A> apiConsumer) throws ApiException, UnsupportedApiException;

	/**
	 * If the API is supported or not. The default implementation calls
	 * {@link #withApi(ApiConsumer)} with an empty consumer, returning {@code true} if it succeeds,
	 * and {@code false} if it throws an {@link ApiException} or an {@link UnsupportedApiException}.
	 * While this brute-force approach works,
	 * a more graceful implementation is preferred if possible.
	 *
	 * @return If the API is supported or not
	 */
	default boolean isSupported() {
		boolean supported;
		try {
			withApi(api -> {});
			supported = true;
		} catch (ApiException | UnsupportedApiException ignored) {
			supported = false;
		}
		return supported;
	}
}
