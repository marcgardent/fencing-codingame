package com.codingame.game.core;

import java.util.LinkedList;

public class TeamState {

    public static final byte SCORE_MAX = 20;
    public static final byte SCORE_GAP = 1;

    public byte score;

    public PlayerState player = new PlayerState();

    public LinkedList<String> messages = new LinkedList<>();

    public boolean touched = false;
}
