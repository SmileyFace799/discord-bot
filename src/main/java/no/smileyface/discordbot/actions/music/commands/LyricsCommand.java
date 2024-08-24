package no.smileyface.discordbot.actions.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.actions.music.LyricsAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link LyricsAction}.
 */
public class LyricsCommand extends ActionCommand<LyricsAction.Key> {
	/**
	 * Makes the command.
	 */
	public LyricsCommand() {
		super(Commands
				.slash("lyrics", "Gets song lyrics for a song. "
						+ "Use this with no arguments for current song lyrics"
				)
				.addOption(
						OptionType.STRING,
						LyricsAction.Key.SEARCH.str(),
						"The query to use when searching for song lyrics"
				)
		);
	}

	@Override
	public Node<LyricsAction.Key, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		Node<LyricsAction.Key, Object> args = new Node<>();

		OptionMapping searchOption = event.getOption(LyricsAction.Key.SEARCH.str());
		if (searchOption != null) {
			args.addChild(LyricsAction.Key.SEARCH, searchOption.getAsString());
		}
		return args;
	}
}
