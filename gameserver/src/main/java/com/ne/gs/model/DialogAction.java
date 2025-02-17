/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model;

public enum DialogAction {
    // Anything commented out was 3.0 and now outdated.

    ERROR(-1),
    NULL(0),
    // TODO what is 1?
    BUY(2),
    SELL(3),
    OPEN_STIGMA_WINDOW(4),
    CREATE_LEGION(5),
    DISPERSE_LEGION(6),
    RECREATE_LEGION(7),
    SELECTED_QUEST_REWARD1(8),
    SELECTED_QUEST_REWARD2(9),
    SELECTED_QUEST_REWARD3(10),
    SELECTED_QUEST_REWARD4(11),
    SELECTED_QUEST_REWARD5(12),
    SELECTED_QUEST_REWARD6(13),
    SELECTED_QUEST_REWARD7(14),
    SELECTED_QUEST_REWARD8(15),
    SELECTED_QUEST_REWARD9(16),
    SELECTED_QUEST_REWARD10(17),
    SELECTED_QUEST_REWARD11(18),
    SELECTED_QUEST_REWARD12(19),
    //RESURRECT_PET(19),
    SELECTED_QUEST_REWARD13(20),
    //RETRIEVE_CHAR_WAREHOUSE(20),
    SELECTED_QUEST_REWARD14(21),
    //DEPOSIT_CHAR_WAREHOUSE(21),
    SELECTED_QUEST_REWARD15(22),
    //RETRIEVE_ACCOUNT_WAREHOUSE(22),
    SELECTED_QUEST_NOREWARD(23),
    //DEPOSIT_ACCOUNT_WAREHOUSE(23),
    RESURRECT_PET(24),
    RETRIEVE_CHAR_WAREHOUSE(25),
    DEPOSIT_CHAR_WAREHOUSE(26),
    RETRIEVE_ACCOUNT_WAREHOUSE(27),
    DEPOSIT_ACCOUNT_WAREHOUSE(28),
    //OPEN_VENDOR(28),
    QUEST_ACCEPT(29),
    //RESURRECT_BIND(29),
    QUEST_REFUSE(30),
    //RECOVERY(30),
    QUEST_SELECT(31),
    //ENTER_HOME_PVP_ARENA(31),
    OPEN_QUEST_WINDOW(32),
    //LEAVE_HOME_PVP_ARENA(32),
    OPEN_VENDOR(33),
    //OPEN_POSTBOX(33),
    RESURRECT_BIND(34),
    RECOVERY(35),
    ENTER_HOME_PVP_ARENA(36),
    //GIVE_ITEM_PROC(36),
    LEAVE_HOME_PVP_ARENA(37),
    //REMOVE_MANASTONE(37),
    OPEN_POSTBOX(38),
    //CHANGE_ITEM_SKIN(38),
    CHECK_USER_HAS_QUEST_ITEM(39),
    //AIRLINE_SERVICE(39),
    DIC(40),
    //GATHER_SKILL_LEVELUP(40),
    GIVE_ITEM_PROC(41),
    //COMBINE_SKILL_LEVELUP(41),
    REMOVE_MANASTONE(42),
    //EXTEND_INVENTORY(42),
    CHANGE_ITEM_SKIN(43),
    //EXTEND_CHAR_WAREHOUSE(43),
    AIRLINE_SERVICE(44),
    //EXTEND_ACCOUNT_WAREHOUSE(44),
    GATHER_SKILL_LEVELUP(45),
    //LEGION_LEVELUP(45),
    COMBINE_SKILL_LEVELUP(46),
    //LEGION_CREATE_EMBLEM(46),
    EXTEND_INVENTORY(47),
    //LEGION_CHANGE_EMBLEM(47),
    EXTEND_CHAR_WAREHOUSE(48),
    //OPEN_LEGION_WAREHOUSE(48),
    EXTEND_ACCOUNT_WAREHOUSE(49),
    //OPEN_PERSONAL_WAREHOUSE(49),
    LEGION_LEVELUP(50),
    //BUY_BY_AP(50),
    LEGION_CREATE_EMBLEM(51),
    //CLOSE_LEGION_WAREHOUSE(51),
    LEGION_CHANGE_EMBLEM(52),
    //PASS_DOORMAN(52),
    OPEN_LEGION_WAREHOUSE(53),
    //CRAFT(53),
    OPEN_PERSONAL_WAREHOUSE(54),
    //EXCHANGE_COIN(54),
    BUY_BY_AP(55),
    //SHOW_MOVIE(55),
    CLOSE_LEGION_WAREHOUSE(56),
    //EDIT_CHARACTER(56),
    PASS_DOORMAN(57),
    //EDIT_GENDER(57),
    CRAFT(58),
    //MATCH_MAKER(58),
    EXCHANGE_COIN(59),
    //MAKE_MERCENARY(59),
    SHOW_MOVIE(60),
    //INSTANCE_ENTRY(60),
    EDIT_CHARACTER(61),
    //COMPOUND_WEAPON(61),
    EDIT_GENDER(62),
    //DECOMPOUND_WEAPON(62),
    MATCH_MAKER(63),
    //FACTION_JOIN(63),
    MAKE_MERCENARY(64),
    //FACTION_LEAVE(64),
    INSTANCE_ENTRY(65),
    //BUY_AGAIN(65),
    COMPOUND_WEAPON(66),
    //PET_ADOPT(66),
    DECOMPOUND_WEAPON(67),
    //PET_ABANDON(67),
    FACTION_JOIN(68),
    //HOUSING_BUILD(68),
    FACTION_LEAVE(69),
    //HOUSING_DESTRUCT(69),
    BUY_AGAIN(70),
    //CHARGE_ITEM_SINGLE(70),
    PET_ADOPT(71),
    //CHARGE_ITEM_MULTI(71),
    PET_ABANDON(72),
    //INSTANCE_PARTY_MATCH(72),
    HOUSING_BUILD(73),
    //TRADE_IN(73),
    HOUSING_DESTRUCT(74),
    //GIVEUP_CRAFT_EXPERT(74),
    CHARGE_ITEM_SINGLE(75),
    //GIVEUP_CRAFT_MASTER(75),
    CHARGE_ITEM_MULTI(76),
    //HOUSING_FRIENDLIST(76),
    INSTANCE_PARTY_MATCH(77),
    //HOUSING_RANDOM_TELEPORT(77),
    TRADE_IN(78),
    //HOUSING_PERSONAL_INS_TELEPORT(78),
    GIVEUP_CRAFT_EXPERT(79),
    //HOUSING_PERSONAL_AUCTION(79),
    GIVEUP_CRAFT_MASTER(80),
    //HOUSING_PAY_RENT(80),
    HOUSING_FRIENDLIST(81),
    //HOUSING_KICK(81),
    HOUSING_RANDOM_TELEPORT(82),
    //HOUSING_CHANGE_BUILDING(82),
    HOUSING_PERSONAL_INS_TELEPORT(83),
    //HOUSING_CONFIG(83),
    HOUSING_PERSONAL_AUCTION(84),
    //HOUSING_GIVEUP(84),
    HOUSING_PAY_RENT(85),
    //HOUSING_CANCEL_GIVEUP(85),
    HOUSING_KICK(86),
    //HOUSING_CREATE_PERSONAL_INS(86);
    HOUSING_CHANGE_BUILDING(87),
    HOUSING_CONFIG(88),
    HOUSING_GIVEUP(89),
    HOUSING_CANCEL_GIVEUP(90),
    HOUSING_CREATE_PERSONAL_INS(91),
    FUNC_PET_H_ADOPT(92),
    FUNC_PET_H_ABANDON(93),
    CHARGE_ITEM_SINGLE2(94),
    CHARGE_ITEM_MULTI2(95),
    HOUSING_RECREATE_PERSONAL_INS(96),
    HOUSING_LIKE(97),
    HOUSING_SCRIPT(98),
    HOUSING_GUESTBOOK(99),
    TOWN_CHALLENGE(100),
    AP_SELL(101),
    // TODO what is 102?
    TRADE_SELL_LIST(103),
    TELEPORT_SIMPLE(104),
    OPEN_INSTANCE_RECRUIT(105),

    /*
     * Quest exclusive stuff.
     * Keep in mind some quest actions are also above here.
     */
    QUEST_ACCEPT_1(1002),
    QUEST_REFUSE_1(1003),
    QUEST_REFUSE_2(1004),
    ASK_QUEST_ACCEPT(1007),
    FINISH_DIALOG(1008),
    SELECT_QUEST_REWARD(1009),
    SELECT_ACTION_1011(1011),
    SELECT_ACTION_1012(1012),
    SELECT_ACTION_1013(1013),
    SELECT_ACTION_1014(1014),
    SELECT_ACTION_1015(1015),
    SELECT_ACTION_1016(1016),
    SELECT_ACTION_1017(1017),
    SELECT_ACTION_1018(1018),
    SELECT_ACTION_1019(1019),
    SELECT_ACTION_1097(1097),
    SELECT_ACTION_1182(1182),
    SELECT_ACTION_1267(1267),
    SELECT_ACTION_1352(1352),
    SELECT_ACTION_1353(1353),
    SELECT_ACTION_1354(1354),
    SELECT_ACTION_1438(1438),
    SELECT_ACTION_1609(1609),
    SELECT_ACTION_1693(1693),
    SELECT_ACTION_1694(1694),
    SELECT_ACTION_1695(1695),
    SELECT_ACTION_1779(1779),
    SELECT_ACTION_2034(2034),
    SELECT_ACTION_2035(2035),
    SELECT_ACTION_2036(2036),
    SELECT_ACTION_2037(2037),
    SELECT_ACTION_2376(2376),
    SELECT_ACTION_2377(2377),
    SELECT_ACTION_2546(2546),
    SELECT_ACTION_2717(2717),
    SELECT_ACTION_2718(2718),
    SELECT_ACTION_2720(2720),
    SELECT_ACTION_3058(3058),
    SELECT_ACTION_3143(3143),
    SELECT_ACTION_3399(3399),
    SELECT_ACTION_3400(3400),
    SELECT_ACTION_3570(3570),
    SELECT_ACTION_3740(3740),
    SELECT_ACTION_3911(3911),
    SELECT_ACTION_4081(4081),
    SELECT_ACTION_4763(4763),

    SETPRO1(10000),
    SETPRO2(10001),
    SETPRO3(10002),
    SETPRO4(10003),
    SETPRO5(10004),
    SETPRO6(10005),
    SETPRO7(10006),
    SETPRO8(10007),
    SETPRO9(10008),
    SETPRO10(10009),
    SETPRO11(10010),
    SETPRO12(10011),
    SETPRO13(10012),
    SETPRO14(10013),
    SETPRO15(10014),
    SETPRO16(10015),
    SETPRO17(10016),
    SETPRO18(10017),
    SETPRO19(10018),
    SETPRO20(10019),
    SETPRO21(10020),
    SETPRO22(10021),
    SETPRO23(10022),
    SETPRO24(10023),
    SETPRO25(10024),
    SETPRO26(10025),
    SETPRO27(10026),
    SETPRO28(10027),
    SETPRO29(10028),
    SETPRO30(10029),
    SETPRO31(10030),
    SETPRO32(10031),
    SETPRO33(10032),
    SETPRO34(10033),
    SETPRO35(10034),
    SETPRO36(10035),
    SETPRO37(10036),
    SETPRO38(10037),
    SETPRO39(10038),
    SETPRO40(10039),
    SETPRO41(10040),
    SET_SUCCEED(10255),
    QUEST_ACCEPT_SIMPLE(20000),
    QUEST_REFUSE_SIMPLE(20001),
    CHECK_USER_HAS_QUEST_ITEM_SIMPLE(20002);

    private final int id;

    private DialogAction(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static DialogAction getActionByDialogId(int id) {
        for (DialogAction da : values()) {
            if (da.id() == id)
                return da;
        }

        return null;
    }
}