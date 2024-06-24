package no.smileyface.discordbot.files;

import java.io.IOException;
import no.smileyface.discordbot.files.generic.LongCollectionFileInterface;

/**
 * File interface for announcement whitelist file.
 */
public class AnnouncementWhitelist extends LongCollectionFileInterface {
	private static AnnouncementWhitelist instance;

	protected AnnouncementWhitelist() throws IOException {
		super("announcementWhitelist.bot");
	}

	/**
	 * Singleton.
	 *
	 * @return Instance
	 * @throws IOException If the instance could not be created due to an I/O Exception
	 */
	public static synchronized AnnouncementWhitelist getInstance() throws IOException {
		if (instance == null) {
			instance = new AnnouncementWhitelist();
		}
		return instance;
	}
}
