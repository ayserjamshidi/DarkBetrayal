/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.database.dao;

import com.ne.commons.database.dao.DAO;
import com.ne.gs.model.gameobjects.player.BlockList;
import com.ne.gs.model.gameobjects.player.Player;

/**
 * Responsible for saving and loading data on players' block lists
 *
 * @author Ben
 */
public abstract class BlockListDAO implements DAO {

    /**
     * Loads the blocklist for the player given
     *
     * @param player
     *
     * @return BlockList
     */
    public abstract BlockList load(Player player);

    /**
     * Adds the given object id to the list of blocked players for the given player
     *
     * @param playerObjId
     *     ID of player to edit the blocklist of
     * @param objIdToBlock
     *     ID of player to add to the blocklist
     *
     * @return Success
     */
    public abstract boolean addBlockedUser(int playerObjId, int objIdToBlock, String reason);

    /**
     * Deletes the given object id from the list of blocked players for the given player
     *
     * @param playerObjId
     *     ID of player to edit the blocklist of
     * @param objIdToDelete
     *     ID of player to remove from the blocklist
     *
     * @return Success
     */
    public abstract boolean delBlockedUser(int playerObjId, int objIdToDelete);

    /**
     * Sets the reason for blocking a player
     *
     * @param playerObjId
     *     Object ID of the player whos list is being edited
     * @param blockedObjId
     *     Object ID of the player whos reason is being edited
     * @param reason
     *     The reason to be set
     *
     * @return true or false
     */
    public abstract boolean setReason(int playerObjId, int blockedObjId, String reason);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return BlockListDAO.class.getName();
    }

}
