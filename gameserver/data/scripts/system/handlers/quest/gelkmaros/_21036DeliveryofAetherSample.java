/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.gelkmaros;

import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

public class _21036DeliveryofAetherSample extends QuestHandler {

    private final static int questId = 21036;

    public _21036DeliveryofAetherSample() {
        super(questId);
    }

    @Override
    public void register() {
        int[] npcs = {799258, 799238, 798713, 799239};
        for (int npc : npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
        qe.registerQuestNpc(799258).addOnQuestStart(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        if (sendQuestNoneDialog(env, 799258, 182207832, 1)) {
            return true;
        }

        QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        if (qs.getStatus() == QuestStatus.START) {
            if (env.getTargetId() == 799238) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
                    case STEP_TO_1:
                        return defaultCloseDialog(env, 0, 1);
                }
            } else if (env.getTargetId() == 798713) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 1) {
                            return sendQuestDialog(env, 1693);
                        }
                    case STEP_TO_2:
                        return defaultCloseDialog(env, 1, 2);
                }
            } else if (env.getTargetId() == 799239) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 2) {
                            return defaultCloseDialog(env, 2, 2, true, false);
                        }
                }
            }
        }
        return sendQuestRewardDialog(env, 799239, 2375);
    }
}
