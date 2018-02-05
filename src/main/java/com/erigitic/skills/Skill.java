package com.erigitic.skills;

import ninja.leaping.configurate.ConfigurationNode;

public interface Skill {
    String SKILLS[] = {"mining", "woodcutting"};

    String getSkillName();
    String[][] getExpValues();
    void setupConfig(ConfigurationNode skillsConfig);
}
