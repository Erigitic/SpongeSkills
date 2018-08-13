package com.erigitic.main;

import com.erigitic.commands.SkillsCommand;
import com.erigitic.config.AccountManager;
import com.erigitic.config.SkillManager;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Plugin(id="spongeskills", name="Sponge Skills", description="Skills for Sponge",version="1.0.0")
public class SpongeSkills {
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    @Inject
    private PluginContainer pluginContainer;

    private ConfigurationNode config;

    private AccountManager accountManager;
    private SkillManager skillManager;

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        setupConfig();

        accountManager = new AccountManager(this);
        skillManager = new SkillManager(this);
    }

    @Listener
    public void init(GameInitializationEvent event) {
        createAndRegisterCommands();

        game.getEventManager().registerListeners(this, skillManager);
    }

    @Listener
    public void postInit(GamePostInitializationEvent event) {

    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Sponge Skills has started!");
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        logger.info("Sponge Skills has stopped!");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        UUID playerUUID = event.getTargetEntity().getUniqueId();

        accountManager.createAccount(playerUUID);
    }

    private void setupConfig() {
        try {
            if (!defaultConfig.toFile().exists()) {
                pluginContainer.getAsset("spongeskills.conf").get().copyToFile(defaultConfig);
            }

            config = loader.load();
        } catch (IOException e) {
            logger.warn("An error occurred while setting up the main configuration file!");
        }
    }

    private void createAndRegisterCommands() {
        CommandSpec skillsCommand = CommandSpec.builder()
                .description(Text.of("Display levels and experiences amounts for skills"))
                .permission("spongeskills.command.skills")
                .executor(new SkillsCommand(this))
                .build();

        game.getCommandManager().register(this, skillsCommand, "skills", "s");
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getConfigDir() {
        return configDir;
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }
}
