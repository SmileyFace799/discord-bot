package org.smileyface.botlisteners;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for when the bot is all ready & set up.
 */
public class ReadyListener implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            Logger.getLogger(getClass().getName()).log(
                    Level.INFO, "{0} is ready", event.getJDA().getSelfUser().getName()
            );
        }
    }
}
