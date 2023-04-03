package org.smileyface.commands;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.smileyface.checks.CommandFailedException;

public abstract class Category {
    private final Map<String, BotCommand> commands;

    protected Category(List<BotCommand> commandList) {
        this.commands = commandList.stream().collect(Collectors.toMap(
                command -> command.getData().getName(),
                command -> command
        ));

        for (BotCommand command : commandList.stream().toList()) {
            Collection<String> nicknames = command.getNicknames();
            for (String nickname : nicknames) {
                SlashCommandData commandData = command.getData();
                BotCommand shortcutCommand = new BotCommand(Commands
                        .slash(nickname, "Shortcut for /" + commandData.getName())
                        .addOptions(commandData.getOptions())
                        .setGuildOnly(commandData.isGuildOnly())
                        .setDefaultPermissions(commandData.getDefaultPermissions())
                        .setNSFW(commandData.isNSFW())
                ) {
                    @Override
                    public void run(SlashCommandInteractionEvent event)
                            throws CommandFailedException {
                        command.run(event);
                    }
                };
                commands.put(nickname, shortcutCommand);
            }
        }
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
    }
}
