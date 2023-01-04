package de.presti.ree6.menu;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

public class MenuEventListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        MenuUtil.getItems().stream()
                .filter(c -> c.identifier.equals(event.getId()))
                .filter(c -> c.component instanceof Button)
                .forEach(c -> c.consumer.accept(event));
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        super.onStringSelectInteraction(event);
        MenuUtil.getItems().stream()
                .filter(c -> c.identifier.equals(event.getId()))
                .filter(c -> c.component instanceof StringSelectMenu)
                .forEach(c -> c.consumer.accept(event));
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        super.onEntitySelectInteraction(event);
        MenuUtil.getItems().stream()
                .filter(c -> c.identifier.equals(event.getId()))
                .filter(c -> c.component instanceof EntitySelectMenu)
                .forEach(c -> c.consumer.accept(event));
    }
}
