package org.smileyface.components;

import java.util.stream.Stream;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import org.smileyface.components.buttons.GoToPageButton;
import org.smileyface.components.buttons.NextPageButton;
import org.smileyface.components.buttons.PlayPauseButton;
import org.smileyface.components.buttons.PrevPageButton;
import org.smileyface.components.buttons.QueueButton;
import org.smileyface.components.buttons.RepeatButton;
import org.smileyface.components.buttons.ShuffleButton;
import org.smileyface.components.buttons.SkipButton;
import org.smileyface.components.buttons.StopButton;
import org.smileyface.generics.GenericManager;

/**
 * Intermediary class that instantiates & stores specific components.
 */
public class ComponentManager extends GenericManager<ActionComponent> {
    private static ComponentManager instance;

    private ComponentManager() {
        super(Stream.of(
                new SkipButton(),
                new PlayPauseButton(),
                new QueueButton(),
                new StopButton(),
                new PrevPageButton(),
                new NextPageButton(),
                new GoToPageButton(),
                new RepeatButton(),
                new ShuffleButton()
        ), ActionComponent::getId);
    }

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static ComponentManager getInstance() {
        if (instance == null) {
            instance = new ComponentManager();
        }
        return instance;
    }
}
