package de.presti.ree6.commands;

import de.presti.ree6.language.LanguageService;
import de.presti.ree6.util.data.resolver.ResolverService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Event class used to parse and provide Information about a command execution.
 */
public class CommandEvent {

    /**
     * The Name of Command that has been executed.
     */
    @Getter
    String command;

    /**
     * The Member associated with the Command execution.
     */
    @Nonnull
    Member member;

    /**
     * The Guild associated with the Command execution.
     */
    @Nonnull
    Guild guild;

    /**
     * The message associated with the Command execution.
     */
    @Nullable
    Message message;

    /**
     * The MessageChannel associated with the Command execution.
     */
    @Nonnull
    MessageChannelUnion channel;

    /**
     * The Arguments parsed with the Command execution.
     */
    @Nullable
    String[] arguments;

    /**
     * The SlashCommandEvent associated with the Command execution.
     */
    @Nullable
    SlashCommandInteractionEvent slashCommandInteractionEvent;

    /**
     * Constructor used to save the Data.
     *
     * @param command                      the Command Name.
     * @param member                       the {@link Member} Entity.
     * @param guild                        the {@link Guild} Entity.
     * @param message                      the {@link Message} Entity.
     * @param textChannel                  the {@link TextChannel} Entity.
     * @param arguments                    the given Arguments.
     * @param slashCommandInteractionEvent the {@link SlashCommandInteractionEvent} Entity.
     */
    public CommandEvent(String command, @Nonnull Member member, @Nonnull Guild guild, @Nullable Message message, @Nonnull MessageChannelUnion textChannel, @Nullable String[] arguments, @Nullable SlashCommandInteractionEvent slashCommandInteractionEvent) {
        this.command = command;
        this.member = member;
        this.guild = guild;
        this.message = message;
        this.channel = textChannel;
        this.arguments = arguments;
        this.slashCommandInteractionEvent = slashCommandInteractionEvent;
    }

    /**
     * Reply to the Command execution.
     *
     * @param message the Message to reply with.
     */
    public void reply(String message) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.setContent(message);
        reply(messageCreateBuilder.build());
    }

    /**
     * Reply to the Command execution.
     *
     * @param message      the Message to reply with.
     * @param deleteSecond the Seconds to delete the Message after.
     */
    public void reply(String message, int deleteSecond) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.setContent(message);
        reply(messageCreateBuilder.build(), deleteSecond);
    }

    /**
     * Reply to the Command execution.
     *
     * @param message the Message to reply with.
     */
    public void reply(MessageEmbed message) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.setEmbeds(message);
        reply(messageCreateBuilder.build());
    }

    /**
     * Reply to the Command execution.
     *
     * @param message      the Message to reply with.
     * @param deleteSecond the Seconds to delete the Message after.
     */
    public void reply(MessageEmbed message, int deleteSecond) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.setEmbeds(message);
        reply(messageCreateBuilder.build(), deleteSecond);
    }

    /**
     * Reply to the Command execution.
     *
     * @param message the Message to reply with.
     */
    public void reply(MessageCreateData message) {
        ResolverService.getCommandManagerResolver().resolveClass().sendMessage(message, getChannel(), getInteractionHook());
    }

    /**
     * Reply to the Command execution.
     *
     * @param message      the Message to reply with.
     * @param deleteSecond the Seconds to delete the Message after.
     */
    public void reply(MessageCreateData message, int deleteSecond) {
        ResolverService.getCommandManagerResolver().resolveClass().sendMessage(message, deleteSecond, getChannel(), getInteractionHook());
    }

    /**
     * Get a Message from the Guild specific Language Setting.
     *
     * @param key        the Key of the Message.
     * @param parameters the Parameters to replace in the Message.
     * @return the Message.
     */
    public String getResource(String key, Object... parameters) {
        return LanguageService.getByEvent(this, key, parameters);
    }

    /**
     * Get the {@link Member} Entity associated with the Event.
     *
     * @return the {@link Member} Entity.
     */
    public @NotNull Member getMember() {
        return member;
    }

    /**
     * Get the {@link Guild} Entity associated with the Event.
     *
     * @return the {@link Guild} Entity.
     */
    public @NotNull Guild getGuild() {
        return guild;
    }

    /**
     * Get the {@link Message} Entity associated with the Event.
     *
     * @return the {@link Message} Entity.
     */
    public @Nullable Message getMessage() {
        return message;
    }

    /**
     * Get the {@link TextChannel} Entity associated with the Event.
     *
     * @return the {@link TextChannel} Entity.
     */
    public @NotNull MessageChannelUnion getChannel() {
        return channel;
    }

    /**
     * Get the Arguments associated with the Event.
     *
     * @return the Arguments.
     */

    public String[] getArguments() {
        return getArguments(false);
    }

    /**
     * Get the Arguments associated with the Event.
     *
     * @param parseFromSlash if the Arguments should be parsed from the SlashCommandInteractionEvent.
     *
     * @return the Arguments.
     */
    public String[] getArguments(boolean parseFromSlash) {
        if (arguments == null) {
            arguments = new String[0];
        }

        if (isSlashCommand() && parseFromSlash) {
            getSlashCommandInteractionEvent().getOptions().forEach(option -> {
                String[] newArguments = new String[arguments.length + 1];
                System.arraycopy(arguments, 0, newArguments, 0, arguments.length);
                newArguments[newArguments.length - 1] = option.getAsString();
                arguments = newArguments;
            });
        }

        return arguments;
    }

    /**
     * Get the {@link SlashCommandInteractionEvent} Entity associated with the Event.
     *
     * @return the {@link SlashCommandInteractionEvent} Entity.
     */
    public @Nullable SlashCommandInteractionEvent getSlashCommandInteractionEvent() {
        return slashCommandInteractionEvent;
    }

    /**
     * Check if the Command Execution is a Slash Command or not.
     *
     * @return true, if it is a Slash Command Execution. | false, if not.
     */
    public boolean isSlashCommand() {
        return getSlashCommandInteractionEvent() != null;
    }

    /**
     * Get the {@link InteractionHook} from the {@link SlashCommandInteractionEvent}.
     *
     * @return the {@link InteractionHook} Entity.
     */
    public InteractionHook getInteractionHook() {
        if (isSlashCommand()) return getSlashCommandInteractionEvent().getHook().setEphemeral(true);

        return null;
    }
}
