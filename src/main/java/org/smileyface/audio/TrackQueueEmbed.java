package org.smileyface.audio;

import java.util.Collections;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.smileyface.components.ComponentManager;

/**
 * The embed associated with a queue, shown in chat.
 */
public class TrackQueueEmbed {
    private final TrackQueue queue;
    private Message playerMessage;
    private String lastCommand;
    private boolean musicEnded;
    private boolean musicPaused;

    /**
     * Makes the embed class.
     *
     * @param queue The queue to make an embed for
     */
    public TrackQueueEmbed(TrackQueue queue) {
        this.queue = queue;
        this.musicEnded = false;
        this.musicPaused = false;
    }

    /**
     * Builds an embed with the current audio information.
     *
     * @return The constructed embed
     */
    private MessageEmbed buildEmbed() {
        MusicTrack current = queue.getCurrentlyPlaying();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (musicEnded) {
            embedBuilder.setColor(0xFF0000).setTitle("Music has ended");
        } else if (current == null) {
            embedBuilder.setColor(0xFFFF00).setTitle("Ready to play music...");
        } else {
            if (musicPaused) {
                embedBuilder
                        .setColor(0xFFFF00)
                        .setTitle("PAUSED");
            } else {
                embedBuilder
                        .setColor(0x00FF00)
                        .setTitle("PLAYING MUSIC");
            }
            embedBuilder
                    .setDescription("**NOW PLAYING:**")
                    .addField("Title:", current.getTitle(), false)
                    .addField("Uploaded by:", current.getAuthor(), false)
                    .addField("Queued by:", current.getQueuedBy().getEffectiveName(), false)
                    .addField("Link:", current.getLink(), false);
        }
        if (lastCommand != null) {
            embedBuilder.setFooter("Last command - " + lastCommand);
        }
        return embedBuilder.build();
    }

    /**
     * Updates the player message, and makes it if it doesn't exist yet.
     */
    public void updateEmbed() {
        if (playerMessage == null) {
            showPlayer();
        } else {
            playerMessage.editMessageEmbeds(buildEmbed()).queue();
            if (musicEnded) {
                playerMessage.editMessageComponents(Collections.emptyList()).queue();
            }
        }
    }

    /**
     * Shows a new player message, and deletes the old one if it exists.
     */
    public void showPlayer() {
        if (playerMessage != null) {
            playerMessage.delete().queue();
        }
        ComponentManager componentManager = ComponentManager.getInstance();
        playerMessage = queue
                .getPlayerChannel()
                .sendMessageEmbeds(buildEmbed())
                .addActionRow(
                        componentManager.getItem("queueButton"),
                        componentManager.getItem("playPauseButton")
                ).complete();
    }

    /**
     * Sets the footer with the last command used.
     *
     * @param author The author of that command
     * @param lastCommand A description of what the last command was
     */
    public void setLastCommand(Member author, String lastCommand) {
        this.lastCommand = author.getEffectiveName() + ": " + lastCommand;
        updateEmbed();
    }

    /**
     * Signals that the player has closed, and updates the message accordingly.
     */
    public void playerClosed() {
        this.musicEnded = true;
        updateEmbed();
    }

    public void togglePaused() {
        musicPaused = !musicPaused;
        updateEmbed();
    }
}
