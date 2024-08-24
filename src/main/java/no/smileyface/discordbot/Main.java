package no.smileyface.discordbot;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import no.smileyface.discordbotframework.DiscordBot;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.files.properties.PropertyLoadException;
import no.smileyface.discordbotframework.files.properties.PropertyLoader;

/**
 * Main class for starting the bot.
 */
public class Main {
	/**
	 * Starts the bot.
	 *
	 * @param args java args. This is ignored
	 * @throws NoSuchFileException  If the token file for the active bot is not found
	 * @throws InterruptedException If the bot is interrupted while starting
	 */
	public static void main(
			String[] args
	) throws IOException, InterruptedException, PropertyLoadException {
		try {
			Files.createDirectory(Path.of("botFiles"));

		} catch (FileAlreadyExistsException ignored) {
			// Folder exists, all good
		}
		Node<String, String> properties = PropertyLoader.loadProperties();
		Properties.initialize(properties);
		DiscordBot bot = new DiscordBot(
				new InputListener(new ActionCreator()),
				properties,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.DIRECT_MESSAGES
		);
		bot.getJda().getPresence().setActivity(Activity.playing("/play"));
	}
}
