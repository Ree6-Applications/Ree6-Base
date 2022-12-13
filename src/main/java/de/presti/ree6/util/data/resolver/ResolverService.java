package de.presti.ree6.util.data.resolver;

import de.presti.ree6.util.data.resolver.base.IObjectResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
}
