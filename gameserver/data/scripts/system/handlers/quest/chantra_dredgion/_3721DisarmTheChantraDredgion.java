/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.chantra_dredgion;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _3721DisarmTheChantraDredgion extends QuestHandler {

    private static final int questId = 3721;

    public _3721DisarmTheChantraDredgion() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798928).addOnQuestStart(questId);
        qe.registerQuestNpc(798928).addOnTalkEvent(questId);
        qe.registerQuestNpc(799069).addOnTalkEvent(questId);
        qe.registerQuestNpc(216886).addOnKillEvent(questId);
        qe.addHandlerSideQuestDrop(questId, 700836, 182202193, 1, 100, false);
        qe.registerGetingItem(182202193, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 798928) { // Yulia
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 799069) { // Yannis
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                    }
                    case STEP_TO_1: {
                        return defaultCloseDialog(env, 0, 1); // 1
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798928) { // Yulia
                if (dialog == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onGetItemEvent(QuestEnv env) {
        return defaultOnGetItemEvent(env, 1, 2, false); // 2
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        return defaultOnKillEvent(env, 216886, 2, true); // reward
    }
}
