package com.codingame.game.core;

public class TeamState {

    public static final byte SCORE_MAX = 20;
    public static final byte SCORE_GAP = 1;

    public byte Score;

    public PlayerState Player = new PlayerState();
}
