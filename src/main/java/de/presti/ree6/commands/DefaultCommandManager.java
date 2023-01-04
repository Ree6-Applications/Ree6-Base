package de.presti.ree6.commands;

import de.presti.ree6.bot.BotWorker;
import de.presti.ree6.commands.exceptions.CommandInitializerException;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.language.LanguageService;
import de.presti.ree6.util.data.resolver.ResolverService;
import de.presti.ree6.util.others.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manager class used to manage all Commands and command related operation.
 */
@Slf4j
public class DefaultCommandManager implements ICommandManager {

    /**
     * An Arraylist with all registered Commands.
     */
    static final ArrayList<ICommand> commands = new ArrayList<>();

    /**
     * HashMap used to store a users Ids, to keep them from spamming commands.
     */
    static final List<String> commandCooldown = new ArrayList<>();

    /**
     * Constructor for the Command-Manager used to register every Command.
     *
     * @throws CommandInitializerException if an error occurs while initializing the Commands.
     * @throws IllegalStateException       if an Invalid Command was used to initialize.
     * @throws IllegalAccessException      when an Instance of a Command is not accessible.
     * @throws InstantiationException      when an Instance of a Command is not instantiable.
     * @throws NoSuchMethodException       when a Constructor Instance of a Command is not found.
     * @throws InvocationTargetException   when a Constructor Instance of a Command is not found.
     */
    public DefaultCommandManager() throws CommandInitializerException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        init();
    }

    @Override
    public void registerSlashCommand(JDA jda) {
        CommandListUpdateAction listUpdateAction = jda.updateCommands();

        for (ICommand command : getCommands()) {
            Command commandAnnotation = command.getClass().getAnnotation(Command.class);

            if (commandAnnotation.category() == Category.HIDDEN) continue;

            CommandData commandData;

            if (command.getCommandData() != null) {
                commandData = command.getCommandData();
            } else {
                commandData = new CommandDataImpl(command.getClass().getAnnotation(Command.class).name(), command.getClass().getAnnotation(Command.class).description());
            }

            if (commandAnnotation.category() == Category.NSFW) {
                commandData.setNSFW(true);
            }

            if (commandData instanceof CommandDataImpl commandData1) {

                for (DiscordLocale discordLocale : DiscordLocale.values()) {
                    if (!LanguageService.languageResources.containsKey(discordLocale)) continue;

                    String description = LanguageService.getByLocale(discordLocale, commandAnnotation.description());
                    if (description.equals("Missing language resource!")) {
                        description = LanguageService.getDefault(commandAnnotation.description());
                    }

                    if (!description.equals("Missing language resource!")) {
                        commandData1.setDescriptionLocalization(discordLocale, description);
                    }
                }

                String description = LanguageService.getDefault(commandAnnotation.description());

                if (!description.equals("Missing language resource!")) {
                    commandData1.setDescription(description);
                }

                if (commandAnnotation.category() == Category.MOD && commandData.getDefaultPermissions() == DefaultMemberPermissions.ENABLED) {
                    commandData1.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
                }

                commandData1.setGuildOnly(true);

                //noinspection ResultOfMethodCallIgnored
                listUpdateAction.addCommands(commandData1);
            } else {
                if (commandAnnotation.category() == Category.MOD && commandData.getDefaultPermissions() == DefaultMemberPermissions.ENABLED) {
                    commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
                }

                commandData.setGuildOnly(true);

                //noinspection ResultOfMethodCallIgnored
                listUpdateAction.addCommands(commandData);
            }
        }

        listUpdateAction.queue();
    }

    @Override
    public void registerCommand(ICommand command) throws CommandInitializerException {
        if (!command.getClass().isAnnotationPresent(Command.class) || command.getClass().getAnnotation(Command.class).category() == null)
            throw new CommandInitializerException(command.getClass());

        if (!commands.contains(command)) {
            commands.add(command);
        }
    }

    @Override
    public ICommand getCommandByName(String name) {
        return getCommands().stream().filter(command -> command.getClass().getAnnotation(Command.class).name().equalsIgnoreCase(name) || Arrays.stream(command.getAlias()).anyMatch(s -> s.equalsIgnoreCase(name))).findFirst().orElse(null);

    }

    @Override
    public ICommand getCommandBySlashName(String name) {
        return getCommands().stream().filter(command -> (command.getCommandData() != null && command.getCommandData().getName().equalsIgnoreCase(name)) || (command.getClass().isAnnotationPresent(Command.class) && command.getClass().getAnnotation(Command.class).name().equalsIgnoreCase(name))).findFirst().orElse(null);
    }

    @Override
    public void unregisterCommand(ICommand command) {
        commands.remove(command);
    }

    @Override
    public List<ICommand> getCommands() {
        return commands;
    }

    @Override
    public boolean perform(Member member, Guild guild, String messageContent, Message message, MessageChannelUnion textChannel, SlashCommandInteractionEvent slashCommandInteractionEvent) {

        // Check if the User is under Cooldown.
        if (isTimeout(member.getUser())) {

            // Check if it is a Slash Command or not.
            if (slashCommandInteractionEvent != null) {
                sendMessage(LanguageService.getByGuild(guild, "command.perform.cooldown"), 5, textChannel, slashCommandInteractionEvent.getHook().setEphemeral(true));
                deleteMessage(message, slashCommandInteractionEvent.getHook().setEphemeral(true));
            } else if (messageContent.toLowerCase().startsWith(ResolverService.getPrefixResolver().resolve(guild.getIdLong()))) {
                sendMessage(LanguageService.getByGuild(guild, "command.perform.cooldown"), 5, textChannel, null);
                deleteMessage(message, null);
            }

            // Return false.
            return false;
        }

        // Check if it is a Slash Command.
        if (slashCommandInteractionEvent != null) {
            if (!performSlashCommand(textChannel, slashCommandInteractionEvent)) {
                return false;
            }
        } else {
            if (!performMessageCommand(member, guild, messageContent, message, textChannel)) {
                return false;
            }
        }

        // Check if this is a Developer build, if not then cooldown the User.
        if (!BotWorker.getVersion().isDebug()) {
            ThreadUtil.createThread(x -> commandCooldown.remove(member.getUser().getId()), null, Duration.ofSeconds(5), false, false);
        }

        // Add them to the Cooldown.
        if (!commandCooldown.contains(member.getUser().getId()) && !BotWorker.getVersion().isDebug()) {
            commandCooldown.add(member.getUser().getId());
        }

        // Return that a command has been performed.
        return true;
    }

    /**
     * Perform a Message based Command.
     *
     * @param member         the Member that performed the command.
     * @param guild          the Guild the Member is from.
     * @param messageContent the Message content (including the prefix + command name).
     * @param message        the Message Entity.
     * @param textChannel    the TextChannel where the command has been performed.
     * @return true, if a command has been performed.
     */
    private boolean performMessageCommand(Member member, Guild guild, String messageContent, Message message, MessageChannelUnion textChannel) {
        // Check if the Message is null.
        if (message == null) {
            sendMessage(LanguageService.getByGuild(guild, "command.perform.error"), 5, textChannel, null);
            return false;
        }

        // Check if the message starts with the prefix.
        if (!messageContent.toLowerCase().startsWith(ResolverService.getPrefixResolver().resolve(guild.getIdLong()).toLowerCase()))
            return false;

        // Parse the Message and remove the prefix from it.
        messageContent = messageContent.substring(ResolverService.getPrefixResolver().resolve(guild.getIdLong()).length());

        // Split all Arguments.
        String[] arguments = messageContent.split(" ");

        if (arguments.length == 0 || arguments[0].isBlank()) {
            sendMessage("Please provide a command!", 5, textChannel, null);
            return false;
        }

        // Get the Command by the name.
        ICommand command = getCommandByName(arguments[0]);

        // Check if there is even a Command with that name.
        if (command == null) {
            sendMessage(LanguageService.getByGuild(guild, "command.perform.notFound"), 5, textChannel, null);
            return false;
        }

        // Check if the Command is blacklisted.
        if (!ResolverService.getCommandBlockResolver().resolve(guild.getIdLong()) &&
                command.getClass().getAnnotation(Command.class).category() != Category.HIDDEN) {
            sendMessage(LanguageService.getByGuild(guild, "command.perform.blocked"), 5, textChannel, null);
            return false;
        }

        // Parse the arguments.
        String[] argumentsParsed = Arrays.copyOfRange(arguments, 1, arguments.length);

        // Perform the Command.
        command.onASyncPerform(new CommandEvent(command.getClass().getAnnotation(Command.class).name(), member, guild, message, textChannel, argumentsParsed, null));

        return true;
    }

    /**
     * Call when a slash command has been performed.
     *
     * @param textChannel                  the TextChannel where the command has been performed.
     * @param slashCommandInteractionEvent the Slash-Command Event.
     * @return true, if a command has been performed.
     */
    private boolean performSlashCommand(MessageChannelUnion textChannel, SlashCommandInteractionEvent slashCommandInteractionEvent) {
        //Get the Command by the Slash Command Name.
        ICommand command = getCommandBySlashName(slashCommandInteractionEvent.getName());

        // Check if there is a command with that Name.
        if (command == null || slashCommandInteractionEvent.getGuild() == null || slashCommandInteractionEvent.getMember() == null) {
            sendMessage(LanguageService.getByGuild(slashCommandInteractionEvent.getGuild(), "command.perform.notFound"), 5, null, slashCommandInteractionEvent.getHook().setEphemeral(true));
            return false;
        }

        // Check if the command is blocked or not.
        if (!ResolverService.getCommandBlockResolver().resolve(slashCommandInteractionEvent.getGuild().getIdLong()) && command.getClass().getAnnotation(Command.class).category() != Category.HIDDEN) {
            sendMessage(LanguageService.getByGuild(slashCommandInteractionEvent.getGuild(), "command.perform.blocked"), 5, null, slashCommandInteractionEvent.getHook().setEphemeral(true));
            return false;
        }

        // Perform the Command.
        command.onASyncPerform(new CommandEvent(command.getClass().getAnnotation(Command.class).name(), slashCommandInteractionEvent.getMember(), slashCommandInteractionEvent.getGuild(), null, textChannel, null, slashCommandInteractionEvent));

        return true;
    }

    /**
     * Check if a User is time-outed.
     *
     * @param user the User.
     * @return true, if yes | false, if not.
     */
    public boolean isTimeout(User user) {
        return commandCooldown.contains(user.getId()) && !BotWorker.getVersion().isDebug();
    }
}