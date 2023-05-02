package de.presti.ree6.menu;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import net.dv8tion.jda.internal.interactions.component.EntitySelectMenuImpl;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import java.util.function.Consumer;
@Builder
@Getter(AccessLevel.PUBLIC)
class MenuItem {

    String identifier;

    ActionComponent component;

    Consumer<GenericComponentInteractionCreateEvent> consumer;
}
