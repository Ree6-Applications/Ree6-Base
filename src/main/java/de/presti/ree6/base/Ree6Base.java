package de.presti.ree6.base;

import de.presti.ree6.bot.BotWorker;
import de.presti.ree6.bot.version.BotVersion;
import de.presti.ree6.language.LanguageService;
import de.presti.ree6.menu.MenuEventListener;
import de.presti.ree6.util.data.resolver.ResolverService;
import lombok.Builder;

@Builder
public class Ree6Base {

    BotVersion botVersion = BotVersion.DEVELOPMENT_BUILD;
    boolean loadMenuSystem;
    boolean loadCommandSystem;
    boolean loadLanguageSystem;

    private Ree6Base() {
        botVersion = BotVersion.DEVELOPMENT_BUILD;
        loadMenuSystem = false;
        loadCommandSystem = false;
        loadLanguageSystem = false;
    }
    
    private Ree6Base(BotVersion version, boolean menuSystem, boolean commandSystem, boolean languageSystem) {
        botVersion = version;
        loadMenuSystem = menuSystem;
        loadCommandSystem = commandSystem;
        loadLanguageSystem = languageSystem;
    }
    
    public void create() {
        BotWorker.createBot(botVersion);

        if (loadMenuSystem) {
            BotWorker.addEvent(new MenuEventListener());
        }

        if (loadCommandSystem) {
            try {
                ResolverService.getCommandManagerResolver().resolveClass().init();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (loadLanguageSystem) {
            LanguageService.initializeLanguages();
        }
    }
}
