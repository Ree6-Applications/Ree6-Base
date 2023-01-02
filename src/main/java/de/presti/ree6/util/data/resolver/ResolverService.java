package de.presti.ree6.util.data.resolver;

import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.CommandManager;
import de.presti.ree6.commands.exceptions.CommandInitializerException;
import de.presti.ree6.util.data.resolver.base.IClassResolver;
import de.presti.ree6.util.data.resolver.base.IConsumerResolver;
import de.presti.ree6.util.data.resolver.base.IObjectResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * Class used to store the Resolvers.
 */
@Slf4j
public class ResolverService {


    /**
     * The Language Resolver.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static IObjectResolver<String, Long> languageResolver = aLong -> "en-GB";

    /**
     * The Prefix Resolver.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static IObjectResolver<String, Long> prefixResolver = aLong -> "ree!";

    /**
     * The Command Block Resolver.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static IObjectResolver<Boolean, Long> commandBlockResolver = aBoolean -> false;

    /**
     * The Command Execute Action Resolver.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static IConsumerResolver<CommandEvent> commandExecutionActionResolver = commandEvent -> null;

    /**
     * The CommandManager Resolver.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static IClassResolver<CommandManager> commandManagerResolver = () -> {
        try {
            return new CommandManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}
