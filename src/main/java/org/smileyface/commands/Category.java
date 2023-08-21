package org.smileyface.commands;

import java.util.List;
import org.smileyface.generics.GenericManager;

/**
 * A category containing commands.
 */
public abstract class Category extends GenericManager<BotCommand> {

    protected Category(List<BotCommand> commandList) {
        super(commandList
                .stream()
                .flatMap(botCommand -> botCommand.getAllVariants().stream()),
                botCommand -> botCommand.getData().getName());
    }
}
