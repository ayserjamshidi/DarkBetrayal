/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.eltnen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */

public class _1464AGiftofLove extends QuestHandler {

    private final static int questId = 1464;

    public _1464AGiftofLove() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204424).addOnQuestStart(questId);
        qe.registerQuestNpc(204424).addOnTalkEvent(questId);
        qe.registerQuestNpc(203755).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204424) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 204424: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            long itemCount1 = player.getInventory().getItemCountByItemId(152000455);
                            if (qs.getQuestVarById(0) == 0 && itemCount1 >= 15) {
                                qs.setQuestVar(0);
                                qs.setStatus(QuestStatus.REWARD);
                                removeQuestItem(env, 152000455, 1);
                                updateQuestStatus(env);
                                return sendQuestDialog(env, 10000);
                            } else {
                                return sendQuestDialog(env, 10001);
                            }
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203755) {
                if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
