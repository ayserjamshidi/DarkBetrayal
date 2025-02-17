/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;

/**
 * Replies to a request to add or delete a friend
 *
 * @author Ben
 */
public class SM_FRIEND_RESPONSE extends AionServerPacket {

    /**
     * You have added {NAME} to your Friends List.
     */
    public static final int TARGET_ADDED = 0x00;

    /**
     * The target of a friend request is offline
     */
    //public static final int TARGET_OFFLINE = 0x01;
    /**
     * The target is already a friend
     */
    public static final int TARGET_ALREADY_FRIEND = 0x02;

    /**
     * The target does not exist
     */
    public static final int TARGET_NOT_FOUND = 0x03;

    /**
     * {NAME} denied your friend request.
     */
    public static final int TARGET_DENIED = 0x04;

    /**
     * The target's friend list is full
     */
    public static final int TARGET_LIST_FULL = 0x05;
    /**
     * The friend was removed from your list
     */
    public static final int TARGET_REMOVED = 0x06;
    /**
     * The target is in your blocked list, and cannot be added to your friends list.
     */
    public static final int TARGET_BLOCKED = 0x08;
    /**
     * The target is dead and cannot be befriended yet.
     */
    public static final int TARGET_DEAD = 0x09;


    /**
     * The target is dead and cannot be befriended yet.
     */
    public static final int TARGET_OFFLINE = 0x0B;

    /**
     * The target is dead and cannot be befriended yet.
     */
    public static final int TARGET_REQUEST_SENT = 0x11;

    private final String player;
    private final int code;

    public SM_FRIEND_RESPONSE(String playerName, int messageType) {
        player = playerName;
        code = messageType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con) {

        writeS(player);

        /*
         * 7  = Does nothing, aka attempting to add yourself.
         * 11 = {NAME} is currently offline, but a message with your friend request will be sent to them.
         * 15 = That Character Does Not Exist.
         */
        writeC(code);
    }

}
