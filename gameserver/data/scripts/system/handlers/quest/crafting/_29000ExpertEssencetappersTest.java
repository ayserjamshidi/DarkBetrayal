/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.crafting;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Gigi
 * @reworked vlog
 */
public class _29000ExpertEssencetappersTest extends QuestHandler {

    private final static int questId = 29000;

    public _29000ExpertEssencetappersTest() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204096).addOnQuestStart(questId);
        qe.registerQuestNpc(204096).addOnTalkEvent(questId);
        qe.registerQuestNpc(204097).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204096) { // Latatusk
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 204097: { // Relir
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case STEP_TO_1: {
                            if (!player.getInventory().isFull()) {
                                return defaultCloseDialog(env, 0, 1, 122001250, 1, 0, 0); // 1
                            }
                        }
                    }
                    break;
                }
                case 204096: { // Latatusk
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            if (var == 1) {
                                return sendQuestDialog(env, 1352);
                            }
                        }
                        case CHECK_COLLECTED_ITEMS: {
                            return checkQuestItems(env, 1, 1, true, 5, 10001); // reward
                        }
                        case FINISH_DIALOG: {
                            return defaultCloseDialog(env, 1, 1);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204096) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
