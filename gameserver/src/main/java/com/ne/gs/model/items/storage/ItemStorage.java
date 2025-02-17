/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.items.storage;

import com.google.common.collect.ImmutableList;
import javolution.util.FastMap;

import com.ne.gs.model.gameobjects.Item;
import org.slf4j.LoggerFactory;

/**
 * @author KID
 */
public class ItemStorage {

    public static final int FIRST_AVAILABLE_SLOT = 65535;

    private FastMap<Integer, Item> items;
    private int limit;

    public ItemStorage(int limit) {
        this.limit = limit;
        this.items = FastMap.newInstance();
    }

    public ImmutableList<Item> getItems() {
        return ImmutableList.copyOf(items.values());
    }

    /*static public ImmutableList<Item> test() {
        return ImmutableList.copyOf(items.values());
    }*/

    public int getLimit() {
        return limit;
    }

    public boolean setLimit(int limit) {
        if (items.size() > limit) {
            return false;
        }

        this.limit = limit;
        return true;
    }

    public Item getFirstItemById(int itemId) {
        for (Item item : items.values()) {
            if (item.getItemTemplate().getTemplateId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public ImmutableList<Item> getItemsById(int itemId) {
        ImmutableList.Builder<Item> b = ImmutableList.builder();
        for (Item item : items.values()) {
            if (item.getItemTemplate().getTemplateId() == itemId) {
                b.add(item);
            }
        }
        return b.build();
    }

    public Item getItemByObjId(int itemObjId) {
        LoggerFactory.getLogger(ItemStorage.class).info("itemObjId in ItemStorage.java == " + items.get(itemObjId));
        return items.get(itemObjId);
    }

    public int getSlotIdByItemId(int itemId) {
        for (Item item : items.values()) {
            if (item.getItemTemplate().getTemplateId() == itemId) {
                return item.getEquipmentSlot();
            }
        }
        return -1;
    }

    public Item getItemBySlotId(short slotId) {
        for (Item item : items.values()) {
            if (item.getEquipmentSlot() == slotId) {
                return item;
            }
        }
        return null;
    }

    public int getSlotIdByObjId(int objId) {
        Item item = getItemByObjId(objId);
        if (item != null) {
            return item.getEquipmentSlot();
        } else {
            return -1;
        }
    }

    public int getNextAvailableSlot() {
        return FIRST_AVAILABLE_SLOT;
    }

    public boolean putItem(Item item) {
        if (this.items.containsKey(item.getObjectId())) {
            return false;
        }

        this.items.put(item.getObjectId(), item);
        /*System.out.println(item.getItemName() + " // " + this.items.put(item.getObjectId(), item));
        System.out.println("Contains == " + this.items.containsKey(item.getObjectId()));*/
        return true;
    }

    public Item removeItem(int objId) {
        return items.remove(objId);
    }

    public boolean isFull() {
        return items.size() >= limit;
    }

    public int getFreeSlots() {
        return limit - items.size();
    }

    public int size() {
        return items.size();
    }

}