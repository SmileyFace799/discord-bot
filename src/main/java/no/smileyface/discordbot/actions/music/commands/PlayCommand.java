package no.smileyface.discordbot.actions.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.actions.music.PlayAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link PlayAction}.
 */
public class PlayCommand extends ActionCommand<PlayAction.Key> {
	/**
	 * Creates the command.
	 */
	public PlayCommand() {
		super(
				Commands
						.slash("play", "Plays a song")
						.setGuildOnly(true)
						.addOption(
								OptionType.STRING,
								PlayAction.Key.INPUT.str(),
								"A search or URL for the song to play. "
										+ "Search is done through YouTube", true)
						.addOption(
								OptionType.BOOLEAN,
								PlayAction.Key.SONG_SEARCH.str(),
								"If the YouTube search should search for songs only. "
										+ "Ignored if input is a URL  (Default: False)"),
				"p"
		);
	}

	@Override
	public Node<PlayAction.Key, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		Node<PlayAction.Key, Object> args = new Node<>();
		args.addChild(PlayAction.Key.INPUT, event.getOption(
				PlayAction.Key.INPUT.str(),
				OptionMapping::getAsString)
		);
		args.addChild(PlayAction.Key.SONG_SEARCH, event.getOption(
				PlayAction.Key.SONG_SEARCH.str(),
				false,
				OptionMapping::getAsBoolean
		));
		return args;
	}
}
