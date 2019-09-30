/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.RewardType;
import com.ne.gs.model.templates.quest.Rewards;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.WorldMapType;

/**
 * @author Balthazar
 */

public class _1640TeleporterRepairs extends QuestHandler {

    private final static int questId = 1640;

    public _1640TeleporterRepairs() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(730033).addOnQuestStart(questId);
        qe.registerQuestNpc(730033).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 730033) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1011);
                    }
                    case STEP_TO_1: {
                        QuestService.startQuest(env);
                        player.sendPck(new SM_DIALOG_WINDOW(0, 0));
                        return true;
                    }
                    default:
                        return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 730033 && env.getDialog() == QuestDialog.USE_OBJECT && player.getInventory().getItemCountByItemId(182201790) >= 1) {
                final int targetObjectId = env.getVisibleObject().getObjectId();
                PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.SIT, 0, targetObjectId), true);
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (!player.isTargeting(targetObjectId)) {
                            return;
                        }

                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                    }
                }, 3000);
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 730033) {
                removeQuestItem(env, 182201790, 1);

                if (qs == null || qs.getStatus() != QuestStatus.REWARD) {
                    return false;
                }
                Rewards rewards = DataManager.QUEST_DATA.getQuestById(questId).getRewards().get(0);
                int rewardExp = rewards.getExp();
                int rewardKinah = (int) (player.getRates().getQuestKinahRate() * rewards.getGold());
                player.getCommonData().addExp(rewardExp, RewardType.QUEST);
                giveQuestItem(env, 182400001, rewardKinah);
                qs.setStatus(QuestStatus.COMPLETE);
                qs.setCompleteCount(255);
                updateQuestStatus(env);
                player.sendPck(new SM_QUEST_ACTION(questId, QuestStatus.COMPLETE, 2));
                player.sendPck(new SM_DIALOG_WINDOW(0, 0));
                return true;
            }
        } else if (qs.getStatus() == QuestStatus.COMPLETE) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    TeleportService.teleportTo(player, WorldMapType.HEIRON.getId(), 187.71689f, 2712.14870f, 141.91672f, (byte) 195);
                }
            }, 1000);
        }
        return false;
    }
}
