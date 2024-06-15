package no.smileyface.discordbot.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import no.smileyface.discordbot.audio.MusicManager;
import no.smileyface.discordbot.audio.TrackQueue;
import no.smileyface.discordbot.checks.BotIsPlaying;
import no.smileyface.discordbot.checks.InVoiceWithBot;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionButton;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Skips the current song.
 */
public class SkipAction extends BotAction<SkipAction.SkipKey> {
	private static class SkipCommand extends ActionCommand<SkipKey> {
		public SkipCommand() {
			super(Commands
					.slash("skip", "Skips the current song")
					.setGuildOnly(true)
					.addOption(OptionType.INTEGER, SkipKey.AMOUNT.str(),
							"the amount of songs to skip. If invalid, "
									+ "the closest valid value is used  (Default: 1)"
					)
			);
		}

		@Override
		public MultiTypeMap<SkipKey> getSlashArgs(SlashCommandInteractionEvent event) {
			MultiTypeMap<SkipKey> args = new MultiTypeMap<>();
			args.put(SkipKey.AMOUNT, event.getOption(
					SkipKey.AMOUNT.str(),
					1,
					OptionMapping::getAsInt)
			);
			return args;
		}
	}

	private static class SkipButton extends ActionButton<SkipKey> {
		public SkipButton() {
			super(ButtonStyle.PRIMARY, "skipButton", "Skip", Emoji.fromUnicode("⏩"));
		}

		@Override
		public MultiTypeMap<SkipKey> createArgs(ButtonInteractionEvent event) {
			MultiTypeMap<SkipKey> args = new MultiTypeMap<>();
			args.put(SkipKey.AMOUNT, 1);
			return args;
		}
	}

	/**
	 * Makes the skip action.
	 */
	public SkipAction() {
		super(new SkipCommand(), new SkipButton(), new InVoiceWithBot(), new BotIsPlaying());
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<SkipKey> args,
			InputRecord inputs
	) {
		Member author = Objects.requireNonNull(event.getMember());
		Guild guild = author.getGuild();
		TrackQueue queue = MusicManager.getInstance().getQueue(guild.getIdLong());
		int amount = Math.clamp(
				args.get(SkipKey.AMOUNT, Integer.class),
				1,
				queue.getTracks().size() + 1
		);
		queue.skip(amount);
		String replyMessage = "Skipped %s song".formatted(amount);
		if (amount != 1) {
			replyMessage += "s";
		}
		event.reply(replyMessage).setEphemeral(true).queue();
		queue.getTrackQueueMessage().setLastCommand(author, replyMessage);
	}

	/**
	 * Keys for args map.
	 */
	public enum SkipKey implements ArgKey {
		AMOUNT
	}
}
