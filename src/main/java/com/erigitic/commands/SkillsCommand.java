package com.erigitic.commands;

import com.erigitic.config.Account;
import com.erigitic.config.AccountManager;
import com.erigitic.main.SpongeSkills;
import com.erigitic.skills.Skill;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class SkillsCommand implements CommandExecutor {

    private SpongeSkills plugin;
    private AccountManager accountManager;

    public SkillsCommand(SpongeSkills plugin) {
        this.plugin = plugin;

        accountManager = plugin.getAccountManager();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = ((Player) src).getPlayer().get();
            UUID uuid = player.getUniqueId();
            Account account = new Account(plugin, accountManager, uuid);

            for (String skill : Skill.SKILLS) {
                int exp = account.getSkillExp(skill);
                int level = account.getSkillLevel(skill);
                int expToLevel = level * 100;

                player.sendMessage(Text.of(skill.toUpperCase(), " | Level ", level, " - ", exp, " / ", expToLevel, " exp"));
            }

            return CommandResult.success();
        }

        return CommandResult.empty();
    }
}
