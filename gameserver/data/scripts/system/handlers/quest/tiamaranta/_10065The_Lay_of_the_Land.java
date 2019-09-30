/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.tiamaranta;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * User: Alexsis
 * Date: 26.04.13
 */
public class _10065The_Lay_of_the_Land extends QuestHandler {

    private final static int questId = 10065;

    public _10065The_Lay_of_the_Land() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestNpc(800066).addOnTalkEvent(questId);
        qe.registerQuestNpc(800067).addOnTalkEvent(questId);
        qe.registerQuestNpc(800068).addOnTalkEvent(questId);
        qe.registerQuestNpc(205842).addOnTalkEvent(questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {10060};
        return defaultOnLvlUpEvent(env, quests, true);
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            return QuestService.startQuest(env);
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null)
            return false;
        int targetId = env.getTargetId();


        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 800066:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1011);
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1);
                    }
                    break;

                case 800067:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1352);
                        case STEP_TO_2:
                            return defaultCloseDialog(env, 1, 2);
                    }
                    break;

                case 800068:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1693);
                        case SET_REWARD:
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return defaultCloseDialog(env, 2, 3);
                    }
                    break;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205842) {
                switch (env.getDialog()) {
                    case SELECT_REWARD:
                        return sendQuestEndDialog(env, 10002);
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

}
