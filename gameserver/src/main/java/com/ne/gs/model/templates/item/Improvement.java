/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Improvement")
public class Improvement {

    @XmlAttribute(name = "way", required = true)
    private int way;

    @XmlAttribute(name = "price2")
    private int price2;

    @XmlAttribute(name = "price1")
    private int price1;

    @XmlAttribute(name = "burn_defend")
    private int burnDefend;

    @XmlAttribute(name = "burn_attack")
    private int burnAttack;

    @XmlAttribute(name = "level")
    private int level;

    public int getLevel() {
        return level;
    }

    public int getChargeWay() {
        return way;
    }

    public int getPrice1() {
        return price1;
    }

    public int getPrice2() {
        return price2;
    }

    public int getBurnAttack() {
        return burnAttack;
    }

    public int getBurnDefend() {
        return burnDefend;
    }
}
