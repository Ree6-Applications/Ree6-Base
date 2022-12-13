package de.presti.ree6.util.data.resolver.implementation;

import de.presti.ree6.util.data.resolver.base.IPrefixResolver;

/**
 * Default implementation for the Prefix Resolver.
 */
public class DefaultPrefixResolver implements IPrefixResolver {

    /**
     * Method used to resolve the Prefix of a Guild.
     *
     * @param guildId The ID of the Guild.
     * @return The Prefix of the Guild.
     */
    @Override
    public String resolvePrefix(long guildId) {
        return "ree!";
    }
}
