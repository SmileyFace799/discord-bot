package org.smileyface.audio;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TrackQueueEmbed {
    private final TrackQueue queue;
    private Message playerMessage;
    private String lastCommand;
    private boolean musicEnded;

    public TrackQueueEmbed(TrackQueue queue) {
        this.queue = queue;
        this.musicEnded = false;
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
            embedBuilder
                    .setColor(0x00FF00)
                    .setTitle("PLAYING MUSIC")
                    .setDescription("**NOW PLAYING:**")
                    .addField("Title:", current.getTitle(), false)
                    .addField("Uploaded by:", current.getAuthor(), false)
                    .addField("Queued by:", current.getQueuedBy().getEffectiveName(), false)
                    .addField("Link:", current.getLink(), false);
        }
        if (lastCommand != null) {
            embedBuilder.setFooter("Last command: " + lastCommand);
        }
        return embedBuilder.build();
    }

    public void updateEmbed() {
        if (playerMessage == null) {
            showPlayer();
        } else {
            playerMessage.editMessageEmbeds(buildEmbed()).queue();
        }
    }

    public void showPlayer() {
        if (playerMessage != null) {
            playerMessage.delete().queue();
        }
        playerMessage = queue
                .getPlayerChannel()
                .sendMessageEmbeds(buildEmbed())
                .complete();
    }

    public void setLastCommand(Member author, String lastCommand) {
        this.lastCommand = author.getEffectiveName() + ": " + lastCommand;
        updateEmbed();
    }

    public void playerClosed() {
        this.musicEnded = true;
    }
}
