package org.smileyface.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class BotCommand {
    SlashCommandData data;

    protected BotCommand(SlashCommandData data) {
        this.data = data;
    }

    public SlashCommandData getData() {
        return data;
    }

    public abstract void run(SlashCommandInteractionEvent event);
}
