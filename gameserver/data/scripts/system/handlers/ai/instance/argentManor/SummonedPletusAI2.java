/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.argentManor;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;

/**
 * @author xTz
 */
@AIName("summoned_pletus")
public class SummonedPletusAI2 extends AggressiveNpcAI2 {

    @Override
    public boolean canThink() {
        return false;
    }

}
