package de.presti.ree6.util.data.resolver.base;

/**
 * Interface used to resolve the Language of a Guild.
 */
public interface ILanguageResolver {

    /**
     * Method used to resolve the Language of a Guild.
     * @param guildId The ID of the Guild.
     * @return The Language of the Guild.
     */
    String resolveLanguage(long guildId);
}
