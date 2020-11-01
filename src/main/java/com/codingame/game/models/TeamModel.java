package com.codingame.game.models;

import java.util.LinkedList;

public class TeamModel {

    public static final byte SCORE_MAX = 20;
    public static final byte SCORE_GAP = 1;

    public byte score;

    public PlayerModel player = new PlayerModel();

    public LinkedList<String> messages = new LinkedList<>();

    public boolean touched = false;
}