package de.presti.ree6.commands;

import de.presti.ree6.commands.exceptions.CommandInitializerException;
import de.presti.ree6.commands.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface ICommandManager {

    /**
     * Method called to register every command.
     *
     * @throws CommandInitializerException if an error occurs while initializing the Commands.
     * @throws IllegalStateException       if an Invalid Command was used to initialize.
     * @throws IllegalAccessException      when an Instance of a Command is not accessible.
     * @throws InstantiationException      when an Instance of a Command is not instantiable.
     * @throws NoSuchMethodException       when a Constructor Instance of a Command is not found.
     * @throws InvocationTargetException   when a Constructor Instance of a Command is not found.
     */
    default void init() throws CommandInitializerException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        log.info("Initializing Commands!");

        Reflections reflections = new Reflections();
        Set<Class<? extends ICommand>> classes = reflections.getSubTypesOf(ICommand.class);

        for (Class<? extends ICommand> aClass : classes) {
            log.info("Loading Command {}", aClass.getSimpleName());
            registerCommand(aClass.getDeclaredConstructor().newInstance());
        }
    }

    /**
     * Method used to add all Commands as SlashCommand on Discord.
     *
     * @param jda Instance of the Bot.
     */
    void registerSlashCommand(JDA jda);

    /**
     * Add a single Command to the Command list.
     *
     * @param command the {@link ICommand}.
     * @throws CommandInitializerException if an error occurs while initializing the Command.
     */
    void registerCommand(ICommand command) throws CommandInitializerException;

    /**
     * Remove a Command from the List.
     *
     * @param command the Command you want to remove.
     */
    void unregisterCommand(ICommand command);

    /**
     * Get a Command by its name.
     *
     * @param name the Name of the Command.
     * @return the {@link ICommand} with the same Name.
     */
    ICommand getCommandByName(String name);

    /**
     * Get a Command by its slash command name.
     *
     * @param name the Name of the Command.
     * @return the {@link ICommand} with the same Name.
     */
    ICommand getCommandBySlashName(String name);

    /**
     * Get every Command in the list.
     *
     * @return an {@link List<ICommand>} with all Commands.
     */
    List<ICommand> getCommands();

    /**
     * Try to perform a Command.
     *
     * @param member                       the Member that performed the try.
     * @param guild                        the Guild the Member is from.
     * @param messageContent               the Message content (including the prefix + command name).
     * @param message                      the Message Entity.
     * @param textChannel                  the TextChannel where the command has been performed.
     * @param slashCommandInteractionEvent the Slash Command Event if it was a Slash Command.
     * @return true, if a command has been performed.
     */
    boolean perform(Member member, Guild guild, String messageContent, Message message, MessageChannelUnion textChannel, SlashCommandInteractionEvent slashCommandInteractionEvent);

    /**
     * Send a message to a special Message-Channel.
     *
     * @param messageCreateData the Message content.
     * @param commandEvent      the Command-Event.
     */
    default void sendMessage(MessageCreateData messageCreateData, CommandEvent commandEvent) {
        sendMessage(messageCreateData, commandEvent.getChannel(), commandEvent.getInteractionHook());
    }

    /**
     * Send a message to a special Message-Channel.
     *
     * @param messageCreateData the Message content.
     * @param deleteSecond      the delete delay
     * @param commandEvent      the Command-Event.
     */
    default void sendMessage(MessageCreateData messageCreateData, int deleteSecond, CommandEvent commandEvent) {
        sendMessage(messageCreateData, deleteSecond, commandEvent.getChannel(), commandEvent.getInteractionHook());
    }

    /**
     * Send a message to a special Message-Channel.
     *
     * @param messageCreateData the Message content.
     * @param messageChannel    the Message-Channel.
     */
    default void sendMessage(MessageCreateData messageCreateData, MessageChannel messageChannel) {
        sendMessage(messageCreateData, messageChannel, null);
    }

    /**
     * Send a message to a special Message-Channel, with a deletion delay.
     *
     * @param messageCreateData the Message content.
     * @param deleteSecond      the delete delay
     * @param messageChannel    the Message-Channel.
     */
    default void sendMessage(MessageCreateData messageCreateData, int deleteSecond, MessageChannel messageChannel) {
        sendMessage(messageCreateData, deleteSecond, messageChannel, null);
    }
    /**
     * Send a message to a special Message-Channel.
     *
     * @param messageCreateData the Message content.
     * @param messageChannel    the Message-Channel.
     * @param interactionHook   the Interaction-hook if it is a slash command.
     */
    default void sendMessage(MessageCreateData messageCreateData, MessageChannel messageChannel, InteractionHook interactionHook) {
        if (interactionHook == null) {
            if (messageChannel.canTalk()) messageChannel.sendMessage(messageCreateData).queue();
        } else interactionHook.sendMessage(messageCreateData).queue();
    }

    /**
     * Send a message to a special Message-Channel, with a deletion delay.
     *
     * @param messageCreateData the Message content.
     * @param messageChannel    the Message-Channel.
     * @param interactionHook   the Interaction-hook if it is a slash command.
     * @param deleteSecond      the delete delay
     */
    default void sendMessage(MessageCreateData messageCreateData, int deleteSecond, MessageChannel messageChannel, InteractionHook interactionHook) {
        if (interactionHook == null) {
            if (messageChannel == null) return;
            if (messageChannel.canTalk())
                messageChannel.sendMessage(messageCreateData).delay(deleteSecond, TimeUnit.SECONDS).flatMap(message -> {
                    if (message != null && message.getChannel().retrieveMessageById(message.getId()).complete() != null) {
                        return message.delete();
                    }

                    return null;
                }).queue();
        } else {
            interactionHook.sendMessage(messageCreateData).queue();
        }
    }

    /**
     * Send a message to a special Message-Channel.
     *
     * @param message        the Message content.
     * @param messageChannel the Message-Channel.
     */
    default void sendMessage(String message, MessageChannel messageChannel) {
        sendMessage(message, messageChannel, null);
    }

    /**
     * Send a message to a special Message-Channel, with a deletion delay.
     *
     * @param message        the Message content.
     * @param deleteSecond   the delete delay
     * @param messageChannel the Message-Channel.
     */
    default void sendMessage(String message, int deleteSecond, MessageChannel messageChannel) {
        sendMessage(message, deleteSecond, messageChannel, null);
    }

    /**
     * Send a message to a special Message-Channel.
     *
     * @param message         the Message content.
     * @param messageChannel  the Message-Channel.
     * @param interactionHook the Interaction-hook if it is a slash command.
     */
    default void sendMessage(String message, MessageChannel messageChannel, InteractionHook interactionHook) {
        sendMessage(new MessageCreateBuilder().setContent(message).build(), messageChannel, interactionHook);
    }

    /**
     * Send a message to a special Message-Channel, with a deletion delay.
     *
     * @param messageContent  the Message content.
     * @param messageChannel  the Message-Channel.
     * @param interactionHook the Interaction-hook if it is a slash command.
     * @param deleteSecond    the delete delay
     */
    default void sendMessage(String messageContent, int deleteSecond, MessageChannel messageChannel, InteractionHook interactionHook) {
        sendMessage(new MessageCreateBuilder().setContent(messageContent).build(), deleteSecond, messageChannel, interactionHook);
    }

    /**
     * Send an Embed to a special Message-Channel.
     *
     * @param embedBuilder   the Embed content.
     * @param messageChannel the Message-Channel.
     */
    default void sendMessage(EmbedBuilder embedBuilder, MessageChannel messageChannel) {
        sendMessage(embedBuilder, messageChannel, null);
    }

    /**
     * Send an Embed to a special Message-Channel, with a deletion delay.
     *
     * @param embedBuilder   the Embed content.
     * @param deleteSecond   the delete delay
     * @param messageChannel the Message-Channel.
     */
    default void sendMessage(EmbedBuilder embedBuilder, int deleteSecond, MessageChannel messageChannel) {
        sendMessage(embedBuilder, deleteSecond, messageChannel, null);
    }

    /**
     * Send an Embed to a special Message-Channel.
     *
     * @param embedBuilder    the Embed content.
     * @param messageChannel  the Message-Channel.
     * @param interactionHook the Interaction-hook if it is a slash command.
     */
    default void sendMessage(EmbedBuilder embedBuilder, MessageChannel messageChannel, InteractionHook interactionHook) {
        sendMessage(new MessageCreateBuilder().setEmbeds(embedBuilder.build()).build(), messageChannel, interactionHook);
    }

    /**
     * Send an Embed to a special Message-Channel, with a deletion delay.
     *
     * @param embedBuilder    the Embed content.
     * @param deleteSecond    the delete delay
     * @param messageChannel  the Message-Channel.
     * @param interactionHook the Interaction-hook if it is a slash command.
     */
    default void sendMessage(EmbedBuilder embedBuilder, int deleteSecond, MessageChannel messageChannel, InteractionHook interactionHook) {
        sendMessage(new MessageCreateBuilder().setEmbeds(embedBuilder.build()).build(), deleteSecond, messageChannel, interactionHook);
    }

    /**
     * Delete a specific message.
     *
     * @param message         the {@link Message} entity.
     * @param interactionHook the Interaction-hook, if it is a slash event.
     */
    default void deleteMessage(Message message, InteractionHook interactionHook) {
        Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        if (message != null && message.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE) && message.getChannel().retrieveMessageById(message.getIdLong()).complete() != null && message.getType().canDelete() && !message.isEphemeral() && interactionHook == null) {
            message.delete().onErrorMap(throwable -> {
                log.error("[CommandManager] Couldn't delete a Message!", throwable);
                return null;
            }).queue();
        }
    }
}
