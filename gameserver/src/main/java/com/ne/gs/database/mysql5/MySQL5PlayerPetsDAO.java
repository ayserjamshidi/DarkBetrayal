/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.database.mysql5;

import com.ne.commons.database.DatabaseFactory;
import com.ne.gs.database.dao.MySQL5DAOUtils;
import com.ne.gs.database.dao.PlayerPetsDAO;
import com.ne.gs.model.gameobjects.player.PetCommonData;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.pet.PetDopingBag;
import com.ne.gs.services.toypet.PetHungryLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author M@xx, xTz, Rolandas
 */
public class MySQL5PlayerPetsDAO extends PlayerPetsDAO {

    private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerPetsDAO.class);

    @Override
    public void saveFeedStatus(Player player, int petId, int hungryLevel, int feedProgress, int feedPoints, long reuseTime) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con
                .prepareStatement("UPDATE player_pets SET hungry_level = ?, feed_progress = ?, feed_points = ?, reuse_time = ? WHERE player_id = ? AND pet_id = ?");
            stmt.setInt(1, hungryLevel);
            stmt.setInt(2, feedProgress);
            stmt.setInt(3, feedPoints);
            stmt.setLong(4, reuseTime);
            stmt.setInt(5, player.getObjectId());
            stmt.setInt(6, petId);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Error update pet #" + petId, e);
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public void saveDopingBag(Player player, int petId, PetDopingBag bag) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE player_pets SET dopings = ? WHERE player_id = ? AND pet_id = ?");
            String itemIds = bag.getFoodItem() + "," + bag.getDrinkItem();
            for (int itemId : bag.getScrollsUsed()) {
                itemIds += "," + Integer.toString(itemId);
            }
            stmt.setString(1, itemIds);
            stmt.setInt(2, player.getObjectId());
            stmt.setInt(3, petId);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Error update doping for pet #" + petId, e);
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public void setTime(Player player, int petId, long time) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE player_pets SET reuse_time = ? WHERE player_id = ? AND pet_id = ?");
            stmt.setLong(1, time);
            stmt.setInt(2, player.getObjectId());
            stmt.setInt(3, petId);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Error update pet #" + petId, e);
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public void insertPlayerPet(PetCommonData petCommonData) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con
                .prepareStatement("INSERT INTO player_pets(player_id, pet_id, decoration, name, despawn_time) VALUES(?, ?, ?, ?, ?)");
            stmt.setInt(1, petCommonData.getMasterObjectId());
            stmt.setInt(2, petCommonData.getPetId());
            stmt.setInt(3, petCommonData.getDecoration());
            stmt.setString(4, petCommonData.getName());
            stmt.setTimestamp(5, petCommonData.getDespawnTime());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Error inserting new pet #" + petCommonData.getPetId() + "[" + petCommonData.getName() + "]", e);
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public void removePlayerPet(Player player, int petId) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("DELETE FROM player_pets WHERE player_id = ? AND pet_id = ?");
            stmt.setInt(1, player.getObjectId());
            stmt.setInt(2, petId);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Error removing pet #" + petId, e);
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public List<PetCommonData> getPlayerPets(Player player) {
        List<PetCommonData> pets = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM player_pets WHERE player_id = ?");
            stmt.setInt(1, player.getObjectId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PetCommonData petCommonData = new PetCommonData(rs.getInt("pet_id"), player.getObjectId());
                petCommonData.setName(rs.getString("name"));
                petCommonData.setDecoration(rs.getInt("decoration"));
                if (petCommonData.getFeedProgress() != null) {
                    petCommonData.getFeedProgress().setHungryLevel(PetHungryLevel.fromId(rs.getInt("hungry_level")));
                    petCommonData.getFeedProgress().setData(rs.getInt("feed_progress"));
                    petCommonData.getFeedProgress().setTotalPoints(rs.getInt("feed_points"));
                    petCommonData.setCurentTime(rs.getLong("reuse_time"));
                }
                if (petCommonData.getDopingBag() != null) {
                    String dopings = rs.getString("dopings");
                    if (dopings != null) {
                        String[] ids = dopings.split(",");
                        for (int i = 0; i < ids.length; i++) {
                            petCommonData.getDopingBag().setItem(Integer.parseInt(ids[i]), i);
                        }
                    }
                }
                petCommonData.setBirthday(rs.getTimestamp("birthday"));
                if (petCommonData.getTime() != 0) {
                    petCommonData.setIsFeedingTime(false);
                    petCommonData.setReFoodTime(petCommonData.getTime());
                }
                petCommonData.setStartMoodTime(rs.getLong("mood_started"));
                petCommonData.setShuggleCounter(rs.getInt("counter"));
                petCommonData.setMoodCdStarted(rs.getLong("mood_cd_started"));
                petCommonData.setGiftCdStarted(rs.getLong("gift_cd_started"));
                Timestamp ts = null;
                try {
                    ts = rs.getTimestamp("despawn_time");
                } catch (Exception e) {
                }
                if (ts == null) {
                    ts = new Timestamp(System.currentTimeMillis());
                }
                petCommonData.setDespawnTime(ts);
                pets.add(petCommonData);
            }
            stmt.close();
        } catch (Exception e) {
            log.error("Error getting pets for " + player.getObjectId(), e);
        } finally {
            DatabaseFactory.close(con);
        }
        return pets;
    }

    @Override
    public void updatePetName(PetCommonData petCommonData) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE player_pets SET name = ? WHERE player_id = ? AND pet_id = ?");
            stmt.setString(1, petCommonData.getName());
            stmt.setInt(2, petCommonData.getMasterObjectId());
            stmt.setInt(3, petCommonData.getPetId());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Error update pet #" + petCommonData.getPetId(), e);
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public boolean savePetMoodData(PetCommonData petCommonData) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con
                .prepareStatement("UPDATE player_pets SET mood_started = ?, counter = ?, mood_cd_started = ?, gift_cd_started = ?, despawn_time = ? WHERE player_id = ? AND pet_id = ?");
            stmt.setLong(1, petCommonData.getMoodStartTime());
            stmt.setInt(2, petCommonData.getShuggleCounter());
            stmt.setLong(3, petCommonData.getMoodCdStarted());
            stmt.setLong(4, petCommonData.getGiftCdStarted());
            stmt.setTimestamp(5, petCommonData.getDespawnTime());
            stmt.setInt(6, petCommonData.getMasterObjectId());
            stmt.setInt(7, petCommonData.getPetId());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Error updating mood for pet #" + petCommonData.getPetId(), e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
        return true;
    }

    @Override
    public boolean supports(String databaseName, int majorVersion, int minorVersion) {
        return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
    }

}
