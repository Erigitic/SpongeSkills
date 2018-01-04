package com.erigitic.skills;

import ninja.leaping.configurate.ConfigurationNode;

public class WoodcuttingSkill implements Skill {

    public WoodcuttingSkill() {

    }

    public String getSkillName() {
        return "woodcutting";
    }

    public String[][] getExpValues() {
        String[][] values = {{"minecraft:log", "5"}};

        return values;
    }

    public void setupConfig(ConfigurationNode skillsConfig) {
        String[][] values = getExpValues();

        for (int i = 0; i < values.length; i++) {
            skillsConfig.getNode(getSkillName(), values[i][0]).setValue(values[i][1]);
        }
    }
}
