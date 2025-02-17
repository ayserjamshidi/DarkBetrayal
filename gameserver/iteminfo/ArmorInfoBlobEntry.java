/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.items.ItemSlot;
import com.ne.gs.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob is sent for armors. It keeps info about slots that armor can be equipped to.
 *
 * @author -Nemesiss-
 */
public class ArmorInfoBlobEntry extends ItemBlobEntry {

    ArmorInfoBlobEntry() {
        super(ItemBlobType.SLOTS_ARMOR);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        // 4.0
        Item item = owner.item;

        writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).id());
        writeQ(buf, 0); // TODO! secondary slot?
        writeC(buf, item.getItemTemplate().isItemDyePermitted() ? 1 : 0);
        writeC(buf, (item.getItemColor() & 0xFF0000) >> 16);
        writeC(buf, (item.getItemColor() & 0xFF00) >> 8);
        writeC(buf, (item.getItemColor() & 0xFF));

        // 3.0
        /*writeD(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).id());
        writeD(buf, 0);// TODO! secondary slot?
        writeD(buf, 0);*/
    }
}
