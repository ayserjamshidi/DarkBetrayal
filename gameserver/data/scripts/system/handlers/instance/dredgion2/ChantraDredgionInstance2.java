/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.dredgion2;

import com.ne.commons.utils.Rnd;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.DescId;
import com.ne.gs.model.Race;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionServerPacket;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

@InstanceID(300210000)
public class ChantraDredgionInstance2 extends DredgionInstance2 {

    @Override
    public void onEnterInstance(Player player) {
        if (isInstanceStarted.compareAndSet(false, true)) {
            sp(730311, 554.83081f, 173.87158f, 432.52448f, (byte) 0, 9, 720000);
            sp(730312, 397.11661f, 184.29782f, 432.80328f, (byte) 0, 42, 720000);
            if (Rnd.get(1, 100) < 21) {
                sp(216889, 484.1199f, 314.08817f, 403.7213f, (byte) 5, 720000);
            }
            if (Rnd.get(1, 100) < 21) {
                sp(216890, 499.52f, 598.67f, 390.49f, (byte) 59, 720000);
            }
            if (Rnd.get(1, 100) < 21) {
                spawn(216887, 486.26382f, 909.48175f, 405.24463f, (byte) 90);
            }
            if (Rnd.get(1, 100) < 51) {
                switch (Rnd.get(2)) {
                    case 0:
                        spawn(216888, 416.3429f, 282.32785f, 409.7311f, (byte) 80);
                        break;
                    default:
                        spawn(216888, 552.07446f, 289.058f, 409.7311f, (byte) 80);
                        break;
                }
            }

            int spawnTime = Rnd.get(10, 15) * 60 * 1000 + 120000;
            sendMsgByRace(1400633, Race.PC_ALL, spawnTime);
            sp(216941, 485.99f, 299.23f, 402.57f, (byte) 30, spawnTime);
            startInstanceTask();
        }
        super.onEnterInstance(player);
    }

    private void onDieSurkan(Npc npc, Player mostPlayerDamage, int points) {
        Race race = mostPlayerDamage.getRace();
        captureRoom(race, npc.getNpcId() + 14 - 700851);
        for (Player player : instance.getPlayersInside()) {
            AionServerPacket packet = new SM_SYSTEM_MESSAGE(1400199, DescId.of(race.equals(Race.ASMODIANS) ? 1800483 : 1800481),
                DescId.of(npc.getObjectTemplate().getNameId() * 2 + 1));
            player.sendPck(packet);
        }
        getPlayerReward(mostPlayerDamage).captureZone();
        if (++surkanKills == 5) {
            spawn(216886, 485.33f, 832.26f, 416.64f, (byte) 55);
            sendMsgByRace(1400632, Race.PC_ALL, 0);
        }
        updateScore(mostPlayerDamage, npc, points, false);
        CreatureActions.delete(npc);
    }

    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
            case 730350: // Secondary Hatch teleporter
                sendMsgByRace(1400641, Race.PC_ALL, 0);
                spawn(730315, 415.07663f, 173.85265f, 432.53436f, (byte) 0, 34);
                CreatureActions.delete(npc);
                return;
            case 730349: // Escape Hatch teleporter
                sendMsgByRace(1400631, Race.PC_ALL, 0);
                spawn(730314, 396.979f, 184.392f, 433.940f, (byte) 0, 42);
                CreatureActions.delete(npc);
                return;
            case 730351:
                sendMsgByRace(1400226, Race.PC_ALL, 0);
                spawn(730345, 448.391998f, 493.641998f, 394.131989f, (byte) 90, 12);
                CreatureActions.delete(npc);
                return;
            case 730352:
                sendMsgByRace(1400227, Race.PC_ALL, 0);
                spawn(730346, 520.875977f, 493.401001f, 394.433014f, (byte) 90, 133);
                CreatureActions.delete(npc);
                return;
            case 216890:
            case 216889:
                return;
        }
        Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
        if (mostPlayerDamage == null) {
            return;
        }
        Race race = mostPlayerDamage.getRace();
        switch (npc.getNpcId()) {
            case 700838:
            case 700839:
                onDieSurkan(npc, mostPlayerDamage, 500);
                return;
            case 700840:
				onDieSurkan(npc, mostPlayerDamage, 900);
                return;
            case 700848:
            case 700849:
				onDieSurkan(npc, mostPlayerDamage, 500);
                return;
            case 700850:
            case 700851:
                onDieSurkan(npc, mostPlayerDamage, 700);
                return;
            case 700845:
            case 700846:
                onDieSurkan(npc, mostPlayerDamage, 500);
                return;
            case 700847:
                onDieSurkan(npc, mostPlayerDamage, 900);
                return;
            case 700841:
            case 700842:
                onDieSurkan(npc, mostPlayerDamage, 1080);
                return;
            case 700843:
            case 700844:
                onDieSurkan(npc, mostPlayerDamage, 600);
                return;
            case 216882: // Captain's Cabin teleport
                sendMsgByRace(1400652, Race.PC_ALL, 0);
                if (race.equals(Race.ASMODIANS)) {
                    spawn(730358, 496.178f, 761.770f, 390.805f, (byte) 0, 186);
                } else {
                    spawn(730357, 473.759f, 761.864f, 390.805f, (byte) 0, 33);
                }
                break;
            case 700836:
                updateScore(mostPlayerDamage, npc, 100, false);
                CreatureActions.delete(npc);
                return;
            case 216886:
                if (!dredgionReward.isRewarded()) {
                    updateScore(mostPlayerDamage, npc, 1000, false);
                    Race winningRace = dredgionReward.getWinningRaceByScore();
                    stopInstance(winningRace);
                }
                // if (winningRace.equals(Race.ELYOS)) {
                // sendMsgByRace(1400230, Race.ELYOS, 0);
                // }
                // else {
                // sendMsgByRace(1400231, Race.ASMODIANS, 0);
                // }
                return;
            case 216941:
                updateScore(mostPlayerDamage, npc, 1000, false);
                return;
            case 216885:
                updateScore(mostPlayerDamage, npc, 500, false);
                return;
        }
        super.onDie(npc);
    }

    @Override
    protected void openFirstDoors() {
        openDoor(4);
        openDoor(173);
    }
}
