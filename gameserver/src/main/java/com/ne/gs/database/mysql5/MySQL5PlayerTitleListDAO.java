/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.database.mysql5;

import com.ne.commons.database.DB;
import com.ne.commons.database.DatabaseFactory;
import com.ne.commons.database.ParamReadStH;
import com.ne.gs.database.dao.MySQL5DAOUtils;
import com.ne.gs.database.dao.PlayerTitleListDAO;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.title.Title;
import com.ne.gs.model.gameobjects.player.title.TitleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author xavier
 */
public class MySQL5PlayerTitleListDAO extends PlayerTitleListDAO {

    private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerTitleListDAO.class);
    private static final String LOAD_QUERY = "SELECT `title_id`, `remaining` FROM `player_titles` WHERE `player_id`=?";
    private static final String INSERT_QUERY = "INSERT INTO `player_titles`(`player_id`,`title_id`, `remaining`) VALUES (?,?,?)";
    private static final String DELETE_QUERY = "DELETE FROM `player_titles` WHERE `player_id`=? AND `title_id` =?;";

    @Override
    public TitleList loadTitleList(final int playerId) {
        final TitleList tl = new TitleList();

        DB.select(LOAD_QUERY, new ParamReadStH() {

            @Override
            public void setParams(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, playerId);
            }

            @Override
            public void handleRead(ResultSet rset) throws SQLException {
                while (rset.next()) {
                    int id = rset.getInt("title_id");
                    int remaining = rset.getInt("remaining");
                    tl.addEntry(id, remaining);
                }
            }
        });
        return tl;
    }

    @Override
    public boolean storeTitles(Player player, Title entry) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
            stmt.setInt(1, player.getObjectId());
            stmt.setInt(2, entry.getId());
            stmt.setInt(3, entry.getExpireTime());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Could not store emotionId for player " + player.getObjectId() + " from GDB: " + e.getMessage(), e);
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

    /*
     * (non-Javadoc)
     * @see com.ne.gs.database.dao.PlayerTitleListDAO#removeTitle(int, int)
     */
    @Override
    public boolean removeTitle(int playerId, int titleId) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
            stmt.setInt(1, playerId);
            stmt.setInt(2, titleId);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Could not delete title for player " + playerId + " from GDB: " + e.getMessage(), e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
        return true;
    }
}
