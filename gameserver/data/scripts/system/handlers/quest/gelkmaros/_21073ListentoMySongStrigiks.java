/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.gelkmaros;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author HellBoy
 * @reworked vlog
 */
public class _21073ListentoMySongStrigiks extends QuestHandler {

    private final static int questId = 21073;

    public _21073ListentoMySongStrigiks() {
        super(questId);
    }

    @Override
    public void register() {
        int[] npcs = {799407, 799408};
        for (int npc : npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
        qe.registerQuestNpc(799407).addOnQuestStart(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799407) { // Skilving
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 799408) { // Svasuth
                if (dialog == QuestDialog.START_DIALOG) {
                    if (var == 0) {
                        return sendQuestDialog(env, 1352);
                    }
                } else if (dialog == QuestDialog.STEP_TO_1) {
                    return defaultCloseDialog(env, 0, 1); // 1
                }
            } else if (targetId == 799407) { // Skilving
                if (dialog == QuestDialog.START_DIALOG) {
                    if (var == 1) {
                        return sendQuestDialog(env, 2375);
                    }
                } else if (dialog == QuestDialog.SELECT_REWARD) {
                    changeQuestStep(env, 1, 1, true); // reward
                    return sendQuestDialog(env, 5);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799407) { // Skilving
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
