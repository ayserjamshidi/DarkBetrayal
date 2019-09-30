/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.pernon;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _28807BlessingsofaGreenThumb extends QuestHandler {

    private static final int questId = 28807;

    public _28807BlessingsofaGreenThumb() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(830211).addOnQuestStart(questId);
        qe.registerQuestNpc(830211).addOnTalkEvent(questId);
        qe.registerQuestNpc(730524).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 830211) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case ACCEPT_QUEST_SIMPLE:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 730524: {
                    switch (dialog) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1352);
                        case SELECT_ACTION_1353:
                            return sendQuestDialog(env, 1353);
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1);
                    }
                    break;
                }
                case 830211: {
                    switch (dialog) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 2375);
                        }
                        case SELECT_REWARD:
                            changeQuestStep(env, 1, 1, true);
                            return sendQuestDialog(env, 5);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 830211) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
