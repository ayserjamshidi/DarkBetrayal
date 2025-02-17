/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.controllers.effect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mw.TempConst;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.skillengine.model.*;
import javolution.util.FastMap;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.ne.gs.skillengine.effect.AbnormalState;
import com.ne.gs.skillengine.effect.EffectTemplate;
import com.ne.gs.skillengine.effect.EffectType;
import com.ne.gs.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.ne.gs.utils.PacketSendUtility;
/**
 * @author ATracer modified by Wakizashi, Sippolo
 */
public class EffectController implements Iterable<Effect>{
    private final Creature owner;
    protected Map<String, Effect> passiveEffectMap = new FastMap<String, Effect>().shared();
    protected Map<String, Effect> noshowEffects = new FastMap<String, Effect>().shared();
    protected Map<String, Effect> abnormalEffectMap = new FastMap<String, Effect>().shared();
    private final Lock lock = new ReentrantLock();
    protected int abnormals;
    private boolean isUnderShield = false;
    public EffectController(Creature owner) {
        this.owner = owner;
    }
    /**
     * @return the owner
     */
    public Creature getOwner() {
        return owner;
    }
    public boolean isUnderShield() {
        return isUnderShield;
    }
    public void setUnderShield(boolean isUnderShield) {
        this.isUnderShield = isUnderShield;
    }
    public void addEffect(Effect nextEffect) {
        Map<String, Effect> mapToUpdate = getMapForEffect(nextEffect);
        lock.lock();
        try {
            boolean useEffectId = true;
            Effect existingEffect = mapToUpdate.get(nextEffect.getStack());
            if (existingEffect != null) {
                // check stack level
                if (existingEffect.getSkillStackLvl() > nextEffect.getSkillStackLvl()) {
                    return;
                }
                // check skill level (when stack level same)
                if (existingEffect.getSkillStackLvl() == nextEffect.getSkillStackLvl() && existingEffect.getSkillLevel() > nextEffect.getSkillLevel()) {
                    return;
                }
                existingEffect.endEffect();
                useEffectId = false;
            }
            Effect conflictedEffect = findConflictedEffect(mapToUpdate, nextEffect);
            if (conflictedEffect != null) {
                conflictedEffect.endEffect();
                useEffectId = false;
            }
            if (useEffectId) {
                /**
                 * idea here is that effects with same effectId shouldnt stack effect with higher basiclvl takes priority
                 */
                for (Effect ef : mapToUpdate.values()) {
                    if (ef.getTargetSlot() == nextEffect.getTargetSlot()) {
                        for (EffectTemplate et : ef.getEffectTemplates()) {
                            if (et.getEffectid() == 0) {
                                continue;
                            }
                            /* TODO(ViAl): figure out what for this was introduced
                             * if(et.getEffectType() == EffectType.SHIELD) {
                                continue;
                            }*/
                            for (EffectTemplate et2 : nextEffect.getEffectTemplates()) {
                                if (et2.getEffectid() == 0) {
                                    continue;
                                }
                                if (et.getEffectid() == et2.getEffectid()) {

                                    if (ef.getSkillTemplate().getStigmaType().getId() < nextEffect.getSkillTemplate().getStigmaType().getId()
                                            && ef.getTargetSlot() == nextEffect.getTargetSlot()
                                            && ef.getTargetSlotLevel() == nextEffect.getTargetSlotLevel()) {
                                        continue;
                                    }

                                    if (et.getBasicLvl() > et2.getBasicLvl()) {
                                        return;
                                    } else {
                                        ef.endEffect();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (nextEffect.isToggle() && mapToUpdate.size() >= 3) {
                Iterator<Effect> iter = mapToUpdate.values().iterator();
                Effect effect = iter.next();
                effect.endEffect();
                iter.remove();
            }
            if (nextEffect.isChant()) {
                Collection<Effect> chants = getChantEffects();
                if (chants.size() >= 4) {
                    Iterator<Effect> chantIter = chants.iterator();
                    chantIter.next().endEffect();
                }
            }
            if (!nextEffect.isPassive()) {
                if (searchConflict(nextEffect)) {
                    return;
                }
                checkEffectCooldownId(nextEffect);
            }
            mapToUpdate.put(nextEffect.getStack(), nextEffect);
            // TODO hex1r0: make sure this does not produce deadlocks
            nextEffect.startEffect(false);
        }
        finally {
            lock.unlock();
        }
        if (!nextEffect.isPassive()) {
            broadCastEffects();
        }
    }
    /**
     * @param mapToUpdate
     * @return
     */
    private Effect findConflictedEffect(Map<String, Effect> mapToUpdate, Effect newEffect) {
        int conflictId = newEffect.getSkillTemplate().getConflictId();
        if (conflictId == 0) {
            return null;
        }
        for (Effect effect : mapToUpdate.values()) {
            if (effect.getSkillTemplate().getCooldownId() == conflictId) {
                return effect;
            }
        }
        return null;
    }
    /**
     * @param effect
     *
     * @return
     */
    private Map<String, Effect> getMapForEffect(Effect effect) {
        if (effect.isPassive()) {
            return passiveEffectMap;
        }
        if (effect.isToggle()) {
            return noshowEffects;
        }
        return abnormalEffectMap;
    }
    public Map<String, Effect> getAnormalEffect() {
        return abnormalEffectMap;
    }
    /**
     * @param stack
     *
     * @return abnormalEffectMap
     */
    public Effect getAnormalEffect(String stack) {
        return abnormalEffectMap.get(stack);
    }
    /**
     * @param skillId
     *
     * @return
     */
    public boolean hasAbnormalEffect(int skillId) {
        for (Effect localEffect : abnormalEffectMap.values()) {
            if (localEffect.getSkillId() == skillId) {
                return true;
            }
        }
        return false;
    }
    public void broadCastEffects() {
        owner.addPacketBroadcastMask(BroadcastMode.BROAD_CAST_EFFECTS);
    }
    /**
     * Broadcasts current effects to all visible objects
     */
    public void broadCastEffectsImp() {
        List<Effect> effects = getAbnormalEffects();
        PacketSendUtility.broadcastPacket(getOwner(), new SM_ABNORMAL_EFFECT(getOwner(), abnormals, effects));
    }
    /**
     * Used when player see new player
     *
     * @param player
     */
    public void sendEffectIconsTo(Player player) {
        List<Effect> effects = getAbnormalEffects();
        player.sendPck(new SM_ABNORMAL_EFFECT(getOwner(), abnormals, effects));
    }
    /**
     * @param effect
     */
    public void clearEffect(Effect effect) {
        Map<String, Effect> mapForEffect = getMapForEffect(effect);
        mapForEffect.remove(effect.getStack());
        broadCastEffects();
    }
    /**
     * Removes the effect by skillid.
     *
     * @param skillid
     */
    public void removeEffect(int skillid) {
        for (Effect effect : abnormalEffectMap.values()) {
            if (effect.getSkillId() == skillid) {
                effect.endEffect();
            }
        }
        for (Effect effect : passiveEffectMap.values()) {
            if (effect.getSkillId() == skillid) {
                effect.endEffect();
            }
        }
        for (Effect effect : noshowEffects.values()) {
            if (effect.getSkillId() == skillid) {
                effect.endEffect();
            }
        }
    }
    /**
     * Removes the effect by SkillSetException Number.
     *
     * @param maxOccur
     *     Number
     */
    public void removeEffectBySetNumber(int setNumber, int maxOccur) {
        removeEffectBySetNumber(setNumber, maxOccur, 0);
    }
    /**
     * Removes the effect by SkillSetException Number.
     *
     * @param maxOccur
     *     Number
     * @param skillId
     *     of the effect that is being applied
     */
    public void removeEffectBySetNumber(int setNumber, int maxOccur, int skillId) {
        int i = 0;
        // Count the occurences of effects of the setNumber.
        for (Effect effect : abnormalEffectMap.values()) {
            if (effect.getSkillSetException() == setNumber && effect.getSkillId() != skillId) {
                i++;
            }
        }
        // if there are too much occurences of effects of the setNumber then remove the oldest effect.
        if (maxOccur <= i) {
            for (Effect effect : abnormalEffectMap.values()) {
                if (effect.getSkillSetException() == setNumber) {
                    effect.endEffect();
                    break;
                }
            }
        }
        i = 0;
        for (Effect effect : passiveEffectMap.values()) {
            if (effect.getSkillSetException() == setNumber) {
                i++;
            }
        }
        if (maxOccur <= i) {
            for (Effect effect : passiveEffectMap.values()) {
                if (effect.getSkillSetException() == setNumber) {
                    effect.endEffect();
                    break;
                }
            }
        }
        i = 0;
        for (Effect effect : noshowEffects.values()) {
            if (effect.getSkillSetException() == setNumber) {
                i++;
            }
        }
        if (maxOccur <= i) {
            for (Effect effect : noshowEffects.values()) {
                if (effect.getSkillSetException() == setNumber) {
                    effect.endEffect();
                    break;
                }
            }
        }
    }
    /**
     * Removes the effect with SkillSetException Reserved Number (aka 1).
     */
    public void removeEffectWithSetNumberReserved() {
        removeEffectBySetNumber(1, 1);
    }
    /**
     * @param effectId
     */
    public void removeEffectByEffectId(int effectId) {
        for (Effect effect : abnormalEffectMap.values()) {
            if (effect.containsEffectId(effectId)) {
                effect.endEffect();
            }
        }
    }
    /**
     * Method used to calculate number of effects of given dispelcategory, targetslot and dispelLevel used only in DispelBuffCounterAtk, therefore rest of cases
     * are skipped
     *
     * @param dispelLevel
     *
     * @return
     */
    public int calculateNumberOfEffects(int dispelLevel) {
        int number = 0;
        for (Effect effect : abnormalEffectMap.values()) {
            DispelCategoryType dispelCat = effect.getDispelCategory();
            SkillTargetSlot tragetSlot = effect.getSkillTemplate().getTargetSlot();
            //effects with duration 86400000 cant be dispelled
            //TODO recheck
            if (effect.getDuration() >= 86400000 && !removebleEffect(effect))
                continue;
            //check for targetslot, effects with target slot higher or equal to 2 cant be removed (ex. skillId: 11885)
            if (tragetSlot != SkillTargetSlot.BUFF && (tragetSlot != SkillTargetSlot.DEBUFF && dispelCat != DispelCategoryType.ALL) || effect.getTargetSlotLevel() >= 2)
                continue;
            switch (dispelCat) {
                case ALL:
                case BUFF://DispelBuffCounterAtkEffect
                    if (effect.getReqDispelLevel() <= dispelLevel)
                        number++;
                    break;
            }
        }
        return number;
    }
    public void dispelBuffCounterAtkEffect(int count, int dispelLevel, int power) {
        for (Effect effect : abnormalEffectMap.values()) {
            DispelCategoryType dispelCat = effect.getDispelCategory();
            SkillTargetSlot tragetSlot = effect.getSkillTemplate().getTargetSlot();
            if (count == 0)
                break;
            if (effect.getDuration() >= 86400000 && !removebleEffect(effect)) {
                continue;
            }
            if (tragetSlot != SkillTargetSlot.BUFF && (tragetSlot != SkillTargetSlot.DEBUFF && dispelCat != DispelCategoryType.ALL) || effect.getTargetSlotLevel() >= 2)
                continue;

            if(effect.getSkillTemplate().getStack().equalsIgnoreCase(TempConst.SKILL_STACK_SKILL_EL_MANAREVERSE))
                continue;

            boolean remove = false;
            switch (dispelCat) {
                case ALL:
                case BUFF:
                    if (effect.getReqDispelLevel() <= dispelLevel)
                        remove = true;
                    break;
            }
            if (remove) {
                if (removePower(effect, power)) {
                    effect.endEffect();
                    abnormalEffectMap.remove(effect.getStack());
                }
                count--;
            }
        }
    }
    private boolean removebleEffect(Effect effect) {
        int skillId = effect.getSkillId();
        switch (skillId) {
            case 20938:
            case 20939:
            case 20940:
            case 20941:
            case 20942:
            case 19370:
            case 19371:
            case 19372:
            case 20530:
            case 20531:
            case 19345:
            case 19346:
                //TODO
                return true;
            default:
                return false;
        }
    }
    public void removeEffectByDispelCat(DispelCategoryType dispelCat, SkillTargetSlot targetSlot, int count, int dispelLevel,
                                        int power, boolean itemTriggered) {
        for (Effect effect : abnormalEffectMap.values()) {
            if (count == 0)
                break;
            //effects with duration 86400000 cant be dispelled
            //TODO recheck
            if (effect.getDuration() >= 86400000 && !removebleEffect(effect)) {
                continue;
            }
            // If dispel is triggered by an item (ex. Healing Potion)
            // and debuff is unpottable, do not dispel
            if ((effect.getSkillTemplate().isUndispellableByPotions()) && itemTriggered)
                continue;
            //check for targetslot, effects with target slot level higher or equal to 2 cant be removed (ex. skillId: 11885)
            if (effect.getTargetSlot() != targetSlot.ordinal() || effect.getTargetSlotLevel() >= 2)
                continue;
            boolean remove = false;
            switch (dispelCat) {
                case ALL://DispelDebuffEffect
                    if ((effect.getDispelCategory() == DispelCategoryType.ALL
                            || effect.getDispelCategory() == DispelCategoryType.DEBUFF_MENTAL
                            || effect.getDispelCategory() == DispelCategoryType.DEBUFF_PHYSICAL)
                            && effect.getReqDispelLevel() <= dispelLevel)
                        remove = true;
                    break;
                case DEBUFF_MENTAL://DispelDebuffMentalEffect
                    if ((effect.getDispelCategory() == DispelCategoryType.ALL
                            || effect.getDispelCategory() == DispelCategoryType.DEBUFF_MENTAL)
                            && effect.getReqDispelLevel() <= dispelLevel)
                        remove = true;
                    break;
                case DEBUFF_PHYSICAL://DispelDebuffPhysicalEffect
                    if ((effect.getDispelCategory() == DispelCategoryType.ALL
                            || effect.getDispelCategory() == DispelCategoryType.DEBUFF_PHYSICAL)
                            && effect.getReqDispelLevel() <= dispelLevel)
                        remove = true;
                    break;
                case BUFF://DispelBuffEffect or DispelBuffCounterAtkEffect
                    if (effect.getDispelCategory() == DispelCategoryType.BUFF
                            && effect.getReqDispelLevel() <= dispelLevel)
                        remove = true;
                    break;
                case STUN:
                    if (effect.getDispelCategory() == DispelCategoryType.STUN)
                        remove = true;
                    break;
                case NPC_BUFF://DispelNpcBuff
                    if (effect.getDispelCategory() == DispelCategoryType.NPC_BUFF)
                        remove = true;
                    break;
                case NPC_DEBUFF_PHYSICAL://DispelNpcDebuff
                    if (effect.getDispelCategory() == DispelCategoryType.NPC_DEBUFF_PHYSICAL)
                        remove = true;
                    break;
            }
            if (remove) {
                if (removePower(effect, power)) {
                    effect.endEffect();
                    abnormalEffectMap.remove(effect.getStack());
                }
                else if (owner instanceof Player)
                    PacketSendUtility.sendPck((Player)owner, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_DISPELCOUNT);
                count--;
            }
            else if (owner instanceof Player)
                PacketSendUtility.sendPck((Player)owner, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_DISPELLEVEL);
        }
    }
    public void removeEffectByEffectType(EffectType effectType) {
        for (Effect effect : abnormalEffectMap.values()) {
            for (EffectTemplate et : effect.getSuccessEffects()) {
                if (effectType == et.getEffectType()) {
                    effect.endEffect();
                }
            }
        }
    }
    private boolean removePower(Effect effect, int power) {
        int effectPower = effect.removePower(power);
        return effectPower <= 0;
    }
    /**
     * Removes the effect by skillid.
     *
     * @param skillid
     */
    public void removePassiveEffect(int skillid) {
        for (Effect effect : passiveEffectMap.values()) {
            if (effect.getSkillId() == skillid) {
                effect.endEffect();
            }
        }
    }
    /**
     * @param skillid
     */
    public void removeNoshowEffect(int skillid) {
        for (Effect effect : noshowEffects.values()) {
            if (effect.getSkillId() == skillid) {
                effect.endEffect();
            }
        }
    }
    public void removeAbnormalEffectsByTargetSlot(SkillTargetSlot targetSlot) {
        for (Effect effect : abnormalEffectMap.values()) {
            if (effect.getTargetSlot() == targetSlot.ordinal()) {
                effect.endEffect();
            }
        }
    }
    /**
     * Removes all effects from controllers and ends them appropriately Passive effect will not be removed
     */
    public void removeAllEffects() {
        this.removeAllEffects(false);
    }
    public void removeAllEffects(boolean logout) {
        if (!logout) {
            Iterator<Map.Entry<String, Effect>> it = abnormalEffectMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Effect> entry = it.next();
                // TODO recheck - kecimis
                if (!entry.getValue().getSkillTemplate().isNoRemoveAtDie() && !entry.getValue().isXpBoost()) {
                    entry.getValue().endEffect();
                    it.remove();
                }
            }
            for (Effect effect : noshowEffects.values()) {
                effect.endEffect();
            }
            noshowEffects.clear();
        } else {
            // remove all effects on logout
            for (Effect effect : abnormalEffectMap.values()) {
                effect.endEffect();
            }
            abnormalEffectMap.clear();
            for (Effect effect : noshowEffects.values()) {
                effect.endEffect();
            }
            noshowEffects.clear();
            for (Effect effect : passiveEffectMap.values()) {
                effect.endEffect();
            }
            passiveEffectMap.clear();
        }
    }
    /**
     * Return true if skillId is present among creature's abnormals
     */
    public boolean isAbnormalPresentBySkillId(int skillId) {
        for (Effect effect : abnormalEffectMap.values()) {
            if (effect.getSkillId() == skillId) {
                return true;
            }
        }
        return false;
    }
    public boolean isNoshowPresentBySkillId(int skillId) {
        for (Effect effect : noshowEffects.values()) {
            if (effect.getSkillId() == skillId) {
                return true;
            }
        }
        return false;
    }
    public boolean isPassivePresentBySkillId(int skillId) {
        for (Effect effect : passiveEffectMap.values()) {
            if (effect.getSkillId() == skillId) {
                return true;
            }
        }
        return false;
    }
    /**
     * return true if creature is under Fear effect
     */
    public boolean isUnderFear() {
        return isAbnormalSet(AbnormalState.FEAR);
    }
    public void updatePlayerEffectIcons() {
    }
    public void updatePlayerEffectIconsImpl() {
    }
    /**
     * @return copy of anbornals list
     */
    public List<Effect> getAbnormalEffects() {
        List<Effect> effects = new ArrayList<>();
        Iterator<Effect> iterator = iterator();
        while (iterator.hasNext()) {
            Effect effect = iterator.next();
            if (effect != null) {
                effects.add(effect);
            }
        }
        return effects;
    }
    /**
     * @return list of effects to display as top icons
     */
    public Collection<Effect> getAbnormalEffectsToShow() {
        return Collections2.filter(abnormalEffectMap.values(), new Predicate<Effect>() {
            @Override
            public boolean apply(Effect effect) {
                return effect.getSkillTemplate().getTargetSlot() != SkillTargetSlot.NOSHOW;
            }
        });
    }
    public Collection<Effect> getChantEffects() {
        return Collections2.filter(abnormalEffectMap.values(), new Predicate<Effect>() {
            @Override
            public boolean apply(Effect effect) {
                return effect.isChant();
            }
        });
    }
    /**
     * ABNORMAL EFFECTS
     */
    public void setAbnormal(int mask) {
        owner.getObserveController().notifyAbnormalSettedObservers(AbnormalState.getStateById(mask));
        abnormals |= mask;
    }
    public void unsetAbnormal(int mask) {
        int count = 0;
        for (Effect effect : abnormalEffectMap.values()) {
            if ((effect.getAbnormals() & mask) == mask) {
                count++;
            }
        }
        if (count <= 1) {
            abnormals &= ~mask;
        }
    }
    /**
     * Used for checking unique abnormal states
     *
     * @param id
     *
     * @return
     */
    public boolean isAbnormalSet(AbnormalState id) {
        return (abnormals & id.getId()) == id.getId();
    }
    /**
     * Used for compound abnormal state checks
     *
     * @param id
     *
     * @return
     */
    public boolean isAbnormalState(AbnormalState id) {
        int state = abnormals & id.getId();
        return state > 0 && state <= id.getId();
    }
    public int getAbnormals() {
        return abnormals;
    }
    /**
     * @return
     */
    public Iterator<Effect> iterator() {
        return abnormalEffectMap.values().iterator();
    }
    public TransformType getTransformType() {
        for (Effect eff : getAbnormalEffects()) {
            if (eff.isDeityAvatar()) {
                return TransformType.AVATAR;
            }
            return eff.getTransformType();
        }
        return TransformType.NONE;
    }
    public boolean isEmpty() {
        return abnormalEffectMap.isEmpty();
    }
    public void checkEffectCooldownId(Effect effect) {
        Collection<Effect> effects = this.getAbnormalEffectsToShow();
        int delayId = effect.getSkillTemplate().getCooldownId();
        int rDelay = 0;
        int size = 0;
        if (delayId == 1) {
            return;
        }
        switch (delayId) {
            case 2005:
                size = 2;
                break;
            //TODO
        }
        rDelay = delayId;
        if (delayId == rDelay && effects.size() >= size) {
            int i = 0;
            Effect toRemove = null;
            Iterator<Effect> iter2 = effects.iterator();
            while (iter2.hasNext()) {
                Effect nextEffect = iter2.next();
                if (nextEffect.getSkillTemplate().getCooldownId() == rDelay
                        && nextEffect.getTargetSlot() == effect.getTargetSlot()) {
                    i++;
                    if (toRemove == null) {
                        toRemove = nextEffect;
                    }
                }
            }
            if (i >= size && toRemove != null) {
                toRemove.endEffect();
            }
        }
    }
    private boolean checkExtraEffect(Effect effect) {
        Effect existingEffect = getMapForEffect(effect).get(effect.getStack());
        if (existingEffect != null) {
            if (existingEffect.getDispelCategory() == DispelCategoryType.EXTRA && effect.getDispelCategory() == DispelCategoryType.EXTRA) {
                existingEffect.endEffect();
                return true;
            }
        }
        return false;
    }
    private boolean searchConflict(Effect nextEffect) {
        if (priorityStigmaEffect(nextEffect) || checkExtraEffect(nextEffect)) {
            return false;
        }
        for (Effect effect : abnormalEffectMap.values()) {
            if (effect.getSkillSubType().equals(nextEffect.getSkillSubType()) || effect.getTargetSlotEnum().equals(nextEffect.getTargetSlotEnum())) {
                for (EffectTemplate et : effect.getEffectTemplates()) {
                    if (et.getEffectid() == 0) {
                        continue;
                    }
                    for (EffectTemplate et2 : nextEffect.getEffectTemplates()) {
                        if (et2.getEffectid() == 0) {
                            continue;
                        }
                        if (et.getEffectid() == et2.getEffectid()) {
                            if (et.getBasicLvl() > et2.getBasicLvl()) {
                                if (nextEffect.getTargetSlotEnum() != SkillTargetSlot.DEBUFF) {
                                    nextEffect.setEffectResult(EffectResult.CONFLICT);
                                }
                                return true;
                            } else {
                                effect.endEffect();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private boolean priorityStigmaEffect(Effect nextEffect) {
        for (Effect effect : abnormalEffectMap.values()) {
            if (effect.getSkillTemplate().getStigmaType().getId() < nextEffect.getSkillTemplate().getStigmaType().getId()
                    && effect.getTargetSlot() == nextEffect.getTargetSlot()
                    && effect.getTargetSlotLevel() == nextEffect.getTargetSlotLevel()) {
                for (EffectTemplate et : effect.getEffectTemplates()) {
                    if (et.getEffectid() == 0) {
                        continue;
                    }
                    for (EffectTemplate et2 : nextEffect.getEffectTemplates()) {
                        if (et2.getEffectid() == 0) {
                            continue;
                        }
                        if (et.getEffectid() == et2.getEffectid()) {
                            effect.endEffect();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}