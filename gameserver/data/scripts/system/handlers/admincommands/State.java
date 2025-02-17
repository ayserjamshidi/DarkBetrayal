/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.state.CreatureState;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_INFO;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Rolandas
 */
public class State extends ChatCommand {

    static final Map<Integer, CreatureState> creatureStateLookup = new HashMap<>();
    static final Map<Integer, TestState> testStateLookup = new HashMap<>();

    static {
        for (CreatureState s : EnumSet.allOf(CreatureState.class)) {
            creatureStateLookup.put(s.getId(), s);
        }
        for (TestState t : EnumSet.allOf(TestState.class)) {
            testStateLookup.put(t.id, t);
        }
    }

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        VisibleObject target = admin.getTarget();

        if (target == null) {
            admin.sendMsg("Select a target first!!!");
            return;
        }

        if (params.length == 0) {
            admin.sendMsg("syntax //state <show | set | unset>");
            return;
        }

        if (!(target instanceof Creature)) {
            admin.sendMsg("You can select only creatures!!!");
            return;
        }

        Creature creature = (Creature) target;

        if (params[0].equals("show")) {
            if (params.length != 1) {
                admin.sendMsg("syntax //state show");
                return;
            }

            if (creature.equals(admin)) {
                admin.sendMsg("Your state is : " + creature.getState() + "\n" + getStateDescription((short) admin.getState()));
            } else {
                admin.sendMsg("Creature state is : " + creature.getState() + "\n" + getStateDescription((short) creature.getState()));
            }
        } else if (params[0].equals("set") || params[0].equals("unset")) {
            if (params.length != 2) {
                admin.sendMsg("syntax //state set <bit number>");
                return;
            }
            int number;
            try {
                number = Integer.valueOf(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("syntax //state set <bit number>");
                return;
            }

            if (number < 1 || number > 16) {
                admin.sendMsg("syntax <bit number> should be in range 1-16");
                return;
            }

            short newState;

            if (params[0].equals("set")) {
                newState = (short) (creature.getState() & 0xFFFF | 1 << number - 1);
            } else {
                newState = (short) (creature.getState() & 0xFFFF & ~(1 << number - 1));
            }

            admin.sendMsg("New state : " + newState);
            creature.setState(newState);

            if (target.equals(admin)) {
                admin.sendPck(new SM_PLAYER_INFO(admin, false));
            }

            admin.clearKnownlist();
            admin.updateKnownlist();

            admin.sendMsg("State changed to : " + creature.getState() + "\n" + getStateDescription((short) creature.getState()));
        } else {
            admin.sendMsg("syntax //state <show | set | unset>");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
    }

    String getStateDescription(short state) {
        StringBuilder binsb = new StringBuilder(Integer.toBinaryString(state));
        StringBuilder bin = binsb.reverse();

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        for (int i = 0; i < bin.length(); i++) {
            if (bin.charAt(i) == '1') {
                sb.append("0x");
                int value = 1 << i;
                sb.append(Integer.toHexString(value));

                sb.append(" (");
                sb.append(testStateLookup.get(value).display);
                if (creatureStateLookup.containsKey(value)) {
                    sb.append('=');
                    sb.append(creatureStateLookup.get(value).toString());
                }

                sb.append("),\n");
            }
        }
        if (sb.lastIndexOf(",\n") == sb.length() - 2) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("\n}");
        return sb.toString();
    }

    public enum TestState {
        BIT01(1 << 0, "bit 1"),
        BIT02(1 << 1, "bit 2"),
        BIT03(1 << 2, "bit 3"),
        BIT04(1 << 3, "bit 4"),
        BIT05(1 << 4, "bit 5"),
        BIT06(1 << 5, "bit 6"),
        BIT07(
            1 << 6, "bit 7"),
        BIT08(1 << 7, "bit 8"),
        BIT09(1 << 8, "bit 9"),
        BIT10(1 << 9, "bit 10"),
        BIT11(1 << 10, "bit 11"),
        BIT12(1 << 11, "bit 12"),
        BIT13(1 << 12, "bit 13"),
        BIT14(1 << 13, "bit 14"),
        BIT15(1 << 14, "bit 15"),
        BIT16(1 << 15, "bit 16");

        final int id;
        final String display;

        TestState(int value, String s) {
            id = value;
            display = s;
        }
    }

}
