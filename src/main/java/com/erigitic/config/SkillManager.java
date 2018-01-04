package com.erigitic.config;

import com.erigitic.main.SpongeSkills;
import com.erigitic.skills.MiningSkill;
import com.erigitic.skills.Skill;
import com.erigitic.skills.WoodcuttingSkill;
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
import org.spongepowered.api.event.filter.cause.First;
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
    private WoodcuttingSkill woodcuttingSkill;

    private AccountManager accountManager;
    private ConfigurationNode accountsConfig;

    public SkillManager(SpongeSkills plugin) {
        this.plugin = plugin;

        logger = plugin.getLogger();

        miningSkill = new MiningSkill();
        woodcuttingSkill = new WoodcuttingSkill();

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
                miningSkill.setupConfig(skillsConfig);
                woodcuttingSkill.setupConfig(skillsConfig);

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

    private void giveExp(Player player, Account account, String skillName, int expAmount) {
        account.setSkillExp(skillName, account.getSkillExp(skillName) + expAmount);

        checkForLevelUp(player, account, skillName);
    }

    // NOTE: When using the @First annotation, if an object is not found, in this case a Player object, the listener will not
    // be called. Therefore we don't need to do any sort of check ot see if a player is present.
    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
        UUID playerUUID = player.getUniqueId();
        // This does not distinguish between different variants of logs/stone/etc. So the same result will occur with oak logs and spruce logs for example. This'll be discussed in a later video.
        String blockName = event.getTransactions().get(0).getOriginal().getState().getType().getName();
        Account account = new Account(plugin, accountManager, playerUUID);

        // Loop through each skill in order to find an exp value for the broken block, if any.
        for (String skillName : Skill.SKILLS) {
            // Check the configuration file for an exp value for the broken block, if one is not found default to 0.
            int expAmount = skillsConfig.getNode(skillName, blockName).getInt(0);

            // If an exp value was found for the broken block, give the player some exp in that skill and send a message
            if (expAmount > 0) {
                giveExp(player, account, skillName, expAmount);

                player.sendMessage(Text.of("You broke: ", blockName, " worth ", TextColors.GOLD, expAmount, " exp."));

                // We did what we needed to do, so let's skedaddle
                break;
            }
        }

    }
}
