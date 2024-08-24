package no.smileyface.discordbot.actions.music.commands;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.actions.music.RemoveAction;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.noncontext.ActionCommand;

/**
 * Command for triggering {@link RemoveAction}.
 */
public class RemoveCommand extends ActionCommand<RemoveAction.Key> {

	/**
	 * Creates the command.
	 */
	public RemoveCommand() {
		super(Commands
				.slash("remove", "Removes one or more songs from the queue")
				.setGuildOnly(true)
				.addOption(
						OptionType.INTEGER,
						RemoveAction.Key.INDEX.str(),
						"Index of the song to remove",
						true
				).addOption(
						OptionType.INTEGER,
						RemoveAction.Key.END_INDEX.str(),
						"Makes this remove multiple songs, from \"index\" to \"end_index\""
				)
		);
	}

	@Override
	public Node<RemoveAction.Key, Object> getSlashArgs(SlashCommandInteractionEvent event) {
		Node<RemoveAction.Key, Object> args = new Node<>();
		args.addChild(
				RemoveAction.Key.INDEX,
				Objects.requireNonNull(event.getOption(RemoveAction.Key.INDEX.str())).getAsInt()
		);
		OptionMapping endIndex = event.getOption(RemoveAction.Key.END_INDEX.str());
		if (endIndex != null) {
			args.addChild(RemoveAction.Key.END_INDEX, endIndex.getAsInt());
		}
		return args;
	}
}