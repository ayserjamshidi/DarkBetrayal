/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.skillengine.model;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;

import com.ne.gs.model.templates.item.WeaponType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "motion_time")
public class MotionTime {

    @XmlAttribute(required = true)
    protected String times;
    @XmlAttribute(required = true)
    protected String name;// TODO enum

    @XmlTransient
    private final HashMap<WeaponTypeWrapper, Integer> timeForWeaponType = new HashMap<>();

    public String getName() {
        return name;
    }

    public String getTimes() {
        return times;
    }

    /**
     * @param name
     *     the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param times
     *     the times to set
     */
    public void setTimes(String times) {
        this.times = times;
    }

    public int getTimeForWeapon(WeaponTypeWrapper weapon) {
        return timeForWeaponType.get(weapon);
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        String[] tokens = times.split(",");
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.BOOK_2H, null), Integer.parseInt(tokens[0]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.BOW, null), Integer.parseInt(tokens[1]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.DAGGER_1H, null), Integer.parseInt(tokens[2]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.MACE_1H, null), Integer.parseInt(tokens[3]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.ORB_2H, null), Integer.parseInt(tokens[4]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.POLEARM_2H, null), Integer.parseInt(tokens[5]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.STAFF_2H, null), Integer.parseInt(tokens[6]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.SWORD_1H, null), Integer.parseInt(tokens[7]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.SWORD_2H, null), Integer.parseInt(tokens[8]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.SWORD_1H, WeaponType.SWORD_1H), Integer.parseInt(tokens[9]));
    }
    /**
     * ordering BOOK_2H BOW DAGGER_1H MACE_1H ORB_2H POLEARM_2H STAFF_2H SWORD_1H SWORD_2H dualwield
     */
}
