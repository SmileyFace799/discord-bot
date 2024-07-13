package no.smileyface.discordbot.model.apis;

import api.deezer.DeezerApi;
import no.smileyface.discordbot.model.apis.generic.ApiConsumer;
import no.smileyface.discordbot.model.apis.generic.ApiContainer;

/**
 * Container for the Deezer API.
 */
public class DeezerContainer implements ApiContainer<DeezerApi> {
	private final DeezerApi api;

	public DeezerContainer() {
		this.api = new DeezerApi("");
	}

	@Override
	public void withApi(ApiConsumer<DeezerApi> apiConsumer) throws ApiException {
		apiConsumer.accept(api);
	}
}
