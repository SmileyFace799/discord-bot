package org.smileyface.commands.misc;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.smileyface.commands.BotCommand;
import org.smileyface.misc.MultiTypeMap;

/**
 * Basic ping command.
 */
public class PingCommand extends BotCommand {
    public PingCommand() {
        super(Commands.slash("ping", "Bot answers with \"pong!\""));
    }

    @Override
    protected void execute(IReplyCallback event, MultiTypeMap<String> args) {
        event.reply("pong!").queue();
    }
}
