/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.reshanta;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.stats.AbyssRankEnum;

/**
 * [Group] Confront Asmodian Officers Fight against Asmodian Officers and win (10). -> Race = ASMO, Abyss Rank = 1 -
 * 5-Star Officer Report the result to Michalis (278501). minlevel_permitted="40" cannot_share="true"
 * race_permitted="ELYOS"
 *
 * @author vlog
 */
public class _1719ConfrontAsmodianOfficers extends QuestHandler {

    private final static int _questId = 1719;

    public _1719ConfrontAsmodianOfficers() {
        super(_questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(278501).addOnTalkEvent(_questId);
        qe.registerQuestNpc(278501).addOnQuestStart(_questId);
        qe.registerOnKillRanked(AbyssRankEnum.STAR1_OFFICER, _questId);
    }

    @Override
    public boolean onKillRankedEvent(QuestEnv env) {
        return defaultOnKillRankedEvent(env, 0, 10, true); // reward
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(_questId);

        if (env.getTargetId() == 278501) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1352);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
