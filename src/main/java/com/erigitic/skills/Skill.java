package com.erigitic.skills;

import ninja.leaping.configurate.ConfigurationNode;

public interface Skill {
    String SKILLS[] = {"mining"};

    String getSkillName();
    String[][] getExpValues();
    void setupConfig(ConfigurationNode skillsConfig);
}
