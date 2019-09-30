/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.services;

import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AITemplate;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.dataholders.NpcShoutData;
import com.ne.gs.model.gameobjects.AionObject;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.npcshout.NpcShout;
import com.ne.gs.model.templates.npcshout.ShoutEventType;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.World;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;

/**
 * This class is handling NPC shouts
 *
 * @author Rolandas
 */
public class NpcShoutsService {

    private static final Logger log = LoggerFactory.getLogger(NpcShoutsService.class);

    NpcShoutData shoutsCache = DataManager.NPC_SHOUT_DATA;

    private NpcShoutsService() {
        for (Npc npc : World.getInstance().getNpcs()) {
            int npcId = npc.getNpcId();
            int worldId = npc.getSpawn().getWorldId();
            final int objectId = npc.getObjectId();

            if (!shoutsCache.hasAnyShout(worldId, npcId, ShoutEventType.IDLE)) {
                continue;
            }

            final List<NpcShout> shouts = shoutsCache.getNpcShouts(worldId, npcId, ShoutEventType.IDLE, null, 0);
            if (shouts.size() == 0) {
                continue;
            }

            int defaultPollDelay = Rnd.get(180, 360) * 1000;
            for (NpcShout shout : shouts) {
                if (shout.getPollDelay() != 0 && shout.getPollDelay() < defaultPollDelay) {
                    defaultPollDelay = shout.getPollDelay();
                }
            }

            ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    AionObject npcObj = World.getInstance().findVisibleObject(objectId);
                    if (npcObj != null && npcObj instanceof Npc) {
                        Npc npc2 = (Npc) npcObj;
                        // check if AI overrides
                        if (!npc2.getAi2().poll(AIQuestion.CAN_SHOUT)) {
                            return;
                        }
                        int randomShout = Rnd.get(shouts.size());
                        NpcShout shout = shouts.get(randomShout);
                        if ((shout.getPattern() != null) && (!((AITemplate) npc2.getAi2()).onPatternShout(ShoutEventType.IDLE, shout.getPattern(), 0))) {
                            return;
                        }
                        Iterator<Player> iter = npc2.getKnownList().getKnownPlayers().values().iterator();
                        while (iter.hasNext()) {
                            Player kObj = iter.next();
                            if (kObj.getLifeStats().isAlreadyDead()) {
                                return;
                            }
                            shout(npc2, kObj, shout, 0);
                        }
                    }
                }
            }, 0, defaultPollDelay);
        }
    }

    public void shout(Npc owner, Creature target, List<NpcShout> shouts, int delaySeconds, boolean isSequence) {
        if (owner == null || shouts == null) {
            return;
        }
        if (shouts.size() > 1) {
            if (isSequence) {
                int nextDelay = 5;
                for (NpcShout shout : shouts) {
                    if (delaySeconds == -1) {
                        shout(owner, target, shout, nextDelay);
                        nextDelay += 5;
                    } else {
                        shout(owner, target, shout, delaySeconds);
                        delaySeconds = -1;
                    }
                }
            } else {
                int randomShout = Rnd.get(shouts.size());
                shout(owner, target, shouts.get(randomShout), delaySeconds);
            }
        } else if (shouts.size() == 1) {
            shout(owner, target, shouts.get(0), delaySeconds);
        }
    }

    public void shout(Npc owner, Creature target, NpcShout shout, int delaySeconds) {
        if (owner == null || shout == null) {
            return;
        }

        Object param = shout.getParam();

        if (target instanceof Player) {
            Player player = (Player) target;
            if ("username".equals(param)) {
                param = player.getName();
            } else if ("userclass".equals(param)) {
                param = (240000 + player.getCommonData().getPlayerClass().getClassId()) * 2 + 1;
            } else if ("usernation".equals(param)) {
                log.warn("Shout with param 'usernation' is not supported");
                return;
            } else if ("usergender".equals(param)) {
                param = (902012 + player.getCommonData().getGender().getGenderId()) * 2 + 1;
            } else if ("mainslotitem".equals(param)) {
                Item weapon = player.getEquipment().getMainHandWeapon();
                if (weapon == null) {
                    return;
                }
                param = weapon.getItemTemplate().getNameId();
            } else if ("quest".equals(shout.getPattern())) {
                delaySeconds = 0;
            }
        }

        if ("target".equals(param) && target != null) {
            param = target.getObjectTemplate().getName();
        }

        owner.shout(shout, target, param, delaySeconds);
    }

    public void sendMsg(Npc npc, int msg, int Obj, int color, int delay) {
        sendMsg(npc, null, msg, Obj, false, color, delay);
    }

    public void sendMsg(Npc npc, int msg, int Obj, boolean isShout, int color, int delay) {
        sendMsg(npc, null, msg, Obj, isShout, color, delay);
    }

    public void sendMsg(Npc npc, int msg, int delay) {
        sendMsg(npc, null, msg, 0, false, 25, delay);
    }

    public void sendMsg(Npc npc, int msg) {
        sendMsg(npc, null, msg, 0, false, 25, 0);
    }

    public void sendMsg(WorldMapInstance instance, int msg, int Obj, boolean isShout, int color, int delay) {
        sendMsg(null, instance, msg, Obj, isShout, color, delay);
    }

    public void sendMsg(WorldMapInstance instance, int msg, int delay) {
        sendMsg(null, instance, msg, 0, false, 25, delay);
    }

    public void sendMsg(final Npc npc, final WorldMapInstance instance, final int msg, final int Obj, final boolean isShout, final int color, int delay) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if ((npc != null) && (npc.isSpawned())) {
                    npc.getKnownList().doOnAllPlayers(new Visitor<Player>() {

                        @Override
                        public void visit(Player player) {
                            player.sendPck(new SM_SYSTEM_MESSAGE(isShout, msg, Obj, color));
                        }

                    });
                } else if (instance != null) {
                    instance.doOnAllPlayers(new Visitor<Player>() {

                        @Override
                        public void visit(Player player) {
                            player.sendPck(new SM_SYSTEM_MESSAGE(isShout, msg, Obj, color));
                        }
                    });
                }
            }
        }, delay);
    }

    public static NpcShoutsService getInstance() {
        return SingletonHolder.instance;
    }

    @SuppressWarnings("synthetic-access")
    private static final class SingletonHolder {

        protected static final NpcShoutsService instance = new NpcShoutsService();
    }

}
