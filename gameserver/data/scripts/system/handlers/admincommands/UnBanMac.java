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
import com.ne.gs.network.BannedMacManager;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author KID
 */
public class UnBanMac extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            onError(player, null);
            return;
        }

        String address = params[0];
        boolean result = BannedMacManager.getInstance().unbanAddress(address,
            "uban;mac=" + address + ", " + player.getObjectId() + "; admin=" + player.getName());
        if (result) {
            player.sendMsg("mac " + address + " has unbanned");
        } else {
            player.sendMsg("mac " + address + " is not banned");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //unbanmac <mac>");
    }
}
