/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.eltnen;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Ritsu
 */
public class _1345BearerOfBadNews extends QuestHandler {

    private final static int questId = 1345;

    public _1345BearerOfBadNews() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204006).addOnQuestStart(questId); // Demokritos
        qe.registerQuestNpc(204006).addOnTalkEvent(questId);
        qe.registerQuestNpc(203765).addOnTalkEvent(questId); // Kreon
        qe.registerQuestItem(182201320, questId); // Tarnished Ring
    }

    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.isInsideZone(ZoneName.get("LC1_ITEMUSEAREA_Q1345"))) {
                return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, true, 0, 0, 0));
            }
        }
        return HandlerResult.SUCCESS; // ??
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        QuestDialog dialog = env.getDialog();
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204006) {// Demokritos
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (dialog == QuestDialog.ACCEPT_QUEST) {
                    if (!giveQuestItem(env, 182201320, 1)) {
                        return true;
                    }
                    return sendQuestStartDialog(env);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 203765) {// Kreon
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            return defaultCloseDialog(env, 0, 1);
                        }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204006) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
