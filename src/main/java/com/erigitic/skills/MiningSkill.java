package com.erigitic.skills;

import ninja.leaping.configurate.ConfigurationNode;

public class MiningSkill implements Skill {
    public MiningSkill() {

    }

    public String getSkillName() {
        return "mining";
    }

    public String[][] getExpValues() {
        String[][] values = {{"minecraft:coal_ore", "10"}, {"minecraft:iron_ore", "20"}};

        return values;
    }

    public void setupConfig(ConfigurationNode skillsConfig) {
        String[][] values = getExpValues();

        for (int i = 0; i < values.length; i++) {
            skillsConfig.getNode(getSkillName(), values[i][0]).setValue(values[i][1]);
        }
    }
}
