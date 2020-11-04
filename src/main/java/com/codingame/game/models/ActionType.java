package com.codingame.game.models;

import java.util.Arrays;

public enum ActionType {

    //League 0
    SUPPRESSED(0, Integer.MAX_VALUE, 0, 0, 0, 0),
    BREAK(1, 0, 2, 0, 0, 0),
    WALK(2, 0, -1, 20, 0, 0),
    RETREAT(3, 0, -1, -20, 0, 0),
    LUNGE(4, 0, -2, 0, 40, 0),
    PARRY(5, 0, -2, 0, -40, 0),

    //Boss -> GA algorithm

    //League 1
    MIDDLE_POSTURE(6, 1, -1, 0, 0, 0),
    TOP_POSTURE(7, 1, -1, 0, 0, 0),
    BOTTOM_POSTURE(8, 1, -1, 0, 0, 0),
    DOUBLE_FORWARD_MOVE(9, 1, -4, 40, 0, 0),
    DOUBLE_BACKWARD_MOVE(10, 1, -4, -30, 0, 0),

    //Boss -> TreeExplore algorithm

    // league 2
    OFFENSIVE_RANGE_SKILL(11, 2, -5, 0, 0, 5),
    DEFENSIVE_RANGE_SKILL(12, 2, -5, 0, 0, 5),
    ENERGY_MAX_SKILL(13, 2, -5, 0, 0, 5),
    FORWARD_SKILL(14, 2, -5, 0, 0, 5),
    BACKWARD_SKILL(15, 2, -5, 0, 0, 5),
    DOUBLE_FORWARD_SKILL(16, 2, -5, 0, 0, 10),
    DOUBLE_BACKWARD_SKILL(17, 2, -5, 0, 0, 10);

    public final int code;
    public final int league;
    public final int energy;
    public final int move;
    public final int distance;
    public final int doping;

    ActionType(int code, int league, int energy, int move, int distance, int doping) {
        this.code = code;
        this.league = league;
        this.energy = energy;
        this.move = move;
        this.distance = distance;
        this.doping = doping;
    }

    public static ActionType fromInteger(int code) {
        return Arrays.stream(ActionType.values())
                .filter(x -> x.code == code).findFirst().orElseGet(() -> null);
    }

    public static ActionType fromString(String name) {
        return Arrays.stream(ActionType.values())
                .filter(x -> x.name().equals(name)).findFirst().orElseGet(() -> null);
    }
}