/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.greater_stigma;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author JIEgOKOJI
 * @modified kecimis
 */
public class _3930SecretoftheShatteredStigma extends QuestHandler {

    private final static int questId = 3930;

    public _3930SecretoftheShatteredStigma() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203711).addOnQuestStart(questId);// miriya start
        qe.registerQuestNpc(203833).addOnTalkEvent(questId);// Xenophon
        qe.registerQuestNpc(798321).addOnTalkEvent(questId);// Koruchinerk
        qe.registerQuestNpc(700562).addOnTalkEvent(questId);// Strongbox
        qe.registerQuestNpc(203711).addOnTalkEvent(questId);// Miriya
    }

    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        // Instanceof
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        // NPC Quest :
        // 0 - Vergelmir start
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203711)// Miriya
            {
                // Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    // Send HTML_PAGE_SELECT_NONE to eddit-HtmlPages.xml
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }

            }
        }

        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.START) {

            switch (targetId) {

                // Xenophon
                case 203833:
                    if (var == 0) {
                        switch (env.getDialog()) {
                            // Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
                            case START_DIALOG:
                                // Send select1 to eddit-HtmlPages.xml
                                return sendQuestDialog(env, 1011);
                            // Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
                            case STEP_TO_1:
                                qs.setQuestVar(1);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                        }
                    }
                    // 2 / 4- Talk with Koruchinerk
                case 798321:
                    if (var == 1) {
                        switch (env.getDialog()) {
                            // Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
                            case START_DIALOG:
                                // Send select1 to eddit-HtmlPages.xml
                                return sendQuestDialog(env, 1352);
                            // Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
                            case STEP_TO_2:
                                qs.setQuestVar(2);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                        }
                    } else if (var == 2) {
                        switch (env.getDialog()) {
                            // Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
                            case START_DIALOG:
                                // Send select1 to eddit-HtmlPages.xml
                                return sendQuestDialog(env, 1693);
                            // Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
                            case CHECK_COLLECTED_ITEMS:
                                if (player.getInventory().getItemCountByItemId(182206075) < 1) {
                                    // player doesn't own required item
                                    return sendQuestDialog(env, 10001);
                                }
                                removeQuestItem(env, 182206075, 1);
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                return sendQuestDialog(env, 10000);
                        }

                    }
                    return false;
                case 700562:
                    if (var == 2) {
                        ThreadPoolManager.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                updateQuestStatus(env);
                            }
                        }, 3000);
                        return true;
                    }
                    break;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203711)// Miriya
            {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
