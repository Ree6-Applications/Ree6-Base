package de.presti.ree6.util.data.resolver.base;

/**
 * Interface used to resolve the Prefix of a Guild.
 */
public interface IPrefixResolver {

    /**
     * Method used to resolve the Prefix of a Guild.
     * @param guildId The ID of the Guild.
     * @return The Prefix of the Guild.
     */
    String resolvePrefix(long guildId);
}
