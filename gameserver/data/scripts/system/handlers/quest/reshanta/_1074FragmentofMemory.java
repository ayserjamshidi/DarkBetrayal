/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.reshanta;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author MetaWind
 */
public class _1074FragmentofMemory extends QuestHandler {

    private final static int questId = 1074;

    public _1074FragmentofMemory() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(279029).addOnTalkEvent(questId);
        qe.registerQuestNpc(278501).addOnTalkEvent(questId);
        qe.registerQuestNpc(790001).addOnTalkEvent(questId);
		qe.registerQuestNpc(700355).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 278501: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        case STEP_TO_1:
                            if (var == 0) {
                                qs.setQuestVarById(0, var + 1);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                            }
                    }
                }
                break;
                case 279029: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 2) {
                                return sendQuestDialog(env, 1693);
                            }
                        case STEP_TO_3:
                            if (var == 2) {
                                qs.setQuestVarById(0, var + 1);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                                return true;
                            }
                    }
                }
                break;
				case 700355: {
                    switch (env.getDialog()) {
                        case USE_OBJECT:
                            if (var == 3) {
								if (player.getInventory().getItemCountByItemId(188020000) > 0) {
									removeQuestItem(env, 188020000, 1);
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(env);
									playQuestMovie(env, 271);
									return true;
								}
							}
                    }
                }
                break;
                case 790001: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 1) {
                                return sendQuestDialog(env, 1352);
                            }
                        case STEP_TO_2:
                            if (var == 1) {
                                qs.setQuestVarById(0, var + 1);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                            }
                    }
                }

            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 279029) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 1701, true);
    }
}
