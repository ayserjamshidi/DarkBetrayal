/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.portals;

import java.util.List;

import com.ne.gs.ai2.AIName;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.DialogAction;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.portal.PortalPath;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.AutoGroupService2;
import com.ne.gs.services.QuestService;
import com.ne.gs.services.teleport.PortalService;

/**
 * @author xTz
 * @reworked vlog
 */
@AIName("portal_dialog")
public class PortalDialogAI2 extends PortalAI2 {

    /**
     * Standard value. Can be changed through override
     */
    protected int teleportationDialogId = 1011;
    /**
     * Standard value. Can be changed through override
     */
    protected int rewardDialogId = 10002;
    /**
     * Standard value. Can be changed through override
     */
    protected int startingDialogId = 10;
    /**
     * Standard value. Can be changed through override
     */
    protected int questDialogId = 10;

    @Override
    protected void handleDialogStart(Player player) {
        if (getTalkDelay() == 0) {
            checkDialog(player);
        } else {
            super.handleDialogStart(player);
        }
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
        env.setExtendedRewardIndex(extendedRewardIndex);
        if (QuestEngine.getInstance().onDialog(env)) {
            return true;
        }
        if (dialogId == DialogAction.INSTANCE_PARTY_MATCH.id()) { // auto groups
            AutoGroupService2.getInstance().sendRequestEntry(player, getNpcId());
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
        } else {
            if (questId == 0) {
                PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(getNpcId(), dialogId, player.getRace());
                if (portalPath != null) {
                    PortalService.port(portalPath, player, getObjectId());
                }
            } else {
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
            }
        }
        return true;
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        checkDialog(player);
    }

    private void checkDialog(Player player) {
        int npcId = getNpcId();
        List<Integer> relatedQuests = QuestEngine.getInstance().getQuestNpc(npcId).getOnTalkEvent();
        boolean playerHasQuest = false;
        boolean playerCanStartQuest = false;
        if (!relatedQuests.isEmpty()) {
            for (int questId : relatedQuests) {
                QuestState qs = player.getQuestStateList().getQuestState(questId);
                if (qs != null && (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD)) {
                    playerHasQuest = true;
                    break;
                } else if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
                    if (QuestService.checkStartConditions(new QuestEnv(getOwner(), player, questId, 0), true)) {
                        playerCanStartQuest = true;
                        break;
                    }
                }
            }
        }

        if (playerHasQuest) { // show quest selection dialog and handle teleportation in script, if needed
            boolean isRewardStep = false;
            for (int questId : relatedQuests) {
                QuestState qs = player.getQuestStateList().getQuestState(questId);
                if (qs != null && qs.getStatus() == QuestStatus.REWARD) { // reward dialog
                    player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), rewardDialogId, questId));
                    isRewardStep = true;
                    break;
                }
            }
            if (!isRewardStep) { // normal dialog
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), questDialogId));
            }
        } else if (playerCanStartQuest) { // start quest dialog
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), startingDialogId));
        } else { // show teleportation dialog
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), teleportationDialogId, 0));
        }
    }
}
