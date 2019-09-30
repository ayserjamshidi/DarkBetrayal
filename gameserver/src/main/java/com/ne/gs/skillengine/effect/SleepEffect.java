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
import com.ne.gs.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SleepEffect")
public class SleepEffect extends EffectTemplate {

    @Override
    public void applyEffect(Effect effect) {
        effect.addToEffectedController();
    }

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect, StatEnum.SLEEP_RESISTANCE, null);
    }

    @Override
    public void startEffect(Effect effect) {
        Creature effected = effect.getEffected();
        effected.getController().cancelCurrentSkill();
        effect.setAbnormal(AbnormalState.SLEEP.getId());
        effected.getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
        effect.setCancelOnDmg(true);
    }

    @Override
    public void endEffect(Effect effect) {
        effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.SLEEP.getId());
    }
}
