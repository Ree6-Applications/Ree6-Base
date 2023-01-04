package de.presti.ree6.menu;

import lombok.AccessLevel;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import net.dv8tion.jda.internal.interactions.component.EntitySelectMenuImpl;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import java.util.function.Consumer;

class MenuItem {

    @Getter(AccessLevel.PUBLIC)
    String identifier;

    @Getter(AccessLevel.PUBLIC)
    ActionComponent component;

    @Getter(AccessLevel.PUBLIC)
    Consumer<GenericComponentInteractionCreateEvent> consumer;

    public static class Builder {

        final MenuItem menuItem;

        public Builder() {
            menuItem = new MenuItem();
        }

        public Builder button(ButtonImpl button) {
            menuItem.component = button;
            return this;
        }

        public Builder stringSelection(StringSelectMenuImpl stringSelectMenu) {
            menuItem.component = stringSelectMenu;
            return this;
        }

        public Builder entitySelection(EntitySelectMenuImpl entitySelectMenu) {
            menuItem.component = entitySelectMenu;
            return this;
        }


        public Builder consumer(Consumer<GenericComponentInteractionCreateEvent> eventConsumer) {
            menuItem.consumer = eventConsumer;
            return this;
        }

        public MenuItem build() {
            menuItem.identifier = menuItem.component.getId();
            return menuItem;
        }
    }
}
