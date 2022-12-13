package de.presti.ree6.util.data.resolver;

import de.presti.ree6.commands.CommandManager;
import de.presti.ree6.commands.exceptions.CommandInitializerException;
import de.presti.ree6.util.data.resolver.base.IClassResolver;
import de.presti.ree6.util.data.resolver.base.ILanguageResolver;
import de.presti.ree6.util.data.resolver.base.IPrefixResolver;
import de.presti.ree6.util.data.resolver.implementation.DefaultLanguageResolver;
import de.presti.ree6.util.data.resolver.implementation.DefaultPrefixResolver;
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
     * The Prefix Resolver.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static IPrefixResolver prefixResolver = new DefaultPrefixResolver();

    /**
     * The Language Resolver.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static ILanguageResolver languageResolver = new DefaultLanguageResolver();

    /**
     * The Language Resolver.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static CommandManager commandManager;

    static {
        try {
            commandManager = new CommandManager();
        } catch (Exception e) {
            log.error("Could not initialize CommandManager!", e);
        }
    }
}
