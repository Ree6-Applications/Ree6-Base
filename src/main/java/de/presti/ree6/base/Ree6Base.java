package de.presti.ree6.base;

import de.presti.ree6.bot.BotWorker;
import de.presti.ree6.bot.version.BotVersion;
import de.presti.ree6.language.LanguageService;
import de.presti.ree6.menu.MenuEventListener;
import de.presti.ree6.util.data.resolver.ResolverService;

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

    public static class Builder {

        final Ree6Base ree6Base;

        public Builder() {
            ree6Base = new Ree6Base();
        }

        public Builder botVersion(BotVersion botVersion) {
            ree6Base.botVersion = botVersion;
            return this;
        }

        public Builder enableMenuSystem() {
            ree6Base.loadMenuSystem = true;
            return this;
        }

        public Builder enableCommandSystem() {
            ree6Base.loadCommandSystem = true;
            return this;
        }

        public Builder enableLanguageSystem() {
            ree6Base.loadLanguageSystem = true;
            return this;
        }

        public Ree6Base build() {
            return ree6Base;
        }
    }
}
