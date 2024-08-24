package no.smileyface.discordbot.actions.misc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import no.smileyface.discordbot.actions.misc.commands.CreditsCommand;
import no.smileyface.discordbotframework.ActionManager;
import no.smileyface.discordbotframework.data.Node;
import no.smileyface.discordbotframework.entities.BotAction;
import no.smileyface.discordbotframework.entities.GenericBotAction;

//DO NOT REMOVE THIS COMMAND
/**
 * Shows bot credits.
 */
public class CreditsAction extends BotAction<GenericBotAction.ArgKey> {
    public CreditsAction(ActionManager manager) {
        super(manager, new CreditsCommand());
    }

    @Override
    protected void execute(
            IReplyCallback event,
            Node<ArgKey, Object> args
    ) {
        JDA jda = event.getJDA();
        User smiley = jda.retrieveUserById(234724168183054336L).complete();

        StringBuilder msg = new StringBuilder("This bot is running on a bot script developed by "
                + smiley.getName() + "."
                + "\nI'm the single developer behind this bot script, "
                + "and I've developed it as a hobby project, "
                + "alongside my studies in computer engineering."
                + "\nAnyone is free to run my script on their bot account, "
                + "and I also don't mind if people want to fork my repository "
                + "to make their own version of it."
                + "\n\nThe bot is developed with the help of the following libraries:"
                + "\n- Java Discord API (JDA): " + "`https://github.com/DV8FromTheWorld/JDA`"
                + "\n- [fork] LavaPlayer - Audio player library for Discord: "
                + "`https://github.com/lavalink-devs/lavaplayer`"
        );

        //DO NOT REMOVE OR MODIFY THIS
        User yorthicc = jda.retrieveUserById(651563251896942602L).complete();
        if (jda.getSelfUser().getIdLong() != yorthicc.getIdLong()) {
            msg
                    .append("\n\n**Note:** This bot is not an account that belongs to me. ")
                    .append("I do not hold any responsibility for anything harmful or damaging ")
                    .append("done by any bot account other than my own (Which is ")
                    .append(yorthicc.getName()).append("). ")
                    .append("Therefore, I cannot guarantee that this bot is to be trusted, ")
                    .append("only use this bot if you trust whoever is hosting it.");
        }

        event.reply(msg.toString()).setEphemeral(true).queue();
    }
}
