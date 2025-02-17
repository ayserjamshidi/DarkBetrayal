/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.clientpackets;

import com.ne.gs.network.aion.AionClientPacket;
import com.ne.gs.network.aion.serverpackets.SM_VERSION_CHECK;

/**
 * @author -Nemesiss-
 */
public class CM_VERSION_CHECK extends AionClientPacket {

    private int version;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        version = readH(); // Version
        readH(); // Subversion
        readD(); // windowsEncoding
        readD(); // windowsVersion
        readD(); // windowsSubversion
        readC(); // unk (3.5)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        sendPacket(new SM_VERSION_CHECK(version));
    }
}
