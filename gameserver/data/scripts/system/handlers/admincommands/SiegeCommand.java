/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.database.dao.SiegeDAO;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerCommonData;
import com.ne.gs.model.siege.ArtifactLocation;
import com.ne.gs.model.siege.FortressLocation;
import com.ne.gs.model.siege.OutpostLocation;
import com.ne.gs.model.siege.SiegeLocation;
import com.ne.gs.model.siege.SiegeModType;
import com.ne.gs.model.siege.SiegeRace;
import com.ne.gs.model.siege.SourceLocation;
import com.ne.gs.model.team.legion.Legion;
import com.ne.gs.services.LegionService;
import com.ne.gs.services.SiegeService;
import com.ne.gs.services.siegeservice.BalaurAssaultService;
import com.ne.gs.services.siegeservice.Siege;
import com.ne.gs.utils.chathandlers.ChatCommand;

@SuppressWarnings("rawtypes")
public class SiegeCommand extends ChatCommand {

    private static final String COMMAND_START = "start";
    private static final String COMMAND_STOP = "stop";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_LIST_LOCATIONS = "locations";
    private static final String COMMAND_LIST_SIEGES = "sieges";
    private static final String COMMAND_CAPTURE = "capture";
    private static final String COMMAND_ASSAULT = "assault";

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {

        if (params.length == 0) {
            showHelp(player);
            return;
        }

        if (COMMAND_STOP.equalsIgnoreCase(params[0]) || COMMAND_START.equalsIgnoreCase(params[0])) {
            handleStartStopSiege(player, params);
        } else if (COMMAND_LIST.equalsIgnoreCase(params[0])) {
            handleList(player, params);
        } else if (COMMAND_LIST_SIEGES.equals(params[0])) {
            listLocations(player);
        } else if (COMMAND_CAPTURE.equals(params[0])) {
            capture(player, params);
        } else if (COMMAND_ASSAULT.equals(params[0])) {
            assault(player, params);
        }
    }

    protected void handleStartStopSiege(Player player, String... params) {
        if (params.length != 2 || !NumberUtils.isDigits(params[1])) {
            showHelp(player);
            return;
        }

        int siegeLocId = NumberUtils.toInt(params[1]);
        if (!isValidSiegeLocationId(player, siegeLocId)) {
            showHelp(player);
            return;
        }

        if (COMMAND_START.equalsIgnoreCase(params[0])) {
            if (SiegeService.getInstance().isSiegeInProgress(siegeLocId)) {
                player.sendMsg("Siege Location " + siegeLocId + " is already under siege");
            } else {
                player.sendMsg("Siege Location " + siegeLocId + " - starting siege!");
                SiegeService.getInstance().startSiege(siegeLocId);
            }
        } else if (COMMAND_STOP.equalsIgnoreCase(params[0])) {
            if (!SiegeService.getInstance().isSiegeInProgress(siegeLocId)) {
                player.sendMsg("Siege Location " + siegeLocId + " is not under siege");
            } else {
                player.sendMsg("Siege Location " + siegeLocId + " - stopping siege!");
                SiegeService.getInstance().stopSiege(siegeLocId);
            }
        }
    }

    protected boolean isValidSiegeLocationId(Player player, int fortressId) {

        if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(fortressId)) {
            player.sendMsg("Id " + fortressId + " is invalid");
            return false;
        }

        return true;
    }

    protected void handleList(Player player, String[] params) {
        if (params.length != 2) {
            showHelp(player);
            return;
        }

        if (COMMAND_LIST_LOCATIONS.equalsIgnoreCase(params[1])) {
            listLocations(player);
        } else if (COMMAND_LIST_SIEGES.equalsIgnoreCase(params[1])) {
            listSieges(player);
        } else {
            showHelp(player);
        }
    }

    protected void listLocations(Player player) {
        for (FortressLocation f : SiegeService.getInstance().getFortresses().values()) {
            player.sendMsg("Fortress: " + f.getLocationId() + " belongs to " + f.getRace());
        }
        for (OutpostLocation o : SiegeService.getInstance().getOutposts().values()) {
            player.sendMsg("Outpost: " + o.getLocationId() + " belongs to " + o.getRace());
        }
        for (SourceLocation s : SiegeService.getInstance().getSources().values()) {
            player.sendMsg("Source: " + s.getLocationId() + " belongs to " + s.getRace());
        }
        for (ArtifactLocation a : SiegeService.getInstance().getStandaloneArtifacts().values()) {
            player.sendMsg("Artifact: " + a.getLocationId() + " belongs to " + a.getRace());
        }
    }

    protected void listSieges(Player player) {
        for (Integer i : SiegeService.getInstance().getSiegeLocations().keySet()) {
            Siege s = SiegeService.getInstance().getSiege(i);
            if (s != null) {
                int secondsLeft = SiegeService.getInstance().getRemainingSiegeTimeInSeconds(i);
                String minSec = secondsLeft / 60 + "m ";
                minSec += secondsLeft % 60 + "s";
                player.sendMsg("Location: " + i + ": " + minSec + " left.");
            }
        }
    }

    protected void capture(Player player, String[] params) {
        if (params.length < 3 || !NumberUtils.isNumber(params[1])) {
            showHelp(player);
            return;
        }

        int siegeLocationId = NumberUtils.toInt(params[1]);
        if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(siegeLocationId)) {
            player.sendMsg("Invalid Siege Location Id: " + siegeLocationId);
            return;
        }

        // check if params2 is siege race
        SiegeRace sr = null;
        try {
            sr = SiegeRace.valueOf(params[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            // ignore
        }

        // try to find legion by name
        Legion legion = null;
        if (sr == null) {

            try {
                int legionId = Integer.valueOf(params[2]);
                legion = LegionService.getInstance().getLegion(legionId);
            } catch (NumberFormatException e) {
                String legionName = "";
                for (int i = 2; i < params.length; i++) {
                    legionName += " " + params[i];
                }
                legion = LegionService.getInstance().getLegion(legionName.trim());
            }

            if (legion != null) {
                int legionBGeneral = LegionService.getInstance().getLegionBGeneral(legion.getLegionId());
                if (legionBGeneral != 0) {
                    PlayerCommonData BGeneral = GDB.get(PlayerDAO.class).loadPlayerCommonData(legionBGeneral);
                    sr = SiegeRace.getByRace(BGeneral.getRace());
                }
            }
        }

        // check if can capture
        if (legion == null && sr == null) {
            player.sendMsg(params[2] + " is not valid siege race or legion name");
            return;
        }

        // capture
        SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(siegeLocationId);
        Siege s = SiegeService.getInstance().getSiege(siegeLocationId);
        if (s != null) {
            s.getSiegeCounter().addRaceDamage(sr, s.getBoss().getLifeStats().getMaxHp() + 1);
            s.setBossKilled(true);
            SiegeService.getInstance().stopSiege(siegeLocationId);
            loc.setLegionId(legion != null ? legion.getLegionId() : 0);
        } else {
            SiegeService.getInstance().deSpawnNpcs(siegeLocationId);
            loc.setVulnerable(false);
            loc.setUnderShield(false);
            loc.setRace(sr);
            loc.setLegionId(legion != null ? legion.getLegionId() : 0);
            SiegeService.getInstance().spawnNpcs(siegeLocationId, sr, SiegeModType.PEACE);
            GDB.get(SiegeDAO.class).updateSiegeLocation(loc);
            switch (siegeLocationId) {
                case 2011:
                case 2021:
                case 3011:
                case 3021:
                    SiegeService.getInstance().updateOutpostStatusByFortress((FortressLocation) loc);
                    break;
                case 4011:
                case 4021:
                case 4031:
                case 4041:
                    SiegeService.getInstance().updateTiamarantaRiftsStatus(false, true);
                    break;
            }
        }
        SiegeService.getInstance().broadcastUpdate(loc);
    }

    protected void assault(Player player, String[] params) {
        if (params.length < 2 || !NumberUtils.isNumber(params[1]) && !NumberUtils.isNumber(params[2])) {
            showHelp(player);
            return;
        }

        int siegeLocationId = NumberUtils.toInt(params[1]);
        int delay = NumberUtils.toInt(params[2]);
        if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(siegeLocationId)) {
            player.sendMsg("Invalid Siege Location Id: " + siegeLocationId);
            return;
        }

        BalaurAssaultService.getInstance().startAssault(player, siegeLocationId, delay);
    }

    protected void showHelp(Player player) {
        player.sendMsg("AdminCommand //siege Help\n" + "//siege start|stop <LocationId>\n" + "//siege list locations|sieges\n"
            + "//siege capture <LocationId> <siegeRaceName|legionName|legionId>\n" + "//siege assault <LocationId> <delaySec>");

        java.util.Set<Integer> fortressIds = SiegeService.getInstance().getFortresses().keySet();
        java.util.Set<Integer> artifactIds = SiegeService.getInstance().getStandaloneArtifacts().keySet();
        java.util.Set<Integer> sourceIds = SiegeService.getInstance().getSources().keySet();
        java.util.Set<Integer> outpostIds = SiegeService.getInstance().getOutposts().keySet();
        player.sendMsg("Fortress: " + StringUtils.join(fortressIds, ", "));
        player.sendMsg("Artifacts: " + StringUtils.join(artifactIds, ", "));
        player.sendMsg("Sources: " + StringUtils.join(sourceIds, ", "));
        player.sendMsg("Outposts: " + StringUtils.join(outpostIds, ", "));
    }
}
