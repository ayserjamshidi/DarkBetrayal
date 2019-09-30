/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.stats.container;

import com.ne.commons.utils.Rnd;
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.actions.PlayerMode;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Equipment;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.state.CreatureState;
import com.ne.gs.model.stats.calc.AdditionStat;
import com.ne.gs.model.stats.calc.Stat2;
import com.ne.gs.model.templates.ride.RideInfo;
import com.ne.gs.model.templates.stats.PlayerStatsTemplate;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_STATS_INFO;
import com.ne.gs.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xavier
 */
public class PlayerGameStats extends CreatureGameStats<Player> {

    private int cachedSpeed;
    private int cachedAttackSpeed;

    /**
     * @param owner
     */
    public PlayerGameStats(Player owner) {
        super(owner);
    }

    @Override
    protected void onStatsChange() {
        super.onStatsChange();
        updateStatsAndSpeedVisually();
    }

    public void updateStatsAndSpeedVisually() {
        updateStatsVisually();
        checkSpeedStats();
    }

    public void updateStatsVisually() {
        owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
    }

    private void checkSpeedStats() {
        int current = getMovementSpeed().getCurrent();
        int currentAttackSpeed = getAttackSpeed().getCurrent();
        if (current != cachedSpeed || currentAttackSpeed != cachedAttackSpeed) {
            owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
        }
        cachedSpeed = current;
        cachedAttackSpeed = currentAttackSpeed;
    }

    @Override
    public Stat2 getMaxHp() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.MAXHP, pst.getMaxHp());
    }

    @Override
    public Stat2 getMaxMp() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.MAXMP, pst.getMaxMp());
    }

    public Stat2 getMaxDp() {
        return getStat(StatEnum.MAXDP, 4000);
    }

    public Stat2 getFlyTime() {
        return getStat(StatEnum.FLY_TIME, CustomConfig.BASE_FLYTIME);
    }

    @Override
    public Stat2 getAttackSpeed() {
        int base = 1500;
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();

        if (mainHandWeapon != null) {
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackSpeed();
            Item offWeapon = owner.getEquipment().getOffHandWeapon();
            if (offWeapon != null) {
                base += offWeapon.getItemTemplate().getWeaponStats().getAttackSpeed() / 4;
            }
        }
        Stat2 aSpeed = getStat(StatEnum.ATTACK_SPEED, base);
        return aSpeed;
    }

    @Override
    public Stat2 getMovementSpeed() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        Stat2 movementSpeed;
        if (owner.isInPlayerMode(PlayerMode.RIDE)) {
            RideInfo ride = owner.ride;
            int runSpeed = (int) pst.getRunSpeed() * 1000;
            if (owner.isInState(CreatureState.FLYING)) {
                movementSpeed = new AdditionStat(StatEnum.FLY_SPEED, runSpeed, owner);
                movementSpeed.addToBonus((int) (ride.getFlySpeed() * 1000) - runSpeed);
            } else {
                float speed = owner.isInSprintMode() ? ride.getSprintSpeed() : ride.getMoveSpeed();
                movementSpeed = new AdditionStat(StatEnum.SPEED, runSpeed, owner);
                movementSpeed.addToBonus((int) (speed * 1000) - runSpeed);
            }
        } else if (owner.isInFlyingState()) {
            movementSpeed = getStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed() * 1000));
        } else if (owner.isInState(CreatureState.FLIGHT_TELEPORT) && !owner.isInState(CreatureState.RESTING)) {
            movementSpeed = getStat(StatEnum.SPEED, 12000);
        } else if (owner.isInState(CreatureState.WALKING)) {
            movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getWalkSpeed() * 1000));
        } else {
            movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getRunSpeed() * 1000));
        }
        return movementSpeed;
    }

    @Override
    public Stat2 getAttackRange() {
        int base = 1500;
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if (mainHandWeapon != null) {
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();

            if(offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon())
                base = Math.min(base, offHandWeapon.getItemTemplate().getWeaponStats().getAttackRange());
        }
        return getStat(StatEnum.ATTACK_RANGE, base);
    }

    @Override
    public Stat2 getPDef() {
        return getStat(StatEnum.PHYSICAL_DEFENSE, 0);
    }

    @Override
    public Stat2 getPCDef() {

        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        int base = pst.getPCritResist();

        return getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, base);
    }

    @Override
    public Stat2 getMDef() {
        return getStat(StatEnum.MAGICAL_DEFEND, 0);
    }

    @Override
    public Stat2 getMResist() {
        return getStat(StatEnum.MAGICAL_RESIST, 0);
    }

    @Override
    public Stat2 getPower() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.POWER, pst.getPower());
    }

    @Override
    public Stat2 getHealth() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.HEALTH, pst.getHealth());
    }

    @Override
    public Stat2 getAccuracy() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.ACCURACY, pst.getAccuracy());
    }

    @Override
    public Stat2 getAgility() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.AGILITY, pst.getAgility());
    }

    @Override
    public Stat2 getKnowledge() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.KNOWLEDGE, pst.getKnowledge());
    }

    @Override
    public Stat2 getWill() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.WILL, pst.getWill());
    }

    @Override
    public Stat2 getEvasion() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.EVASION, pst.getEvasion());
    }

    @Override
    public Stat2 getParry() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        int base = pst.getParry();
        Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getParry();
        }
        return getStat(StatEnum.PARRY, base);
    }

    @Override
    public Stat2 getBlock() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        return getStat(StatEnum.BLOCK, pst.getBlock());
    }

    @Override
    public Stat2 getMainHandPAttack() {
        int base = 18;
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            if (mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
                return new AdditionStat(StatEnum.MAIN_HAND_POWER, 0, owner);
            }
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
        }
        Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
        return getStat(StatEnum.MAIN_HAND_POWER, stat);
    }

    public Stat2 getOffHandPAttack() {
        Equipment equipment = owner.getEquipment();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
            int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
            base *= 0.98;
            Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
            return getStat(StatEnum.OFF_HAND_POWER, stat);
        }
        return new AdditionStat(StatEnum.OFF_HAND_POWER, 0, owner);
    }

    @Override
    public Stat2 getMainHandPCritical() {
        int base = 2;
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
        }
        return getStat(StatEnum.PHYSICAL_CRITICAL, base);
    }

    public Stat2 getOffHandPCritical() {
        Equipment equipment = owner.getEquipment();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
            int base = offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
            return getStat(StatEnum.PHYSICAL_CRITICAL, base);
        }
        return new AdditionStat(StatEnum.OFF_HAND_CRITICAL, 0, owner);
    }

    @Override
    public Stat2 getMainHandPAccuracy() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        int base = pst.getMainHandAccuracy();
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
        }
        return getStat(StatEnum.PHYSICAL_ACCURACY, base);
    }

    public Stat2 getOffHandPAccuracy() {
        Equipment equipment = owner.getEquipment();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
            PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
            int base = pst.getMainHandAccuracy();
            base += offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
            return getStat(StatEnum.PHYSICAL_ACCURACY, base);
        }
        return new AdditionStat(StatEnum.OFF_HAND_ACCURACY, 0, owner);
    }

    @Override
    public Stat2 getMAttack() {
        int base = 0;
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
                return new AdditionStat(StatEnum.MAGICAL_ATTACK, 0, owner);
            }
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
        } else {
            base = Rnd.get(16, 20);// hand attack
        }

        return getStat(StatEnum.MAGICAL_ATTACK, base);
    }

    @Override
    public Stat2 getMBoost() {
        int base = 0;
        Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getBoostMagicalSkill();
        }
        return getStat(StatEnum.BOOST_MAGICAL_SKILL, base);
    }

    @Override
    public Stat2 getMBResist() {
        int base = 0;
        return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, base);
    }

    @Override
    public Stat2 getMAccuracy() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        int base = pst.getMagicAccuracy();
        Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
        }
        return getStat(StatEnum.MAGICAL_ACCURACY, base);
    }

    @Override
    public Stat2 getMCritical() {
        return getStat(StatEnum.MAGICAL_CRITICAL, 50);
    }

    @Override
    public Stat2 getHpRegenRate() {
        int base = owner.getLevel() + 3;
        if (owner.isInState(CreatureState.RESTING)) {
            base *= 8;
        }
        base *= getHealth().getCurrent() / 100f;
        return getStat(StatEnum.REGEN_HP, base);
    }

    @Override
    public Stat2 getMpRegenRate() {
        int base = owner.getLevel() + 8;
        if (owner.isInState(CreatureState.RESTING)) {
            base *= 8;
        }
        base *= getWill().getCurrent() / 100f;
        return getStat(StatEnum.REGEN_HP, base);
    }

    @Override
    public void updateStatInfo() {
        owner.sendPck(new SM_STATS_INFO(owner));
    }

    @Override
    public void updateSpeedInfo() {
        PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0), true);
    }
}
