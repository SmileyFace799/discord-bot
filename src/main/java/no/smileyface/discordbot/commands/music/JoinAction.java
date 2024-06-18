package no.smileyface.discordbot.commands.music;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.smileyface.discordbot.checks.BotNotInVoice;
import no.smileyface.discordbot.checks.InVoice;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.InputRecord;
import no.smileyface.discordbotframework.entities.ActionCommand;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Makes the bot join the user's current voice channel.
 */
public class JoinAction extends BotAction<BotAction.ArgKey> {

	/**
	 * Makes the join action.
	 */
	public JoinAction() {
		super(
				new ActionCommand<>(Commands
						.slash("join", "Joins a voice channel")
						.setGuildOnly(true)
				),
				new InVoice(), new BotNotInVoice()
		);
	}

	@Override
	protected void execute(
			IReplyCallback event,
			MultiTypeMap<ArgKey> args,
			InputRecord inputs
	) {
		Member member = Objects.requireNonNull(event.getMember());
		MusicManager.getInstance().createPlayer(
				Objects.requireNonNull(Objects.requireNonNull(member.getVoiceState()).getChannel()),
				(GuildMessageChannel) event.getMessageChannel(),
				inputs,
				member
		);
		event.reply("Joined channel!").setEphemeral(true).queue();
	}
}
