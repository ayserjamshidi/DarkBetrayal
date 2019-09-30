/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.altgard;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Mr. Poke
 */
public class _2208MauInTenMinutesADay extends QuestHandler {

    private final static int questId = 2208;

    public _2208MauInTenMinutesADay() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203591).addOnQuestStart(questId);
        qe.registerQuestNpc(203591).addOnTalkEvent(questId);
        qe.registerQuestNpc(203589).addOnTalkEvent(questId);
        qe.registerQuestItem(182203205, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203591) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialogId() == 1002) {
                    if (giveQuestItem(env, 182203205, 1)) {
                        return sendQuestStartDialog(env);
                    }
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203589) {
                int var = qs.getQuestVarById(0);
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    if (var == 0) {
                        return sendQuestDialog(env, 1693);
                    } else if (var == 1) {
                        return sendQuestDialog(env, 1352);
                    }
                } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return sendQuestSelectionDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203591) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();

        if (id != 182203205) {
            return HandlerResult.UNKNOWN;
        }
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return HandlerResult.FAILED;
        }
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
                player.getInventory().decreaseByObjectId(itemObjId, 1);
                qs.setQuestVarById(0, 1);
                updateQuestStatus(env);
            }
        }, 3000);
        return HandlerResult.SUCCESS;
    }
}
