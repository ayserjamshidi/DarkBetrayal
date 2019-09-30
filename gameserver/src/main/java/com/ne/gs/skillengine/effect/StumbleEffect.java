/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.stats.container.StatEnum;
import com.ne.gs.network.aion.serverpackets.SM_FORCED_MOVE;
import com.ne.gs.skillengine.model.Effect;
import com.ne.gs.skillengine.model.SpellStatus;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author ATracer //Опрокид
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StumbleEffect")
public class StumbleEffect extends EffectTemplate {
	
	// захват ног
	private static final int SKILLID = 322;
    private static final int SKILLID1 = 227;
    private static final int SKILLID2 = 2465;

    @Override
    public void applyEffect(Effect effect) {
        //lonefoxx
        if(effect.getEffected().getEffectController().isAbnormalPresentBySkillId(724) 
                || effect.getEffected().getEffectController().isAbnormalPresentBySkillId(1800) 
                || effect.getEffected().getEffectController().isAbnormalPresentBySkillId(920)){
            return;
        }
        
        
        if (!effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.OPENAERIAL)) {
			
            //Опрокидывание должно снимать эффект сна с персонажа
            if (effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.SLEEP)) {
                effect.getEffected().getEffectController().removeEffectByEffectType(EffectType.SLEEP);
            }
			
			if (effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.ROOT) &&
                    !effect.getEffected().getEffectController().isAbnormalPresentBySkillId(SKILLID) &&
                    !effect.getEffected().getEffectController().isAbnormalPresentBySkillId(SKILLID1) &&
                    !effect.getEffected().getEffectController().isAbnormalPresentBySkillId(SKILLID2)) {
                effect.getEffected().getEffectController().removeEffectByEffectType(EffectType.ROOT);
            }
            effect.addToEffectedController();
        }
    }

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect, StatEnum.STUMBLE_RESISTANCE, SpellStatus.STUMBLE);
    }

    @Override
    public void startEffect(Effect effect) {
        Creature effected = effect.getEffected();
        effected.getController().cancelCurrentSkill();
        effected.getMoveController().abortMove();
        effected.getEffectController().setAbnormal(AbnormalState.STUMBLE.getId());
        effect.setAbnormal(AbnormalState.STUMBLE.getId());
        PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new SM_FORCED_MOVE(effect.getEffector(), effect.getEffected()));
    }

    @Override
    public void endEffect(Effect effect) {
        effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.STUMBLE.getId());
    }
}
