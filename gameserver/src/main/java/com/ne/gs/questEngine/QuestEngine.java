/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.questEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntProcedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.scripting.classlistener.AggregatedClassListener;
import com.ne.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.ne.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.ne.commons.scripting.scriptmanager.ScriptManager;
import com.ne.gs.GameServerError;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.dataholders.QuestsData;
import com.ne.gs.dataholders.XMLQuests;
import com.ne.gs.model.GameEngine;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.QuestTemplate;
import com.ne.gs.model.templates.quest.HandlerSideDrop;
import com.ne.gs.model.templates.quest.QuestDrop;
import com.ne.gs.model.templates.quest.QuestItems;
import com.ne.gs.model.templates.quest.QuestNpc;
import com.ne.gs.model.templates.rewards.BonusType;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.handlers.QuestHandlerLoader;
import com.ne.gs.questEngine.handlers.models.XMLQuest;
import com.ne.gs.questEngine.model.QuestActionType;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.stats.AbyssRankEnum;
import com.ne.gs.world.World;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author MrPoke, Hilgert
 * @modified vlog
 */
public class QuestEngine implements GameEngine {

    private static final Logger log = LoggerFactory.getLogger(QuestEngine.class);
    private static final TIntObjectHashMap<QuestHandler> questHandlers = new TIntObjectHashMap<>();
    private static ScriptManager scriptManager = new ScriptManager();
    private TIntObjectHashMap<QuestNpc> questNpcs = new TIntObjectHashMap<>();
    private TIntObjectHashMap<TIntArrayList> questItemRelated = new TIntObjectHashMap<>();
    private TIntObjectHashMap<TIntArrayList> questHouseItems = new TIntObjectHashMap<>();
    private TIntObjectHashMap<TIntArrayList> questItems = new TIntObjectHashMap<>();
    private TIntArrayList questOnEnterZoneMissionEnd = new TIntArrayList();
    private TIntArrayList questOnLevelUp = new TIntArrayList();
    private TIntArrayList questOnDie = new TIntArrayList();
    private TIntArrayList questOnLogOut = new TIntArrayList();
    private TIntArrayList questOnEnterWorld = new TIntArrayList();
    private Map<ZoneName, TIntArrayList> questOnEnterZone = new THashMap<>();
    private Map<ZoneName, TIntArrayList> questOnLeaveZone = new THashMap<>();
    private Map<String, TIntArrayList> questOnPassFlyingRings = new THashMap<>();
    private TIntObjectHashMap<TIntArrayList> questOnMovieEnd = new TIntObjectHashMap<>();
    private List<Integer> questOnTimerEnd = new ArrayList<>();
    private List<Integer> onInvisibleTimerEnd = new ArrayList<>();
    private Map<AbyssRankEnum, TIntArrayList> questOnKillRanked = new THashMap<>();
    private Map<Integer, TIntArrayList> questOnKillInWorld = new THashMap<>();
    private TIntObjectHashMap<TIntArrayList> questOnUseSkill = new TIntObjectHashMap<>();
    private Map<Integer, QuestDialog> dialogMap = new THashMap<>();
    private Map<Integer, Integer> questOnFailCraft = new THashMap<>();
    private Map<Integer, Set<Integer>> questOnEquipItem = new THashMap<>();
    private TIntObjectHashMap<TIntArrayList> questCanAct = new TIntObjectHashMap<>();
    private List<Integer> questOnDredgionReward = new ArrayList<>();
    private Map<BonusType, TIntArrayList> questOnBonusApply = new THashMap<>();
    private TIntArrayList reachTarget = new TIntArrayList();
    private TIntArrayList lostTarget = new TIntArrayList();

    private QuestEngine() {
    }

    public static QuestEngine getInstance() {
        return SingletonHolder.instance;
    }

    public boolean onDialog(QuestEnv env) {
        try {
            QuestHandler questHandler = null;
            if (env.getQuestId() != 0) {
                questHandler = getQuestHandlerByQuestId(env.getQuestId());
                if (questHandler != null) {
                    if (questHandler.onDialogEvent(env)) {
                        return true;
                    }
                }
            } else {
                Npc npc = (Npc) env.getVisibleObject();
                for (int questId : getQuestNpc(npc == null ? 0 : npc.getNpcId()).getOnTalkEvent()) {
                    questHandler = getQuestHandlerByQuestId(questId);
                    if (questHandler != null) {
                        env.setQuestId(questId);
                        if (questHandler.onDialogEvent(env)) {
                            return true;
                        }
                    }
                }
                env.setQuestId(0);
            }
        } catch (Exception ex) {
            log.error("QE: exception in onDialog", ex);
            return false;
        }
        return false;
    }

    public boolean onAddAggroList(QuestEnv env) {
        try {
            Npc npc = (Npc) env.getVisibleObject();
            for (int questId : getQuestNpc(npc.getNpcId()).getOnAddAggroListEvent()) {
                QuestHandler questHandler = getQuestHandlerByQuestId(questId);
                if (questHandler != null) {
                    env.setQuestId(questId);
                    questHandler.onAddAggroListEvent(env);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onAddAggroList", ex);
            return false;
        }
        return true;
    }

    public boolean onKill(QuestEnv env) {
        try {
            Npc npc = (Npc) env.getVisibleObject();
            for (int questId : getQuestNpc(npc.getNpcId()).getOnKillEvent()) {
                QuestHandler questHandler = getQuestHandlerByQuestId(questId);
                if (questHandler != null) {
                    env.setQuestId(questId);
                    questHandler.onKillEvent(env);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onKill", ex);
            return false;
        }
        return true;
    }

    public boolean onAttack(QuestEnv env) {
        try {
            Npc npc = (Npc) env.getVisibleObject();
            for (int questId : getQuestNpc(npc.getNpcId()).getOnAttackEvent()) {
                QuestHandler questHandler = getQuestHandlerByQuestId(questId);
                if (questHandler != null) {
                    env.setQuestId(questId);
                    questHandler.onAttackEvent(env);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onAttack", ex);
            return false;
        }
        return true;
    }

    public void onLvlUp(QuestEnv env) {
        try {
            Player player = env.getPlayer();
            for (int index = 0; index < questOnLevelUp.size(); index++) {
                QuestHandler questHandler = null;
                QuestState qs = player.getQuestStateList().getQuestState(questOnLevelUp.get(index));
                if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) {
                    questHandler = getQuestHandlerByQuestId(questOnLevelUp.get(index));
                }
                if (questHandler != null) {
                    env.setQuestId(questOnLevelUp.get(index));
                    questHandler.onLvlUpEvent(env);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onLvlUp", ex);
        }
    }

    public void onEnterZoneMissionEnd(QuestEnv env) {
        try {
            int result = questOnEnterZoneMissionEnd.indexOf(env.getQuestId());
            QuestHandler questHandler = null;
            if (result != -1) {
                questHandler = getQuestHandlerByQuestId(questOnEnterZoneMissionEnd.get(result));
            }
            if (questHandler != null) {
                env.setQuestId(questOnEnterZoneMissionEnd.get(result));
                questHandler.onZoneMissionEndEvent(env);
            }
        } catch (Exception ex) {
            log.error("QE: exception in onLvlUp", ex);
        }
    }

    public void onDie(QuestEnv env) {
        try {
            for (int index = 0; index < questOnDie.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(questOnDie.get(index));
                if (questHandler != null) {
                    env.setQuestId(questOnDie.get(index));
                    questHandler.onDieEvent(env);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onDie", ex);
        }
    }

    public void onLogOut(QuestEnv env) {
        try {
            for (int index = 0; index < questOnLogOut.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(questOnLogOut.get(index));
                if (questHandler != null) {
                    env.setQuestId(questOnLogOut.get(index));
                    questHandler.onLogOutEvent(env);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onLogOut", ex);
        }
    }

    public void onNpcReachTarget(QuestEnv env) {
        try {
            for (int index = 0; index < reachTarget.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(reachTarget.get(index));
                if (questHandler != null && env.getQuestId() == reachTarget.get(index)) {
                    env.setQuestId(reachTarget.get(index));
                    questHandler.onNpcReachTargetEvent(env);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onProtectEndEvent", ex);
        }
    }

    public void onNpcLostTarget(QuestEnv env) {
        try {
            for (int index = 0; index < lostTarget.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(lostTarget.get(index));
                if (questHandler != null) {
                    env.setQuestId(lostTarget.get(index));
                    questHandler.onNpcLostTargetEvent(env);
                }
            }
        }
        catch (Exception ex) {
            log.error("QE: exception in onProtectFailEvent", ex);
        }
    }

    public void onPassFlyingRing(QuestEnv env, String FlyRing) {
        try {
            TIntArrayList lists = getOnPassFlyingRingsQuests(FlyRing);
            for (int index = 0; index < lists.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
                if (questHandler != null) {
                    env.setQuestId(lists.get(index));
                    questHandler.onPassFlyingRingEvent(env, FlyRing);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onFlyRingPassEvent", ex);
        }
    }

    public void onEnterWorld(QuestEnv env) {
        try {
            for (int index = 0; index < questOnEnterWorld.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(questOnEnterWorld.get(index));
                if (questHandler != null) {
                    env.setQuestId(questOnEnterWorld.get(index));
                    questHandler.onEnterWorldEvent(env);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onEnterWorld", ex);
        }
    }

    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        try {
            TIntArrayList lists = getItemRelatedQuests(item.getItemTemplate().getTemplateId());
            for (int index = 0; index < lists.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
                if (questHandler != null) {
                    env.setQuestId(lists.get(index));
                    HandlerResult result = questHandler.onItemUseEvent(env, item);
                    // allow other quests to process, the same item can be used not in one quest
                    if (result != HandlerResult.UNKNOWN) {
                        return result;
                    }
                }
            }
            return HandlerResult.UNKNOWN;
        } catch (Exception ex) {
            log.error("QE: exception in onItemUseEvent", ex);
            return HandlerResult.FAILED;
        }
    }

    public boolean onHouseItemUseEvent(QuestEnv env, int itemId) {
        TIntArrayList lists = getHouseItemQuests(itemId);
        for (int index = 0; index < lists.size(); index++) {
            QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
            if (questHandler != null) {
                env.setQuestId(lists.get(index));
                questHandler.onHouseItemUseEvent(env, itemId);
            }
        }
        return false;
    }

    public void onItemGet(QuestEnv env, int itemId) {
        if (questItems.containsKey(itemId)) {
            for (int i = 0; i < questItems.get(itemId).size(); i++) {
                int questId = questItems.get(itemId).get(i);
                QuestHandler questHandler = getQuestHandlerByQuestId(questId);
                if (questHandler != null) {
                    env.setQuestId(questId);
                    questHandler.onGetItemEvent(env);
                }
            }
        }
    }

    public boolean onKillRanked(QuestEnv env, AbyssRankEnum playerRank) {
        try {
            if (playerRank != null) {
                TIntArrayList questList = getOnKillRankedQuests(playerRank);
                for (int index = 0; index < questList.size(); index++) {
                    int id = questList.get(index);
                    QuestHandler questHandler = getQuestHandlerByQuestId(id);
                    if (questHandler != null) {
                        env.setQuestId(id);
                        questHandler.onKillRankedEvent(env);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onKillRanked", ex);
            return false;
        }
        return true;
    }

    public boolean onKillInWorld(QuestEnv env, int worldId) {
        try {
            if (questOnKillInWorld.containsKey(worldId)) {
                TIntArrayList killInWorldQuests = questOnKillInWorld.get(worldId);
                for (int i = 0; i < killInWorldQuests.size(); i++) {
                    QuestHandler questHandler = getQuestHandlerByQuestId(killInWorldQuests.get(i));
                    if (questHandler != null) {
                        env.setQuestId(killInWorldQuests.get(i));
                        questHandler.onKillInWorldEvent(env);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onKillInWorld", ex);
            return false;
        }
        return true;
    }

    public boolean onEnterZone(QuestEnv env, ZoneName zoneName) {
        try {
            TIntArrayList lists = getOnEnterZoneQuests(zoneName);
            for (int index = 0; index < lists.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
                if (questHandler != null) {
                    env.setQuestId(lists.get(index));
                    questHandler.onEnterZoneEvent(env, zoneName);
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onEnterZone", ex);
            return false;
        }
        return true;
    }

    public boolean onLeaveZone(QuestEnv env, ZoneName zoneName) {
        try {
            if (questOnLeaveZone.containsKey(zoneName)) {
                TIntArrayList leaveZoneList = questOnLeaveZone.get(zoneName);
                for (int i = 0; i < leaveZoneList.size(); i++) {
                    QuestHandler questHandler = getQuestHandlerByQuestId(leaveZoneList.get(i));
                    if (questHandler != null) {
                        env.setQuestId(leaveZoneList.get(i));
                        questHandler.onLeaveZoneEvent(env, zoneName);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onLeaveZone", ex);
            return false;
        }
        return true;
    }

    public boolean onMovieEnd(QuestEnv env, int movieId) {
        try {
            TIntArrayList onMovieEndQuests = getOnMovieEndQuests(movieId);
            for (int index = 0; index < onMovieEndQuests.size(); index++) {
                env.setQuestId(onMovieEndQuests.get(index));
                QuestHandler questHandler = getQuestHandlerByQuestId(env.getQuestId());
                if (questHandler != null) {
                    if (questHandler.onMovieEndEvent(env, movieId)) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onMovieEnd", ex);
        }
        return false;
    }

    public void onQuestTimerEnd(QuestEnv env) {
        for (int questId : questOnTimerEnd) {
            QuestHandler questHandler = getQuestHandlerByQuestId(questId);
            if (questHandler != null) {
                env.setQuestId(questId);
            }
            questHandler.onQuestTimerEndEvent(env);
        }
    }

    public void onInvisibleTimerEnd(QuestEnv env) {
        for (int questId : onInvisibleTimerEnd) {
            QuestHandler questHandler = getQuestHandlerByQuestId(questId);
            if (questHandler != null) {
                env.setQuestId(questId);
                questHandler.onQuestTimerEndEvent(env);
            }
        }
    }

    public boolean onUseSkill(QuestEnv env, int skillId) {
        try {
            if (questOnUseSkill.containsKey(skillId)) {
                TIntArrayList quests = questOnUseSkill.get(skillId);
                for (int i = 0; i < quests.size(); i++) {
                    QuestHandler questHandler = getQuestHandlerByQuestId(quests.get(i));
                    if (questHandler != null) {
                        env.setQuestId(quests.get(i));
                        questHandler.onUseSkillEvent(env, skillId);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("QE: exception in onUseSkill", ex);
            return false;
        }
        return true;
    }

    public void onFailCraft(QuestEnv env, int itemId) {
        if (questOnFailCraft.containsKey(itemId)) {
            int questId = questOnFailCraft.get(itemId);
            QuestHandler questHandler = getQuestHandlerByQuestId(questId);
            if (questHandler != null) {
                if (env.getPlayer().getInventory().getItemCountByItemId(itemId) == 0) {
                    env.setQuestId(questId);
                    questHandler.onFailCraftEvent(env, itemId);
                }
            }
        }
    }

    public void onEquipItem(QuestEnv env, int itemId) {
        if (questOnEquipItem.containsKey(itemId)) {
            Set<Integer> questIds = questOnEquipItem.get(itemId);
            for (int questId : questIds) {
                QuestHandler questHandler = getQuestHandlerByQuestId(questId);
                if (questHandler != null) {
                    env.setQuestId(questId);
                    questHandler.onEquipItemEvent(env, itemId);
                }
            }
        }
    }

    public boolean onCanAct(final QuestEnv env, int templateId, final QuestActionType questActionType, final Object... objects) {
        if (questCanAct.containsKey(templateId)) {
            TIntArrayList questIds = questCanAct.get(templateId);
            return !questIds.forEach(new TIntProcedure() {
                @Override
                public boolean execute(int value) {
                    QuestHandler questHandler = getQuestHandlerByQuestId(value);
                    if (questHandler != null) {
                        env.setQuestId(value);
                        if (questHandler.onCanAct(env, questActionType, objects)) {
                            return false; // Abort for
                        }
                    }
                    return true;
                }
            });
        }
        return false;
    }

    public void onDredgionReward(QuestEnv env) {
        for (int questId : questOnDredgionReward) {
            QuestHandler questHandler = getQuestHandlerByQuestId(questId);
            if (questHandler != null) {
                env.setQuestId(questId);
                questHandler.onDredgionRewardEvent(env);
            }
        }
    }

    public HandlerResult onBonusApplyEvent(QuestEnv env, BonusType bonusType, List<QuestItems> rewardItems) {
        try {
            TIntArrayList lists = this.getOnBonusApplyQuests(bonusType);
            for (int index = 0; index < lists.size(); index++) {
                QuestHandler questHandler = getQuestHandlerByQuestId(lists.get(index));
                if (questHandler != null) {
                    env.setQuestId(lists.get(index));
                    return questHandler.onBonusApplyEvent(env, bonusType, rewardItems);
                }
            }
            return HandlerResult.UNKNOWN;
        } catch (Exception ex) {
            log.error("QE: exception in onBonusApply", ex);
            return HandlerResult.FAILED;
        }
    }

    public QuestNpc registerQuestNpc(int npcId) {
        if (!questNpcs.containsKey(npcId)) {
            questNpcs.put(npcId, new QuestNpc(npcId));
        }
        return questNpcs.get(npcId);
    }

    public void registerQuestItem(int itemId, int questId) {
        if (!questItemRelated.containsKey(itemId)) {
            TIntArrayList itemRelatedQuests = new TIntArrayList();
            itemRelatedQuests.add(questId);
            questItemRelated.put(itemId, itemRelatedQuests);
        } else {
            questItemRelated.get(itemId).add(questId);
        }
    }

    public void registerQuestHouseItem(int itemId, int questId) {
        if (!questHouseItems.containsKey(itemId)) {
            TIntArrayList itemRelatedQuests = new TIntArrayList();
            itemRelatedQuests.add(questId);
            questHouseItems.put(itemId, itemRelatedQuests);
        } else {
            questHouseItems.get(itemId).add(questId);
        }
    }

    public void registerGetingItem(int itemId, int questId) {
        if (!questItems.containsKey(itemId)) {
            TIntArrayList questItemsToReg = new TIntArrayList();
            questItemsToReg.add(questId);
            questItems.put(itemId, questItemsToReg);
        } else {
            questItems.get(itemId).add(questId);
        }
    }

    public void registerOnLevelUp(int questId) {
        if (!questOnLevelUp.contains(questId)) {
            questOnLevelUp.add(questId);
        }
    }

    public void registerOnEnterZoneMissionEnd(int questId) {
        if (!questOnEnterZoneMissionEnd.contains(questId)) {
            questOnEnterZoneMissionEnd.add(questId);
        }
    }

    public void registerOnEnterWorld(int questId) {
        if (!questOnEnterWorld.contains(questId)) {
            questOnEnterWorld.add(questId);
        }
    }

    public void registerOnDie(int questId) {
        if (!questOnDie.contains(questId)) {
            questOnDie.add(questId);
        }
    }

    public void registerOnLogOut(int questId) {
        if (!questOnLogOut.contains(questId)) {
            questOnLogOut.add(questId);
        }
    }

    public void registerOnEnterZone(ZoneName zoneName, int questId) {
        if (!questOnEnterZone.containsKey(zoneName)) {
            TIntArrayList onEnterZoneQuests = new TIntArrayList();
            onEnterZoneQuests.add(questId);
            questOnEnterZone.put(zoneName, onEnterZoneQuests);
        } else {
            questOnEnterZone.get(zoneName).add(questId);
        }
    }

    public void registerOnLeaveZone(ZoneName zoneName, int questId) {
        if (!questOnLeaveZone.containsKey(zoneName)) {
            TIntArrayList onLeaveZoneQuests = new TIntArrayList();
            onLeaveZoneQuests.add(questId);
            questOnLeaveZone.put(zoneName, onLeaveZoneQuests);
        } else {
            questOnLeaveZone.get(zoneName).add(questId);
        }
    }

    public void registerOnKillRanked(AbyssRankEnum playerRank, int questId) {
        for (int rank = playerRank.getId(); rank < 19; rank++) {
            if (!questOnKillRanked.containsKey(AbyssRankEnum.getRankById(rank))) {
                TIntArrayList onKillRankedQuests = new TIntArrayList();
                onKillRankedQuests.add(questId);
                questOnKillRanked.put(AbyssRankEnum.getRankById(rank), onKillRankedQuests);
            } else {
                questOnKillRanked.get(AbyssRankEnum.getRankById(rank)).add(questId);
            }
        }
    }

    public void registerOnKillInWorld(int worldId, int questId) {
        if (!questOnKillInWorld.containsKey(worldId)) {
            TIntArrayList killInWorldQuests = new TIntArrayList();
            killInWorldQuests.add(questId);
            questOnKillInWorld.put(worldId, killInWorldQuests);
        } else {
            questOnKillInWorld.get(worldId).add(questId);
        }
    }

    public void registerOnPassFlyingRings(String flyingRing, int questId) {
        if (!questOnPassFlyingRings.containsKey(flyingRing)) {
            TIntArrayList onPassFlyingRingsQuests = new TIntArrayList();
            onPassFlyingRingsQuests.add(questId);
            questOnPassFlyingRings.put(flyingRing, onPassFlyingRingsQuests);
        } else {
            questOnPassFlyingRings.get(flyingRing).add(questId);
        }
    }

    public void registerOnMovieEndQuest(int moveId, int questId) {
        if (!questOnMovieEnd.containsKey(moveId)) {
            TIntArrayList onMovieEndQuests = new TIntArrayList();
            onMovieEndQuests.add(questId);
            questOnMovieEnd.put(moveId, onMovieEndQuests);
        } else {
            questOnMovieEnd.get(moveId).add(questId);
        }
    }

    public void registerOnQuestTimerEnd(int questId) {
        if (!questOnTimerEnd.contains(questId)) {
            questOnTimerEnd.add(questId);
        }
    }

    public void registerAddOnReachTargetEvent(int questId) {
        if (!reachTarget.contains(questId))
            reachTarget.add(questId);
    }

    public void registerAddOnLostTargetEvent(int questId) {
        if (!lostTarget.contains(questId))
            lostTarget.add(questId);
    }

    public void registerOnInvisibleTimerEnd(int questId) {
        if (!onInvisibleTimerEnd.contains(Integer.valueOf(questId))) {
            onInvisibleTimerEnd.add(questId);
        }
    }

    public void registerQuestSkill(int skillId, int questId) {
        if (!questOnUseSkill.containsKey(skillId)) {
            TIntArrayList questSkills = new TIntArrayList();
            questSkills.add(questId);
            questOnUseSkill.put(skillId, questSkills);
        } else {
            questOnUseSkill.get(skillId).add(questId);
        }
    }

    public void registerOnFailCraft(int itemId, int questId) {
        if (!questOnFailCraft.containsKey(itemId)) {
            questOnFailCraft.put(itemId, questId);
        }
    }

    public void registerOnEquipItem(int itemId, int questId) {
        if (!questOnEquipItem.containsKey(itemId)) {
            Set<Integer> questIds = new HashSet<>();
            questIds.add(questId);
            questOnEquipItem.put(itemId, questIds);
        } else {
            questOnEquipItem.get(itemId).add(questId);
        }
    }

    public void registerCanAct(int questId, int templateId) {
        if (!questCanAct.containsKey(templateId)) {
            TIntArrayList questSkills = new TIntArrayList();
            questSkills.add(questId);
            questCanAct.put(templateId, questSkills);
        } else {
            questCanAct.get(templateId).add(questId);
        }
    }

    public void registerOnDredgionReward(int questId) {
        if (!questOnDredgionReward.contains(questId)) {
            questOnDredgionReward.add(questId);
        }
    }

    public void registerOnBonusApply(int questId, BonusType bonusType) {
        if (!questOnBonusApply.containsKey(bonusType)) {
            TIntArrayList onBonusApplyQuests = new TIntArrayList();
            onBonusApplyQuests.add(questId);
            questOnBonusApply.put(bonusType, onBonusApplyQuests);
        } else {
            questOnBonusApply.get(bonusType).add(questId);
        }
    }

    private TIntArrayList getOnBonusApplyQuests(BonusType bonusType) {
        if (questOnBonusApply.containsKey(bonusType)) {
            return questOnBonusApply.get(bonusType);
        }
        return new TIntArrayList();
    }

    public QuestNpc getQuestNpc(int npcId) {
        if (questNpcs.containsKey(npcId)) {
            return questNpcs.get(npcId);
        }
        return new QuestNpc(npcId);
    }

    public QuestDialog getDialog(int dialogId) {
        if (dialogMap.containsKey(dialogId)) {
            return dialogMap.get(dialogId);
        }
        return null;
    }

    private TIntArrayList getItemRelatedQuests(int itemId) {
        if (questItemRelated.containsKey(itemId)) {
            return questItemRelated.get(itemId);
        }
        return new TIntArrayList();
    }

    private TIntArrayList getHouseItemQuests(int itemId) {
        if (questHouseItems.containsKey(itemId)) {
            return questHouseItems.get(itemId);
        }
        return new TIntArrayList();
    }

    private TIntArrayList getOnEnterZoneQuests(ZoneName zoneName) {
        if (questOnEnterZone.containsKey(zoneName)) {
            return questOnEnterZone.get(zoneName);
        }
        return new TIntArrayList();
    }

    private TIntArrayList getOnKillRankedQuests(AbyssRankEnum playerRank) {
        if (questOnKillRanked.containsKey(playerRank)) {
            return questOnKillRanked.get(playerRank);
        }
        return new TIntArrayList();
    }

    private TIntArrayList getOnPassFlyingRingsQuests(String flyingRing) {
        if (questOnPassFlyingRings.containsKey(flyingRing)) {
            return questOnPassFlyingRings.get(flyingRing);
        }
        return new TIntArrayList();
    }

    private TIntArrayList getOnMovieEndQuests(int moveId) {
        if (questOnMovieEnd.containsKey(moveId)) {
            return questOnMovieEnd.get(moveId);
        }
        return new TIntArrayList();
    }

    private QuestHandler getQuestHandlerByQuestId(int questId) {
        return questHandlers.get(questId);
    }

    public boolean isHaveHandler(int questId) {
        return questHandlers.containsKey(questId);
    }

    public void addQuestHandler(QuestHandler questHandler) {
        questHandler.register();
        int questId = questHandler.getQuestId();
        if (questHandlers.containsKey(questId)) {
            log.warn("Duplicate quest: " + questId);
        }
        questHandlers.put(questId, questHandler);
    }

    /**
     * Add handler side drop (if not already in xml)
     */
    public void addHandlerSideQuestDrop(int questId, int npcId, int itemId, int amount, int chance, boolean dropEachMember) {
        HandlerSideDrop hsd = new HandlerSideDrop(questId, npcId, itemId, amount, chance, dropEachMember);
        QuestService.addQuestDrop(hsd.getNpcId(), hsd);
    }

    // Loading the QE on start up
    public void load(CountDownLatch progressLatch) {
        log.info("Quest engine load started");
        QuestsData questData = DataManager.QUEST_DATA;
        for (QuestTemplate data : questData.getQuestsData()) {
            for (QuestDrop drop : data.getQuestDrop()) {
                drop.setQuestId(data.getId());
                QuestService.addQuestDrop(drop.getNpcId(), drop);
            }
        }
        scriptManager = new ScriptManager();

        AggregatedClassListener acl = new AggregatedClassListener();
        acl.addClassListener(new OnClassLoadUnloadListener());
        acl.addClassListener(new ScheduledTaskClassListener());
        acl.addClassListener(new QuestHandlerLoader());
        scriptManager.setGlobalClassListener(acl);

        try {
            File questDescription = new File("./data/scripts/system/quest_handlers.xml");
            scriptManager.load(questDescription);
            XMLQuests xmlQuests = DataManager.XML_QUESTS;
            for (XMLQuest xmlQuest : xmlQuests.getQuest()) {
                xmlQuest.register(this);
            }
            log.info("Loaded " + questHandlers.size() + " quest handlers.");
        } catch (Exception e) {
            throw new GameServerError("Can't initialize quest handlers.", e);
        } finally {
            if (progressLatch != null) {
                progressLatch.countDown();
            }
        }
        addMessageSendingTask();
        for (QuestDialog d : QuestDialog.values()) {
            dialogMap.put(d.id(), d);
        }
    }

    private void addMessageSendingTask() {
        Calendar sendingDate = Calendar.getInstance();
        sendingDate.set(Calendar.AM_PM, Calendar.AM);
        sendingDate.set(Calendar.HOUR, 9);
        sendingDate.set(Calendar.MINUTE, 0);
        sendingDate.set(Calendar.SECOND, 0); // current date 09:00
        if (sendingDate.getTime().getTime() < System.currentTimeMillis()) {
            sendingDate.add(Calendar.HOUR, 24); // next day 09:00
        }
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                SM_SYSTEM_MESSAGE dailyMessage = new SM_SYSTEM_MESSAGE(1400854);
                SM_SYSTEM_MESSAGE weeklyMessage = new SM_SYSTEM_MESSAGE(1400856);
                for (Player player : World.getInstance().getAllPlayers()) {
                    for (QuestState qs : player.getQuestStateList().getAllQuestState()) {
                        if (qs != null && qs.canRepeat()) {
                            QuestTemplate template = DataManager.QUEST_DATA.getQuestById(qs.getQuestId());
                            if (template.isDaily()) {
                                player.getController().updateNearbyQuests();
                                player.sendPck(dailyMessage);
                            } else if (template.isWeekly()) {
                                player.getController().updateNearbyQuests();
                                player.sendPck(weeklyMessage);
                            }
                        }
                    }
                    player.getNpcFactions().sendDailyQuest();
                }
            }
        }, sendingDate.getTimeInMillis() - System.currentTimeMillis(), 1000 * 60 * 60 * 24);
    }

    // Clearing the QE on reload admin command
    public void shutdown() {
        scriptManager.shutdown();
        clear();
        scriptManager = null;
        log.info("Quests are shutdown...");
    }

    public void clear() {
        questNpcs.clear();
        questItemRelated.clear();
        questItems.clear();
        questHouseItems.clear();
        questOnLevelUp.clear();
        questOnEnterZoneMissionEnd.clear();
        questOnEnterWorld.clear();
        questOnDie.clear();
        questOnLogOut.clear();
        questOnEnterZone.clear();
        questOnLeaveZone.clear();
        questOnMovieEnd.clear();
        questOnTimerEnd.clear();
        questOnPassFlyingRings.clear();
        questOnKillRanked.clear();
        questOnUseSkill.clear();
        questHandlers.clear();
    }

    @SuppressWarnings("synthetic-access")
    private static final class SingletonHolder {

        protected static final QuestEngine instance = new QuestEngine();
    }
}
