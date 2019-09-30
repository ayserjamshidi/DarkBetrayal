/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Rhys2002
 */
public class _1051TheRuinsofRoah extends QuestHandler {

    private final static int questId = 1051;
    private final static int[] npc_ids = {204501, 204582, 203882, 278503, 700303, 700217};

    public _1051TheRuinsofRoah() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestItem(182201602, questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 1500, true);
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
			if (targetId == 204501) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
						return false;
					case STEP_TO_1:
						if (var == 0) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					case STEP_TO_5:
						if (var == 4) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
						return false;
				}
			} else if (targetId == 204582) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
						return false;
					case STEP_TO_2:
						if (var == 1) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					case STEP_TO_4:
						if (var == 3) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							removeQuestItem(env, 182201601, 1);
							return true;
						}
						return false;
				}
			} else if (targetId == 203882) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					case STEP_TO_6:
						if (var == 5) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
						return false;
				}
			} else if (targetId == 278503) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					case CHECK_COLLECTED_ITEMS:
						if (QuestService.collectItemCheck(env, true)) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					case STEP_TO_7:
						if (var == 6) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					case STEP_TO_8:
						if (var == 7) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
						return false;
				}
			} else if (targetId == 700217 && qs.getQuestVarById(0) == 2) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 1693);
				} else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					if (!giveQuestItem(env, 182201601, 1)) {
						return false;
					}
					player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
					changeQuestStep(env, 2, 3, false);
					return true;
				}
			} else if (targetId == 700303 && qs.getQuestVarById(0) == 7) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return true; // loot
				}
			}
		}else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204501) { // Dionera
                return sendQuestEndDialog(env);
            }
        }
		return false;
    }
}
