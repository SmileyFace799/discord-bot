package org.smileyface.commands.feedback;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Reports an issue with the bot to "me" (bot author).
 */
public class ReportIssueCommand extends BotCommand {
    private static final Collection<Long> REPORT_BLACKLIST = Set.of(); //No one, yet :)

    private final Map<Long, LocalDateTime> reportCoolDowns;

    /**
     * Makes the report issue command.
     */
    public ReportIssueCommand() {
        super(Commands.slash("reportissue", "Report an issue with the bot. "
                        + "NB: Make sure to check /knownissues first")
                .addOption(OptionType.STRING, ArgKeys.TOPIC, "The topic of your issue",
                        true)
                .addOption(OptionType.STRING, ArgKeys.DETAILS,
                        "The full details of your issue, "
                                + "including instructions on how to reproduce it", true)
        );
        reportCoolDowns = new HashMap<>();
    }

    @Override
    public MultiTypeMap<String> getArgs(SlashCommandInteractionEvent event) {
        MultiTypeMap<String> args = new MultiTypeMap<>();
        args.put(ArgKeys.CMD_NAME, event.getName());
        args.put(ArgKeys.TOPIC, event.getOption(ArgKeys.TOPIC, OptionMapping::getAsString));
        args.put(ArgKeys.DETAILS, event.getOption(ArgKeys.DETAILS, OptionMapping::getAsString));
        return args;
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        String cmdName = args.get(ArgKeys.CMD_NAME, String.class);
        String topic = args.get(ArgKeys.TOPIC, String.class);
        String details = args.get(ArgKeys.DETAILS, String.class);

        JDA jda = event.getJDA();
        User author = event.getUser();
        User yorthicc = jda.retrieveUserById(651563251896942602L).complete();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime userReportCoolDown = reportCoolDowns.get(author.getIdLong());
        if (REPORT_BLACKLIST.contains(author.getIdLong())) {
            event.reply("You are blacklisted from reporting issues")
                    .setEphemeral(true).queue();
        } else if (jda.getSelfUser().getIdLong() != yorthicc.getIdLong()) {
            event.reply("This is not a bot account that belongs to me."
                    + "\nPlease make sure sure the issue still persists with my bot before "
                    + "reporting it (" + yorthicc.getName()
                    + "). If the issue still persists, report it through my bot. "
                    + "If you do not share a server with my bot, "
                    + "message the host of this bot about this issue instead."
                    + "\n\nIf you need your command again, here it is:\n/"
                    + cmdName + " topic:" + topic + " details:" + details
            ).setEphemeral(true).queue();
        } else if (userReportCoolDown != null && now.isBefore(userReportCoolDown)) {
            long waitDuration = Duration.between(now, userReportCoolDown).getSeconds();
            event.reply("You already sent a report within the last hour. "
                    + "To prevent spam & misuse of this command, please wait "
                    + Math.floorDiv(waitDuration, 60) + "m" + (waitDuration % 60)
                    + "s before reporting again."
                    + "\n\nIf you need your command again, here it is:\n/"
                    + cmdName + " topic:" + topic + " details:" + details
            ).setEphemeral(true).queue();
        } else {
            User me = jda.retrieveUserById(234724168183054336L).complete();
            me.openPrivateChannel().complete().sendMessage("**New reported issue:**"
                    + "\n\nReported by: " + author.getName()
                    + "\nAuthor ID: " + author.getId()
                    + "\n\n**Topic:**\n" + topic
                    + "\n\n**Details:**\n" + details
            ).queue();
            event.reply("Issue has been reported. I may reach out to you for "
                    + "further details about this issue. If you don't have open DMs, "
                    + "I may send you a friend request (I'm "
                    + me.getName() + ")"
            ).setEphemeral(true).queue();
            reportCoolDowns.put(author.getIdLong(), now.plusHours(1));
        }
    }

    /**
     * Keys for args map.
     */
    public static class ArgKeys {
        public static final String CMD_NAME = "cmdName";
        public static final String TOPIC = "topic";
        public static final String DETAILS = "details";

        private ArgKeys() {
            throw new IllegalStateException("Utility class");
        }
    }
}
