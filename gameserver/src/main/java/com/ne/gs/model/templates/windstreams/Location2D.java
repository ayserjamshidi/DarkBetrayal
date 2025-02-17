/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.templates.windstreams;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.ne.gs.model.flypath.FlyPathType;

/**
 * @author LokiReborn
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Location2D")
public class Location2D {

    @XmlAttribute(name = "id")
    protected int id;

    @XmlAttribute(name = "state")
    protected int state;

    @XmlAttribute(name = "fly_path")
    protected FlyPathType flyPath;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the boost
     */
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public FlyPathType getFlyPathType() {
        return flyPath;
    }
}

