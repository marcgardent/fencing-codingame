package com.codingame.game.models;

import java.util.Arrays;

public enum ActionType {
    //League 0
    SUPPRESSED(0, 0, 0, 0, 0, 0),
    MIDDLE_POSTURE(1, 0, -1, 0, 0, 0),
    FORWARD_MOVE(2, 0, -1, 50, 0, 0),
    BACKWARD_MOVE(3, 0, -1, -50, 0, 0),
    OFFENSIVE_ATTITUDE(4, 0, -1, 0, 0, 30),
    BREAK_ATTITUDE(5, 0, 2, 0, 0, 0),
    DEFENSIVE_ATTITUDE(6, 0, -1, 0, 30, 0),

    //League 1
    TOP_POSTURE(7, 1, -1, 0, 0, 0),
    BOTTOM_POSTURE(8, 1, -1, 0, 0, 0),

    //League 2
    DOUBLE_FORWARD_MOVE(9, 2, -1, 100, 0, 0),
    DOUBLE_BACKWARD_MOVE(10, 2, -1, -75, 0, 0),

    // league 3
    OFFENSIVE_RANGE_SKILL(11, 3, -5, 0, 0, 0),
    DEFENSIVE_RANGE_SKILL(12, 3, -5, 0, 0, 0),
    ENERGY_MAX_SKILL(13, 3, -5, 0, 0, 0),
    FORWARD_SKILL(14, 3, -5, 0, 0, 0),
    BACKWARD_SKILL(15, 3, -5, 0, 0, 0),
    DOUBLE_FORWARD_SKILL(16, 3, -5, 0, 0, 0),
    DOUBLE_BACKWARD_SKILL(17, 3, -5, 0, 0, 0);

    public final int code;
    public final int league;
    public final int energy;
    public final int move;
    public final int defensiveRange;
    public final int offensiveRange;

    ActionType(int code, int league, int energy, int move, int defensiveRange, int offensiveRange) {
        this.code = code;
        this.league = league;
        this.energy = energy;
        this.move = move;
        this.defensiveRange = defensiveRange;
        this.offensiveRange = offensiveRange;
    }

    public static ActionType fromInteger(int code) {
        return Arrays.stream(ActionType.values())
                .filter(x -> x.code == code).findFirst().orElseGet(() -> null);
    }
}
