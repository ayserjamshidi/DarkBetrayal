/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.elementis_forest;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Ritsu
 */
public class _30451KeytotheManor extends QuestHandler {

    private static final int questId = 30451;

    public _30451KeytotheManor() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799535).addOnQuestStart(questId);
        qe.registerQuestNpc(799535).addOnTalkEvent(questId);
        qe.registerQuestNpc(799582).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799535) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 799582: {
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1352);
                            }
                        }
                        case STEP_TO_1: {
                            return defaultCloseDialog(env, 0, 1);
                        }
                    }
                }

                case 799535:
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 1) {
                                return sendQuestDialog(env, 2375);
                            }

                        }
                        case CHECK_COLLECTED_ITEMS_SIMPLE: {
                            if (QuestService.collectItemCheck(env, true)) {
                                changeQuestStep(env, 1, 2, true);
                                return sendQuestDialog(env, 5);
                            } else {
                                return closeDialogWindow(env);
                            }
                        }
                    }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799535) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
