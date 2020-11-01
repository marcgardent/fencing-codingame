package com.codingame.game.models;

public interface RefereObserver {

    void playerTired(PlayerModel player);

    void scored(TeamModel team);

    void outside(PlayerModel player);

    void collided();

    void winTheGame();

    void draw();

    void move(PlayerModel player, int from, int to);

    void energyChanged(PlayerModel player, int delta);

    void hit(PlayerModel player);

    void missed(PlayerModel player);
}
