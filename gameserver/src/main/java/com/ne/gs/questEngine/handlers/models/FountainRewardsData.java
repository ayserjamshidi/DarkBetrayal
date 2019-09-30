/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.questEngine.handlers.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.handlers.template.FountainRewards;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FountainRewardsData")
public class FountainRewardsData extends XMLQuest {

    @XmlAttribute(name = "start_npc_ids", required = true)
    protected List<Integer> startNpcIds;

    @Override
    public void register(QuestEngine questEngine) {
        FountainRewards template = new FountainRewards(id, startNpcIds);
        questEngine.addQuestHandler(template);
    }
}
