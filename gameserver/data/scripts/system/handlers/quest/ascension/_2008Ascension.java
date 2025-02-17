/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.ascension;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.PlayerClass;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.state.CreatureState;
import com.ne.gs.network.aion.SystemMessageId;
import com.ne.gs.network.aion.serverpackets.SM_ASCENSION_MORPH;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.ClassChangeService;
import com.ne.gs.services.QuestService;
import com.ne.gs.services.instance.InstanceService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.WorldMapInstance;

/**
 * @author MrPoke
 */
public class _2008Ascension extends QuestHandler {

    private final static int questId = 2008;

    public _2008Ascension() {
        super(questId);
    }

    @Override
    public void register() {
        if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
            return;
        }
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(203550).addOnTalkEvent(questId);
        qe.registerQuestNpc(790003).addOnTalkEvent(questId);
        qe.registerQuestNpc(790002).addOnTalkEvent(questId);
        qe.registerQuestNpc(203546).addOnTalkEvent(questId);
        qe.registerQuestNpc(205020).addOnTalkEvent(questId);
        qe.registerQuestNpc(205040).addOnKillEvent(questId);
        qe.registerQuestNpc(205041).addOnAttackEvent(questId);
        qe.registerOnMovieEndQuest(152, questId);
        qe.registerOnEnterWorld(questId);
        qe.registerOnDie(questId);
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int instanceId = player.getInstanceId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();

        if (targetId == 205040) {
            if (var >= 51 && var <= 53) {
                qs.setQuestVar(qs.getQuestVars().getQuestVars() + 1);
                updateQuestStatus(env);
                return true;
            } else if (var == 54) {
                qs.setQuestVar(5);
                updateQuestStatus(env);
                Npc mob = (Npc) QuestService.addNewSpawn(320020000, instanceId, 205041, 301f, 259f, 205.5f, (byte) 0);
                mob.getAggroList().addDamage(player, 1000);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onAttackEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 5) {
            return false;
        }
        int targetId = env.getTargetId();

        if (targetId != 205041) {
            return false;
        }
        Npc npc = (Npc) env.getVisibleObject();
        if (npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 2) {
            playQuestMovie(env, 152);
            npc.getController().onDelete();
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        env.getPlayer().sendMsg("onDialog");
        final Player player = env.getPlayer();
        final int instanceId = player.getInstanceId();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVars().getQuestVars();
        int targetId = env.getTargetId();

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203550) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        } else if (var == 6) {
                            return sendQuestDialog(env, 2716);
                        }
                    case SELECT_ACTION_2376:
                        if (var == 4) {
                            playQuestMovie(env, 57);
                            removeQuestItem(env, 182203009, 1);
                            removeQuestItem(env, 182203010, 1);
                            removeQuestItem(env, 182203011, 1);
                            return false;
                        }
                    case STEP_TO_1:
                        qs.setQuestVar(1);
                        updateQuestStatus(env);
                        TeleportService.teleportTo(player, 220010000, 585.5074f, 2416.0312f, 278.625f, (byte) 102, TeleportAnimation.BEAM_ANIMATION);
                        return true;
                    case STEP_TO_5:
                        if (var == 4) {
                            qs.setQuestVar(99);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            // Create instance
                            WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320020000);
                            InstanceService.registerPlayerWithInstance(newInstance, player);
                            TeleportService.teleportTo(player, 320020000, newInstance.getInstanceId(), 457.65f, 426.8f, 230.4f);
                            return true;
                        }
                    case STEP_TO_6:
                        if (var == 6) {
                            PlayerClass playerClass = player.getCommonData().getPlayerClass();
                            if (playerClass == PlayerClass.WARRIOR) {
                                return sendQuestDialog(env, 3057);
                            } else if (playerClass == PlayerClass.SCOUT) {
                                return sendQuestDialog(env, 3398);
                            } else if (playerClass == PlayerClass.MAGE) {
                                return sendQuestDialog(env, 3739);
                            } else if (playerClass == PlayerClass.PRIEST) {
                                return sendQuestDialog(env, 4080);
                            }
                        }
                    case STEP_TO_7:
                        if (var == 6) {
                            return setPlayerClass(env, qs, PlayerClass.GLADIATOR);
                        }
                    case STEP_TO_8:
                        if (var == 6) {
                            return setPlayerClass(env, qs, PlayerClass.TEMPLAR);
                        }
                    case STEP_TO_9:
                        if (var == 6) {
                            return setPlayerClass(env, qs, PlayerClass.ASSASSIN);
                        }
                    case STEP_TO_10:
                        if (var == 6) {
                            return setPlayerClass(env, qs, PlayerClass.RANGER);
                        }
                    case STEP_TO_11:
                        if (var == 6) {
                            return setPlayerClass(env, qs, PlayerClass.SORCERER);
                        }
                    case STEP_TO_12:
                        if (var == 6) {
                            return setPlayerClass(env, qs, PlayerClass.SPIRIT_MASTER);
                        }
                    case STEP_TO_13:
                        if (var == 6) {
                            return setPlayerClass(env, qs, PlayerClass.CHANTER);
                        }
                    case STEP_TO_14:
                        if (var == 6) {
                            return setPlayerClass(env, qs, PlayerClass.CLERIC);
                        }
                }
            } else if (targetId == 790003) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
                    case STEP_TO_2:
                        if (var == 1) {
                            if (!giveQuestItem(env, 182203009, 1)) {
                                return true;
                            }

                            qs.setQuestVar(2);
                            updateQuestStatus(env);
                            TeleportService.teleportTo(player, 220010000, 940.74475f, 2295.5305f, 265.65674f, (byte) 46, TeleportAnimation.BEAM_ANIMATION);
                            return true;
                        }
                }
            } else if (targetId == 790002) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
                    case STEP_TO_3:
                        if (var == 2) {
                            if (!giveQuestItem(env, 182203010, 1)) {
                                return true;
                            }

                            qs.setQuestVar(3);
                            updateQuestStatus(env);
                            TeleportService.teleportTo(player, 220010000, 1111.5637f, 1719.2745f, 270.114256f, (byte) 114, TeleportAnimation.BEAM_ANIMATION);
                            return true;
                        }
                }
            } else if (targetId == 203546) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
                    case STEP_TO_4:
                        if (var == 3) {
                            if (!giveQuestItem(env, 182203011, 1)) {
                                return true;
                            }

                            qs.setQuestVar(4);
                            updateQuestStatus(env);
                            TeleportService.teleportTo(player, 220010000, 383.10248f, 1895.3093f, 327.625f, (byte) 59, TeleportAnimation.BEAM_ANIMATION);
                            return true;
                        }
                }
            } else if (targetId == 205020) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 99) {
                            player.setState(CreatureState.FLIGHT_TELEPORT);
                            player.unsetState(CreatureState.ACTIVE);
                            player.setFlightTeleportId(3001);
                            player.sendPck(new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 3001, 0));
                            qs.setQuestVar(50);
                            updateQuestStatus(env);
                            ThreadPoolManager.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    qs.setQuestVar(51);
                                    updateQuestStatus(env);
                                    List<Npc> mobs = new ArrayList<>();
                                    mobs.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 294f, 277f, 207f, (byte) 0));
                                    mobs.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 305f, 279f, 206.5f, (byte) 0));
                                    mobs.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 298f, 253f, 205.7f, (byte) 0));
                                    mobs.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 306f, 251f, 206f, (byte) 0));
                                    for (Npc mob : mobs) {
                                        mob.getAggroList().addDamage(player, 1000);
                                    }
                                }
                            }, 43000);
                            return true;
                        }
                        return false;
                    default:
                        return false;
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203550) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env);
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVars().getQuestVars();
            if (var == 5 || (var >= 50 && var <= 55) || var == 99) {
                if (player.getWorldId() != 320020000) {
                    qs.setQuestVar(4);
                    updateQuestStatus(env);
                    player.sendPck(new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId)
                        .getName()));
                } else {
                    player.sendPck(new SM_ASCENSION_MORPH(1));
                    return true;
                }
            }
            if (var == 6) {
                if (player.getWorldId() == 220010000) {
                    changeQuestStep(env, 6, 6, true); // reward
                    return sendQuestDialog(env, 5);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onMovieEndEvent(QuestEnv env, int movieId) {
        if (movieId != 152) {
            return false;
        }
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 5) {
            return false;
        }
        int instanceId = player.getInstanceId();
        QuestService.addNewSpawn(320020000, instanceId, 203550, 301.92999f, 274.26001f, 205.7f, (byte) 0);
        qs.setQuestVar(6);
        updateQuestStatus(env);
        return true;
    }

    private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass) {
        Player player = env.getPlayer();
        ClassChangeService.setClass(player, playerClass);
        player.getController().upgradePlayer();
        TeleportService.teleportTo(player, 220010000, 386.03476f, 1893.9309f, 327.62283f, (byte) 59);
        return true;
    }

    @Override
    public boolean onDieEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }
        if (qs.getStatus() != QuestStatus.START) {
            return false;
        }
        int var = qs.getQuestVars().getQuestVars();
        if (var == 5 || (var >= 51 && var <= 53)) {
            qs.setQuestVar(4);
            updateQuestStatus(env);
            player.sendPck(new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
        }
        return false;
    }
}
