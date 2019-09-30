/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.inggison;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Leunam
 */
public class _11106RewritingHistory extends QuestHandler {

    private final static int questId = 11106;
    private final static int[] npc_ids = {798976, 798978, 798979, 203832};

    public _11106RewritingHistory() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798963).addOnQuestStart(questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (targetId == 798976) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialogId() == 1002) {
                    if (giveQuestItem(env, 182206780, 1)) {
                        return sendQuestStartDialog(env);
                    } else {
                        return true;
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203832) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        } else if (qs.getStatus() != QuestStatus.START) {
            return false;
        }
        if (targetId == 798978) {
            switch (env.getDialog()) {
                case START_DIALOG:
                    if (var == 0) {
                        return sendQuestDialog(env, 1352);
                    }
                case STEP_TO_1:
                    if (var == 0) {
                        removeQuestItem(env, 182206780, 1);
                        if (giveQuestItem(env, 182206781, 1)) {
                            qs.setQuestVarById(0, var + 1);
                        }
                        updateQuestStatus(env);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
                    return false;
            }
        } else if (targetId == 798979) {
            switch (env.getDialog()) {
                case START_DIALOG:
                    if (var == 1) {
                        return sendQuestDialog(env, 1693);
                    }
                case STEP_TO_2:
                    if (var == 1) {
                        removeQuestItem(env, 182206781, 1);
                        if (giveQuestItem(env, 182206782, 1)) {
                            qs.setQuestVarById(0, var + 1);
                        }
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
                    return false;
            }
        }
        return false;
    }
}
