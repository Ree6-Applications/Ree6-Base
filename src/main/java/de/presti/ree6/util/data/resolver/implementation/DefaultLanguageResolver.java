package de.presti.ree6.util.data.resolver.implementation;

import de.presti.ree6.util.data.resolver.base.ILanguageResolver;

/**
 * Default implementation for the Language Resolver.
 */
public class DefaultLanguageResolver implements ILanguageResolver {

    /**
     * Method used to resolve the Language of a Guild.
     *
     * @param guildId The ID of the Guild.
     * @return The Language of the Guild.
     */
    @Override
    public String resolveLanguage(long guildId) {
        return "en-GB";
    }
}
