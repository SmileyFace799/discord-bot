package org.smileyface.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.smileyface.components.ComponentManager;

/**
 * The embed associated with a queue, shown in chat.
 */
public class TrackQueueMessage {
    private static final int QUEUE_PAGE_SIZE = 10;

    private final TrackQueue queue;
    private Message playerMessage;
    private String lastCommand;
    private boolean musicEnded;
    private boolean musicPaused;
    private int page;
    private int lastPage;

    /**
     * Makes the embed class.
     *
     * @param queue The queue to make an embed for
     */
    public TrackQueueMessage(TrackQueue queue) {
        this.queue = queue;
        this.musicEnded = false;
        this.musicPaused = false;
        this.page = 1;
        this.lastPage = 1;
    }

    /**
     * Builds an embed with the current audio information.
     *
     * @return The constructed embed
     */
    private List<MessageEmbed> buildEmbeds() {
        MusicTrack current = queue.getCurrentlyPlaying();
        EmbedBuilder playerEmbed = new EmbedBuilder();
        EmbedBuilder queueEmbed = new EmbedBuilder();
        List<EmbedBuilder> showEmbeds = new ArrayList<>();
        showEmbeds.add(playerEmbed);
        if (musicEnded) {
            playerEmbed.setColor(0xFF0000).setTitle("Music has ended");
        } else if (current == null) {
            playerEmbed.setColor(0xFFFF00).setTitle("Ready to play music...");
        } else {
            if (musicPaused) {
                playerEmbed
                        .setColor(0xFFFF00)
                        .setTitle("PAUSED");
            } else {
                playerEmbed
                        .setColor(0x00FF00)
                        .setTitle("PLAYING MUSIC");
            }
            playerEmbed
                    .setDescription("**NOW PLAYING:**")
                    .addField("Title:", current.getTitle(), false)
                    .addField("Uploaded by:", current.getAuthor(), false)
                    .addField("Queued by:", current.getQueuedBy().getEffectiveName(), false)
                    .addField("Link:", current.getLink(), false);

            //Make & show queueEmbed
            showEmbeds.add(queueEmbed);
            List<MusicTrack> queuedTracks = new ArrayList<>();
            queuedTracks.add(current);
            queuedTracks.addAll(queue.getTracks());
            lastPage = Math.floorDiv(queuedTracks.size() - 1, QUEUE_PAGE_SIZE) + 1;
            if (page > lastPage) {
                this.page = lastPage;
            }
            queueEmbed
                    .setTitle("**QUEUE:**")
                    .setColor(0x7F7F7F)
                    .addField(
                            String.format("Page %s of %s", page, lastPage),
                            String.join("\n",
                                    queuedTracks.subList(
                                                    (page - 1) * QUEUE_PAGE_SIZE,
                                                    Math.min(
                                                            page * QUEUE_PAGE_SIZE,
                                                            queuedTracks.size()
                                                    )
                                            )
                                            .stream()
                                            .map(track -> "  **"
                                                    + (track.equals(queue.getCurrentlyPlaying())
                                                    ? "Playing"
                                                    : (queuedTracks.indexOf(track) + 1)) + ":** "
                                                    + track.getTitle()
                                            )
                                            .toList()
                            ), false);
        }
        if (lastCommand != null) {
            playerEmbed.setFooter("Last command - " + lastCommand);
        }

        return showEmbeds.stream().map(EmbedBuilder::build).toList();
    }

    /**
     * Updates the player message, and makes it if it doesn't exist yet.
     */
    public void updateEmbed() {
        if (playerMessage == null) {
            showPlayer();
        } else {
            playerMessage.editMessageEmbeds(buildEmbeds()).queue();
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
                .sendMessageEmbeds(buildEmbeds())
                .addActionRow(
                        componentManager.getItem("skipButton"),
                        componentManager.getItem("playPauseButton"),
                        componentManager.getItem("queueButton"),
                        componentManager.getItem("stopButton")
                ).addActionRow(
                        componentManager.getItem("prevPageButton"),
                        componentManager.getItem("nextPageButton"),
                        componentManager.getItem("goToPageButton")
                ).complete();
    }

    /**
     * Sets the footer with the last command used.
     *
     * @param author      The author of that command
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
        this.musicPaused = !musicPaused;
        updateEmbed();
    }

    /**
     * Sets the current page. If the new page is an invalid page,
     * the nearest valid page will be shown.
     *
     * @param page The page to go to
     * @return If the new page was a valid page
     */
    public boolean setPage(int page) {
        this.page = Math.max(1, Math.min(lastPage, page));
        updateEmbed();
        return this.page == page;
    }

    /**
     * Increments the current page. If the increment results in an invalid page,
     * the nearest valid page will be shown.
     *
     * @param increment The amount to increment with
     * @return If the increment resulted in a valid page
     */
    public boolean incrementPage(int increment) {
        return setPage(page + increment);
    }

    public int getPage() {
        return page;
    }
}
