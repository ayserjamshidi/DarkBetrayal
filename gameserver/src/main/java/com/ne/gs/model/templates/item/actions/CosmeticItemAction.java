/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PlayerAppearanceDAO;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerAppearance;
import com.ne.gs.model.templates.cosmeticitems.CosmeticItemTemplate;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_INFO;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CosmeticItemAction")
public class CosmeticItemAction extends AbstractItemAction {

    @XmlAttribute(name = "name")
    protected String cosmeticName;

    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
        CosmeticItemTemplate template = DataManager.COSMETIC_ITEMS_DATA.getCosmeticItemsTemplate(cosmeticName);
        if (template == null) {
            return false;
        }
        if (!template.getRace().equals(player.getRace())) {
            return false;
        }
        if (!template.getGenderPermitted().equals("ALL")) {
            if (!player.getGender().toString().equals(template.getGenderPermitted())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void act(final Player player, Item parentItem, Item targetItem) {
        CosmeticItemTemplate template = DataManager.COSMETIC_ITEMS_DATA.getCosmeticItemsTemplate(cosmeticName);
        PlayerAppearance playerAppearance = player.getPlayerAppearance();
        String type = template.getType();
        int id = template.getId();
        if (type.equals("hair_color")) {
            playerAppearance.setHairRGB(id);
        } else if (type.equals("face_color")) {
            playerAppearance.setSkinRGB(id);
        } else if (type.equals("lip_color")) {
            playerAppearance.setLipRGB(id);
        } else if (type.equals("eye_color")) {
            playerAppearance.setEyeRGB(id);
        } else if (type.equals("hair_type")) {
            playerAppearance.setHair(id);
        } else if (type.equals("face_type")) {
            playerAppearance.setFace(id);
        } else if (type.equals("voice_type")) {
            playerAppearance.setVoice(id);
        } else if (type.equals("makeup_type")) {
            playerAppearance.setTattoo(id);
        } else if (type.equals("tattoo_type")) {
            playerAppearance.setDeco(id);
        } else if (type.equals("preset_name")) {
            CosmeticItemTemplate.Preset preset = template.getPreset();
            playerAppearance.setEyeRGB(preset.getEyeColor());
            playerAppearance.setLipRGB(preset.getLipColor());
            playerAppearance.setHairRGB(preset.getHairColor());
            playerAppearance.setSkinRGB(preset.getEyeColor());
            playerAppearance.setHair(preset.getHairType());
            playerAppearance.setFace(preset.getFaceType());
            playerAppearance.setHeight(preset.getScale());
        }
        GDB.get(PlayerAppearanceDAO.class).store(player);
        player.getInventory().delete(targetItem);
        player.sendPck(new SM_PLAYER_INFO(player, false));
        player.getKnownList().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player rangePlayer) {
                if (rangePlayer.isOnline()) {
                    rangePlayer.sendPck(new SM_PLAYER_INFO(player, player.isEnemy(rangePlayer)));
                }
            }
        });
    }
}
