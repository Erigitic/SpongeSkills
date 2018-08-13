package com.erigitic.config;

import com.erigitic.main.SpongeSkills;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.UUID;

public class Account {
    private SpongeSkills plugin;
    private AccountManager accountManager;
    private UUID uuid;

    private ConfigurationNode accountsConfig;

    public Account(SpongeSkills plugin, AccountManager accountManager, UUID uuid) {
        this.plugin = plugin;
        this.accountManager = accountManager;
        this.uuid = uuid;

        accountsConfig = accountManager.getAccountsConfig();
    }

    public int getSkillExp(String skillName) {
        return accountsConfig.getNode(uuid.toString(), "skills", skillName, "exp").getInt(0);
    }

    public void setSkillExp(String skillName, int expAmount) {
        accountsConfig.getNode(uuid.toString(), "skills", skillName, "exp").setValue(expAmount);

        accountManager.saveConfig();
    }

    public int getSkillLevel(String skillName) {
        return accountsConfig.getNode(uuid.toString(), "skills", skillName, "level").getInt(1);
    }

    public void setSkillLevel(String skillName, int skillLevel) {
        accountsConfig.getNode(uuid.toString(), "skills", skillName, "level").setValue(skillLevel);

        accountManager.saveConfig();
    }

    public int getSkillPoints() {
        return accountsConfig.getNode(uuid.toString(), "skillPoints").getInt(0);
    }

    public void setSkillPoints(int skillPoints) {
        accountsConfig.getNode(uuid.toString(), "skillPoints").setValue(skillPoints);

        accountManager.saveConfig();
    }

    public void awardSkillPoint() {
        int curSkillPoints = getSkillPoints();

        setSkillPoints(curSkillPoints + 1);
    }
}
