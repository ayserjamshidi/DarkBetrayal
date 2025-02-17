/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.clientpackets;

import com.ne.gs.model.gameobjects.player.Friend;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionClientPacket;
import com.ne.gs.network.aion.serverpackets.SM_FRIEND_LIST;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

/**
 * Received when a player sets his note
 *
 * @author Ben
 */
public class CM_SET_NOTE extends AionClientPacket {

    private String note;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        note = readS();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        Player activePlayer = getConnection().getActivePlayer();

        if (!note.equals(activePlayer.getCommonData().getNote())) {
            activePlayer.getCommonData().setNote(note);

            for (Friend friend : activePlayer.getFriendList())  { // For all my friends
                Player frienPlayer = friend.getPlayer();
                if (friend.isOnline() && frienPlayer != null) {
                    friend.getPlayer().getClientConnection().sendPacket(new SM_FRIEND_LIST()); // Send him a new friend list
                    // packet
                    // LMFAOOWN make it send packet saying it set new note
                }
            }
        }
        activePlayer.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_READ_TODAY_WORDS(note));
    }
}
