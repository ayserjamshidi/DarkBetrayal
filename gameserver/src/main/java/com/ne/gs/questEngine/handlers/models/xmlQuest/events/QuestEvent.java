/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.questEngine.handlers.models.xmlQuest.events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

import com.ne.gs.questEngine.handlers.models.xmlQuest.conditions.QuestConditions;
import com.ne.gs.questEngine.handlers.models.xmlQuest.operations.QuestOperations;
import com.ne.gs.questEngine.model.QuestEnv;

/**
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestEvent", propOrder = {"conditions", "operations"})
@XmlSeeAlso({OnKillEvent.class, OnTalkEvent.class})
public abstract class QuestEvent {

    protected QuestConditions conditions;
    protected QuestOperations operations;
    @XmlAttribute
    protected List<Integer> ids;

    public boolean operate(QuestEnv env) {
        return false;
    }

    /**
     * Gets the value of the ids property.
     * <p/>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the ids property.
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getIds().add(newItem);
     * </pre>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link Integer }
     */
    public List<Integer> getIds() {
        if (ids == null) {
            ids = new ArrayList<>();
        }
        return this.ids;
    }
}
