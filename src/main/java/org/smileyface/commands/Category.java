package org.smileyface.commands;

import java.util.List;
import java.util.Map;
import org.smileyface.generics.GenericManager;

public abstract class Category extends GenericManager<BotCommand> {

    protected Category(List<BotCommand> commandList) {
        super(commandList
                .stream()
                .flatMap(botCommand -> botCommand.getAllVariants().stream()),
                botCommand -> botCommand.getData().getName());
    }
}
