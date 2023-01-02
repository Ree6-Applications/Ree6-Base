package de.presti.ree6.language;

import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.util.data.resolver.ResolverService;
import de.presti.ree6.util.external.RequestUtility;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * Utility used to work with Languages.
 */
@Slf4j
public class LanguageService {

    /**
     * A Hashmap containing the locale as key and the YamlConfiguration as value.
     */
    public static final HashMap<DiscordLocale, Language> languageResources = new HashMap<>();

    /**
     * Called to load every Language file into memory.
     */
    public static void initializeLanguages() {
        Path languagePath = Path.of("languages");

        try {
            for (File file : Objects.requireNonNull(languagePath.toFile().listFiles())) {
                if (!file.getName().endsWith(".yml") && !file.getName().endsWith(".yaml")) {
                    log.info("Skipping file {} because it's not a YAML file!", file.getName());
                    continue;
                }

                Language language = new Language(YamlConfiguration.loadConfiguration(file));
                loadLanguageFromFile(language.getDiscordLocale());
            }
        } catch (Exception e) {
            log.error("Couldn't load the language files!", e);
        }
    }

    /**
     * Called to load a Language from a YamlConfiguration.
     *
     * @param discordLocale The DiscordLocale of the Language.
     * @return The Language.
     */
    public static @Nullable Language loadLanguageFromFile(@NotNull DiscordLocale discordLocale) {
        Path languageFile = Path.of("languages/", discordLocale.getLocale() + ".yml");
        if (Files.exists(languageFile)) {
            try {
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(languageFile.toFile());
                Language language = new Language(yamlConfiguration);
                languageResources.putIfAbsent(discordLocale, language);
                return language;
            } catch (Exception exception) {
                log.error("Error while getting Language File!", exception);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Called to get a specific String from the Guild specific Language file.
     *
     * @param commandEvent the CommandEvent.
     * @param key          the key of the String.
     * @param parameter    the parameter to replace.
     * @return the String.
     */
    public static @NotNull String getByEvent(@NotNull CommandEvent commandEvent, @NotNull String key, @Nullable Object... parameter) {
        if (commandEvent.isSlashCommand()) {
            return getByInteraction(commandEvent.getInteractionHook().getInteraction(), key, parameter);
        } else {
            return getByGuild(commandEvent.getGuild(), key, parameter);
        }
    }

    /**
     * Called to get a specific String from the Guild specific Language file.
     *
     * @param commandEvent the GuildEvent.
     * @param key          the key of the String.
     * @param parameter    the parameter to replace.
     * @return the String.
     */
    public static @NotNull String getByEvent(@NotNull GenericGuildEvent commandEvent, @NotNull String key, @Nullable Object... parameter) {
        return getByGuild(commandEvent.getGuild(), key, parameter);
    }

    /**
     * Called to get a specific String from the Language file.
     *
     * @param guild     The Guild to receive the locale from.
     * @param key       The key of the String.
     * @param parameter The Parameters to replace placeholders in the String.
     * @return The String.
     */
    public static @NotNull String getByGuild(Guild guild, @NotNull String key, @Nullable Object... parameter) {
        return getByGuild(guild != null ? guild.getIdLong() : -1, key, parameter);
    }

    /**
     * Called to get a specific String from the Language file.
     *
     * @param guildId   The Guild ID to receive the locale from.
     * @param key       The key of the String.
     * @param parameter The Parameters to replace placeholders in the String.
     * @return The String.
     */
    public static @NotNull String getByGuild(long guildId, @NotNull String key, @Nullable Object... parameter) {
        String resource;
        if (guildId == -1) {
            resource = getDefault(key, parameter);
        } else {
            resource = getByLocale(ResolverService.getLanguageResolver().resolve(guildId), key, parameter);
        }
        resource = resource
                .replace("{guild_prefix}", ResolverService.getPrefixResolver().resolve(guildId));

        return resource;
    }

    /**
     * Called to get a specific String from the default Language file.
     *
     * @param interaction The Interaction to receive the locale from.
     * @param key         The key of the String.
     * @param parameter   The Parameters to replace placeholders in the String.
     * @return The String.
     */
    public static @NotNull String getByInteraction(Interaction interaction, @NotNull String key, @Nullable Object... parameter) {
        String resource = getByLocale(interaction.getUserLocale(), key, parameter);

        if (interaction.getGuild() != null)
            resource = resource
                    .replace("{guild_prefix}", ResolverService.getPrefixResolver().resolve(interaction.getGuild().getIdLong()));

        return resource;
    }

    /**
     * Called to get a specific String from the default Language file.
     *
     * @param key       The key of the String.
     * @param parameter The Parameters to replace placeholders in the String.
     * @return The String.
     */
    public static @NotNull String getDefault(@NotNull String key, @Nullable Object... parameter) {
        return getByLocale(DiscordLocale.ENGLISH_UK, key, parameter);
    }

    /**
     * Called to get a specific String from the Language file.
     *
     * @param locale     The locale of the Language file.
     * @param key        The key of the String.
     * @param parameters The Parameters to replace placeholders in the String.
     * @return The String.
     */
    public static @NotNull String getByLocale(@NotNull String locale, @NotNull String key, @Nullable Object... parameters) {
        return getByLocale(DiscordLocale.from(locale), key, parameters);
    }

    /**
     * Called to get a specific String from the Language file.
     *
     * @param discordLocale The locale of the Language file.
     * @param key           The key of the String.
     * @param parameters    The Parameters to replace placeholders in the String.
     * @return The String.
     */
    public static @NotNull String getByLocale(@NotNull DiscordLocale discordLocale, @NotNull String key, @Nullable Object... parameters) {
        if (discordLocale == DiscordLocale.UNKNOWN) return getDefault(key, parameters);
        Language language = languageResources.containsKey(discordLocale) ? languageResources.get(discordLocale) : languageResources.get(DiscordLocale.ENGLISH_UK);
        return language != null ? language.getResource(key, parameters) : "Missing language resource!";
    }

    /**
     * Called to retrieve all supported Locals.
     * @return The supported Locals.
     */
    public static Set<DiscordLocale> getSupported() {
        return languageResources.keySet();
    }
}
