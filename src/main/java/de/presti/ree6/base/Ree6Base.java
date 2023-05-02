package de.presti.ree6.base;

import de.presti.ree6.bot.BotWorker;
import de.presti.ree6.bot.version.BotVersion;
import de.presti.ree6.language.LanguageService;
import de.presti.ree6.menu.MenuEventListener;
import de.presti.ree6.util.data.resolver.ResolverService;
import lombok.Builder;

@Builder
class Ree6Base {

    BotVersion botVersion = BotVersion.DEVELOPMENT_BUILD;
    boolean loadMenuSystem;
    boolean loadCommandSystem;
    boolean loadLanguageSystem;

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
