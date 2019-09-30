/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.fortuneers;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author vlog
 */
public class _38002FortuneersCallToArms extends QuestHandler {

    public static final int questId = 38002;

    public _38002FortuneersCallToArms() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(799840).addOnTalkEvent(questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (player.getLevel() >= 50 && (qs == null || qs.getStatus() == QuestStatus.NONE)) {
            return QuestService.startQuest(env);
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 799840) { // Juris Po
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 10002);
                } else if (dialog == QuestDialog.SELECT_REWARD) {
                    changeQuestStep(env, 0, 0, true);
                    return sendQuestDialog(env, 5);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799840) { // Juris Po
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
