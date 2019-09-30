/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.walkers;

import java.util.List;
import ai.GeneralNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.handler.MoveEventHandler;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.templates.npcshout.NpcShout;
import com.ne.gs.model.templates.npcshout.ShoutEventType;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.utils.MathUtil;

/**
 * @author Rolandas
 */
@AIName("naia")
public class NaiaAI2 extends GeneralNpcAI2 {

    boolean saidCannon = false;
    boolean saidQydro = false;

    @Override
    protected void handleMoveArrived() {
        MoveEventHandler.onMoveArrived(this);

        Npc npc2 = null;
        Npc cannon = getPosition().getWorldMapInstance().getNpc(203145);
        Npc qydro = getPosition().getWorldMapInstance().getNpc(203125);
        boolean isCannonNear = MathUtil.isIn3dRange(getOwner(), cannon, getOwner().getAggroRange());
        boolean isQydroNear = MathUtil.isIn3dRange(getOwner(), qydro, getOwner().getAggroRange());
        int delay = 0;

        List<NpcShout> shouts = null;
        if (!saidCannon && isCannonNear) {
            saidCannon = true;
            npc2 = cannon;
            delay = 10;
            // TODO: she should get closer and turn to Cannon
            // getOwner().getPosition().setH((byte)60);
            shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(getPosition().getMapId(), getNpcId(), ShoutEventType.WALK_WAYPOINT, "2", 0);
        } else if (saidCannon && !isCannonNear) {
            saidCannon = false;
        }
        if (!saidQydro && isQydroNear) {
            saidQydro = true;
            npc2 = qydro;
            shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(getPosition().getMapId(), getNpcId(), ShoutEventType.WALK_WAYPOINT, "1", 0);
        } else if (saidQydro && !isQydroNear) {
            saidQydro = false;
        }

        if (shouts != null) {
            NpcShoutsService.getInstance().shout(getOwner(), npc2, shouts, delay, false);
            shouts.clear();
        }
    }
}
