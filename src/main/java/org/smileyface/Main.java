package org.smileyface;

import java.nio.file.NoSuchFileException;
import java.util.Collection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.smileyface.commands.BotCommand;
import org.smileyface.commands.CommandManager;
import org.smileyface.botlisteners.ComponentListener;
import org.smileyface.botlisteners.ReadyListener;
import org.smileyface.botlisteners.SlashCommandListener;

/**
 * Main class for starting the bot.
 */
public class Main {
    /**
     * Starts the bot.
     *
     * @param args java args. This is ignored
     * @throws NoSuchFileException If the token file for the active bot is not found
     * @throws InterruptedException If the bot is interrupted while starting
     */
    public static void main(String[] args) throws NoSuchFileException, InterruptedException {
        JDA jda = JDABuilder.createDefault(
                TokenManager.getActiveBot(), GatewayIntent.GUILD_VOICE_STATES)
                .disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                .addEventListeners(
                        new ReadyListener(),
                        new SlashCommandListener(),
                        new ComponentListener())
                .build();

        Collection<BotCommand> allCommands = CommandManager.getInstance().getItems().values();

        jda.awaitReady();

        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(allCommands
                .stream()
                .map(BotCommand::getData)
                .toList()).queue();

        Presence botPresence = jda.getPresence();
        botPresence.setActivity(Activity.playing("Actually working :D"));
    }
}
