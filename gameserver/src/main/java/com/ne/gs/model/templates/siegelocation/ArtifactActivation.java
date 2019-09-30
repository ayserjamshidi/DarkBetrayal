/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.templates.siegelocation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArtifactActivation")
public class ArtifactActivation {

    @XmlAttribute(name = "itemid")
    protected int itemId;
    @XmlAttribute(name = "count")
    protected int count;
    @XmlAttribute(name = "skill")
    protected int skill;
    @XmlAttribute(name = "cd")
    protected int cd;

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

    public int getSkillId() {
        return skill;
    }

    public long getCd() {
        return (long) (cd * 1000);
    }
}
