package com.erigitic.config;

import com.erigitic.main.SpongeSkills;
import com.erigitic.skills.MiningSkill;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SkillManager {
    private Logger logger;
    private File skillsFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode skillsConfig;

    private SpongeSkills plugin;

    private MiningSkill miningSkill;

    private AccountManager accountManager;
    private ConfigurationNode accountsConfig;

    public SkillManager(SpongeSkills plugin) {
        this.plugin = plugin;

        logger = plugin.getLogger();

        miningSkill = new MiningSkill();

        accountManager = plugin.getAccountManager();
        accountsConfig = accountManager.getAccountsConfig();

        setupSkillConfig();
    }

    private void setupSkillConfig() {
        skillsFile = new File(plugin.getConfigDir().toFile(), "skills.conf");
        loader = HoconConfigurationLoader.builder().setFile(skillsFile).build();

        try {
            skillsConfig = loader.load();

            if (!skillsFile.exists()) {
                skillsConfig.getNode("placeholder").setValue(true);
                miningSkill.setupConfig(skillsConfig);
                loader.save(skillsConfig);
            }
        } catch (IOException e) {
            logger.warn("Error setting up the account configuration!");
        }
    }

    private void checkForLevelUp(Player player, Account account, String skillName) {
        int curLvl = account.getSkillLevel(skillName);
        int expAmount = account.getSkillExp(skillName);

        int expToLvl = curLvl * 100;

        if (expAmount >= expToLvl) {
            account.setSkillLevel(skillName, curLvl + 1);

            player.sendMessage(Text.of("You are now level ", TextColors.GOLD, account.getSkillLevel(skillName), TextColors.WHITE,
                    " in ", skillName, "."));

            ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.ENDER_TELEPORT).build();

            player.spawnParticles(effect, player.getLocation().getPosition().add(0, 2, 0));

            player.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, player.getLocation().getPosition(), 1);
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            UUID playerUUID = player.getUniqueId();
            String blockName = event.getTransactions().get(0).getOriginal().getState().getName();

            int miningExpAmount = skillsConfig.getNode("mining", blockName).getInt(0);

            Account account = new Account(plugin, accountManager, playerUUID);

            if (miningExpAmount > 0) {
                account.setSkillExp("mining", account.getSkillExp("mining") + miningExpAmount);

                checkForLevelUp(player, account, "mining");
            }

            player.sendMessage(Text.of("You broke: ", blockName, " worth ", TextColors.GOLD, miningExpAmount, " exp."));
        }
    }
}
