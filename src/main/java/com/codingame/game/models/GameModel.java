package com.codingame.game.models;

public class GameModel {

    public int tick = 0;

    public boolean restart = false;

    public TeamModel teamA = new TeamModel('A');

    public TeamModel teamB = new TeamModel('B');
}
