/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.templates.itemgroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

import com.ne.gs.model.templates.rewards.IdLevelReward;

/**
 * @author Rolandas
 *
 */

/**
 * <p/>
 * Java class for FoodGroup complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="FoodGroup">
 *   &lt;complexContent>
 *     &lt;extension base="{}ItemGroup">
 *       &lt;sequence>
 *         &lt;element name="item" type="{}IdLevelReward" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FoodGroup")
public class FoodGroup extends BonusItemGroup {

    @XmlElement(name = "item")
    protected List<IdLevelReward> items;

    /**
     * Gets the value of the item property.
     * <p/>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the item property.
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getItems().add(newItem);
     * </pre>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link IdLevelReward }
     */
    public List<IdLevelReward> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return this.items;
    }

    /* (non-Javadoc)
     * @see com.ne.gs.model.templates.itemgroups.ItemGroup#getRewards()
     */
    @Override
    public ItemRaceEntry[] getRewards() {
        return getItems().toArray(new ItemRaceEntry[0]);
    }

}
