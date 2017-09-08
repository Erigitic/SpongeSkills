package com.erigitic.config;

import com.erigitic.main.SpongeSkills;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AccountManager {
    private Logger logger;
    private File accountsFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode accountsConfig;

    private SpongeSkills plugin;

    public AccountManager(SpongeSkills plugin) {
        this.plugin = plugin;

        logger = plugin.getLogger();

        setupAccountConfig();
    }

    private void setupAccountConfig() {
        accountsFile = new File(plugin.getConfigDir().toFile(), "accounts.conf");
        loader = HoconConfigurationLoader.builder().setFile(accountsFile).build();

        try {
            accountsConfig = loader.load();

            if (!accountsFile.exists()) {
                accountsConfig.getNode("placeholder").setValue(true);
                loader.save(accountsConfig);
            }
        } catch (IOException e) {
            logger.warn("Error setting up the account configuration!");
        }
    }

    public void createAccount(UUID uuid) {
        Account account = new Account(plugin, this, uuid);

        if (!hasAccount(uuid)) {
            account.setSkillLevel("mining", 1);
            account.setSkillExp("mining", 0);
            saveConfig();
        }
    }

    public boolean hasAccount(UUID uuid) {
        return accountsConfig.getNode(uuid.toString()).getValue() != null;
    }

    public ConfigurationNode getAccountsConfig() {
        return accountsConfig;
    }

    public void saveConfig() {
        try {
            loader.save(accountsConfig);
        } catch (IOException e) {
            logger.warn("Error saving the accounts configuration!");
        }
    }
}
