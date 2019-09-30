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
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author cura
 */
public class Teleportation extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        boolean isTeleportation = player.getAdminTeleportation();

        if (isTeleportation) {
            player.sendMsg("Teleported state is disabled.");
            player.setAdminTeleportation(false);
        } else {
            player.sendMsg("Teleported state.");
            player.setAdminTeleportation(true);
        }
    }
}
