/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.esoterrace;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Vincas
 */
public class _18405MemoriesInTheCornerOfHisMind extends QuestHandler {

    public static final int questId = 18405;
    public static final int npcDaidra = 799553, npcTillen = 799552;

    public _18405MemoriesInTheCornerOfHisMind() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(npcDaidra).addOnTalkEvent(questId);
        qe.registerQuestNpc(npcTillen).addOnTalkEvent(questId);
        qe.registerQuestItem(182215002, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();

        if (env.getTargetId() == 0 && env.getDialog() == QuestDialog.ACCEPT_QUEST) {
            QuestService.startQuest(env);
            player.sendPck(new SM_DIALOG_WINDOW(0, 0));
            return true;
        }

        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            switch (env.getTargetId()) {
                case npcDaidra:
                    if (qs.getQuestVarById(0) == 0) {
                        if (env.getDialog() == QuestDialog.START_DIALOG) {
                            return sendQuestDialog(env, 1352);
                        } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                            return defaultCloseDialog(env, 0, 1, 182215024, 1, 182215002, 1);
                        }
                    }
                case npcTillen:
                    if (qs.getQuestVarById(0) == 1) {
                        if (env.getDialog() == QuestDialog.START_DIALOG) {
                            return sendQuestDialog(env, 2375);
                        } else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
                            removeQuestItem(env, 182215024, 1);
                        }
                        return defaultCloseDialog(env, 1, 2, true, true);
                    }
            }
        }
        return sendQuestRewardDialog(env, npcTillen, 0);
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();

        if (id != 182215002) {
            return HandlerResult.FAILED;
        }
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
                sendQuestDialog(env, 4);
            }
        }, 3000);
        return HandlerResult.SUCCESS;
    }
}
