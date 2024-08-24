package no.smileyface.discordbot.actions.music;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.music.commands.JoinCommand;
import no.smileyface.discordbot.checks.BotNotInVoice;
import no.smileyface.discordbot.checks.InVoice;
import no.smileyface.discordbot.model.intermediary.MusicManager;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

/**
 * Makes the bot join the user's current voice channel.
 */
public class JoinAction extends BotAction<GenericBotAction.ArgKey> {
	/**
	 * Makes the join action.
	 */
	public JoinAction(ActionManager manager) {
		super(manager, new JoinCommand(), new InVoice(), new BotNotInVoice());
	}

	@Override
	protected void execute(IReplyCallback event, Node<ArgKey, Object> args) {
		Member member = Objects.requireNonNull(event.getMember());
		MusicManager.getInstance().createPlayer(
				Objects.requireNonNull(Objects.requireNonNull(member.getVoiceState()).getChannel()),
				(GuildMessageChannel) event.getMessageChannel(),
				getIdentifier(),
				member
		);
		event.reply("Joined channel!").setEphemeral(true).queue();
	}
}
