/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.beluslan;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author David
 * @reworked vlog
 */
public class _2651SpyAFriendsWhereabouts extends QuestHandler {

    private final static int questId = 2651;

    public _2651SpyAFriendsWhereabouts() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204775).addOnQuestStart(questId);
        qe.registerQuestNpc(204775).addOnTalkEvent(questId);
        qe.registerQuestNpc(204764).addOnTalkEvent(questId);
        qe.registerQuestNpc(204650).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204775) { // Betoni
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 204764: { // Epona
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1352);
                            }
                        }
                        case STEP_TO_1: {
                            return defaultCloseDialog(env, 0, 1); // 1
                        }
                    }
                    break;
                }
                case 204650: { // Nesteto
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 1) {
                                return sendQuestDialog(env, 2375);
                            }
                        }
                        case SELECT_REWARD: {
                            changeQuestStep(env, 1, 1, true); // reward
                            return sendQuestDialog(env, 5);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204650) { // Nesteto
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
