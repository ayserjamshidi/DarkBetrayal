/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.templates.quest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestWorkItems", propOrder = {"questWorkItem"})
public class QuestWorkItems {

    @XmlElement(name = "quest_work_item")
    protected List<QuestItems> questWorkItem;

    /**
     * Gets the value of the questWorkItem property.
     * <p/>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the questWorkItem property.
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getQuestWorkItem().add(newItem);
     * </pre>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link QuestItems }
     */
    public List<QuestItems> getQuestWorkItem() {
        if (questWorkItem == null) {
            questWorkItem = new ArrayList<>(0);
        }
        return this.questWorkItem;
    }

}
