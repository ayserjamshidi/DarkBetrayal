/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;

/**
 * @author ATracer
 */
@AIName("postbox")
public class PostboxAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 18));
        player.getMailbox().sendMailList(false);
    }

    @Override
    protected void handleDialogFinish(Player player) {
    }

}
