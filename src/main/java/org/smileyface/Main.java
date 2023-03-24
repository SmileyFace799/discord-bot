package org.smileyface;

import java.nio.file.NoSuchFileException;
import java.util.Collection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.smileyface.commands.BotCommand;
import org.smileyface.commands.CommandManager;
import org.smileyface.listeners.ReadyListener;
import org.smileyface.listeners.SlashCommandListener;

/**
 * Main class for starting the bot.
 */
public class Main {
    private static final String ACTIVE_BOT = "YorthiccBot";

    /**
     * Starts the bot.
     *
     * @param args java args. This is ignored
     * @throws NoSuchFileException If the token file for the active bot is not found
     * @throws InterruptedException If the bot is interrupted while starting
     */
    public static void main(String[] args) throws NoSuchFileException, InterruptedException {
        JDA jda = JDABuilder.createDefault(
                Token.get(ACTIVE_BOT.toLowerCase()), GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(new ReadyListener(), new SlashCommandListener())
                .build();

        Collection<BotCommand> allCommands = CommandManager.getCommands();

        jda.awaitReady();

        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(allCommands
                .stream()
                .map(BotCommand::getData)
                .toList()).queue();

        Presence botPresence = jda.getPresence();
        botPresence.setStatus(OnlineStatus.IDLE);
        botPresence.setActivity(Activity.playing("Being updated :)"));
    }
}
