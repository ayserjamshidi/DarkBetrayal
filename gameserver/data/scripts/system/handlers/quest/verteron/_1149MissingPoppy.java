/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.verteron;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * Find Poppy (203191), the lost Porgus. Take Poppy safely to Cannon (203145). Talk with Cannon.
 *
 * @author Rhys2002
 * @reworked vlog
 */
public class _1149MissingPoppy extends QuestHandler {

    private final static int questId = 1149;

    public _1149MissingPoppy() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203145).addOnQuestStart(questId);
        qe.registerQuestNpc(203145).addOnTalkEvent(questId);
        qe.registerQuestNpc(203191).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (targetId == 203145) { // Cannon
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (qs.getStatus() == QuestStatus.REWARD) {
                if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            } else {
                return false;
            }
        } else if (targetId == 203191) { // Poppy
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                int var = qs.getQuestVarById(0);
                if (env.getDialog() == QuestDialog.START_DIALOG && var == 0) {
                    return sendQuestDialog(env, 1352);
                } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    Npc npc = (Npc) env.getVisibleObject();
                    return defaultStartFollowEvent(env, npc, 203145, 0, 1); // 1
                }
            }
        }
        return false;
    }

    @Override
    public boolean onNpcReachTargetEvent(QuestEnv env) {
        playQuestMovie(env, 12);
        return defaultFollowEndEvent(env, 1, 1, true); // reward
    }

    @Override
    public boolean onNpcLostTargetEvent(QuestEnv env) {
        return defaultFollowEndEvent(env, 1, 0, false); // 0
    }
}
