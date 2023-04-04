package org.smileyface.commands.misc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;

//DO NOT REMOVE THIS COMMANDs
public class CreditsCommand extends BotCommand {
    public CreditsCommand() {
        super(Commands.slash("credits", "Shows bot credits"));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        JDA jda = event.getJDA();
        User smiley = jda.retrieveUserById(234724168183054336L).complete();

        StringBuilder msg = new StringBuilder("This bot is running on a bot script developed by "
                + smiley.getName() + "#" + smiley.getDiscriminator() + "."
                + "\nI'm the single developer behind this bot script, "
                + "and I've developed it as a hobby project, "
                + "alongside my studies in computer engineering."
                + "\nAnyone is free to run my script on their bot account, "
                + "and I also don't mind if people want to fork my repository "
                + "to make their own version of it."
                + "\n\nThe bot is developed with the help of the following libraries:"
                + "\n - Java Discord API (JDA): " + "`https://github.com/DV8FromTheWorld/JDA`"
                + "\n - [fork] LavaPlayer - Audio player library for Discord: "
                + "`https://github.com/Walkyst/lavaplayer-fork`"
        );

        //DO NOT REMOVE OR MODIFY THIS
        User yorthicc = jda.retrieveUserById(651563251896942602L).complete();
        if (jda.getSelfUser().getIdLong() != yorthicc.getIdLong()) {
            msg
                    .append("\n\n**Note:** This bot is not an account that belongs to me. ")
                    .append("I do not hold any responsibility for anything harmful or damaging ")
                    .append("done by any bot account other than my own (Which is ")
                    .append(yorthicc.getName()).append("#")
                    .append(yorthicc.getDiscriminator()).append("). ")
                    .append("Therefore, I cannot guarantee that this bot is to be trusted, ")
                    .append("only use this bot if you trust whoever is hosting it.");
        }

        event.reply(msg.toString()).setEphemeral(true).queue();
    }
}
