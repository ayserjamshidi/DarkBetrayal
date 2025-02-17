/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.pvpArenas;

import java.util.List;
import ai.ShifterAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.instance.instancereward.InstanceReward;
import com.ne.gs.model.instance.instancereward.PvPArenaReward;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("plaza_flame_thrower")
public class PlazaFlameThrowerAI2 extends ShifterAI2 {

    private boolean isRewarded;

    @Override
    protected void handleDialogStart(Player player) {
        InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
        if (instance != null && !instance.isStartProgress()) {
            return;
        }
        super.handleDialogStart(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        super.handleUseItemFinish(player);
        if (!isRewarded) {
            isRewarded = true;
            AI2Actions.handleUseItemFinish(this, player);
            switch (getNpcId()) {
                case 701169:
                    useSkill(getNpcs(701178));
                    useSkill(getNpcs(701192));
                    break;
                case 701170:
                    useSkill(getNpcs(701177));
                    useSkill(getNpcs(701191));
                    break;
                case 701171:
                    useSkill(getNpcs(701176));
                    useSkill(getNpcs(701190));
                    break;
                case 701172:
                    useSkill(getNpcs(701175));
                    useSkill(getNpcs(701189));
                    break;
            }
            AI2Actions.scheduleRespawn(this);
            AI2Actions.deleteOwner(this);
        }
    }

    private void useSkill(List<Npc> npcs) {
        PvPArenaReward instance = (PvPArenaReward) getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
        for (Npc npc : npcs) {
            int skill = instance.getNpcBonusSkill(npc.getNpcId());
            SkillEngine.getInstance().getSkill(npc, skill >> 8, skill & 0xFF, npc).useNoAnimationSkill();
        }
    }

    private List<Npc> getNpcs(int npcId) {
        return getPosition().getWorldMapInstance().getNpcs(npcId);
    }
}
