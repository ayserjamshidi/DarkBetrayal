/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Watson
 */
public class UnBanIp extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            player.sendMsg("Syntax: //unbanip <mask>");
            return;
        }

        LoginServer.getInstance().sendBanPacket((byte) 2, 0, params[0], -1, player.getObjectId());
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //unbanip <mask>");
    }
}
