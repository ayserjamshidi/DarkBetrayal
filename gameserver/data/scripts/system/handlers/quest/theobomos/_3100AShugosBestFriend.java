/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.theobomos;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */

public class _3100AShugosBestFriend extends QuestHandler {

    private final static int questId = 3100;

    public _3100AShugosBestFriend() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203792).addOnQuestStart(questId);
        qe.registerQuestNpc(203792).addOnTalkEvent(questId);
        qe.registerQuestNpc(798168).addOnTalkEvent(questId);
        qe.registerQuestNpc(798169).addOnTalkEvent(questId);
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
            if (targetId == 203792) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1011);
                    }
                    case ACCEPT_QUEST: {
                        if (player.getInventory().getItemCountByItemId(182208072) == 0) {
                            if (!giveQuestItem(env, 182208072, 1)) {
                                return true;
                            }
                        }
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
            switch (targetId) {
                case 798168: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            if (qs.getQuestVarById(0) == 0) {
                                return sendQuestDialog(env, 1352);
                            }
                        }
                        case STEP_TO_1: {
                            removeQuestItem(env, 182208072, 1);
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798169) {
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
