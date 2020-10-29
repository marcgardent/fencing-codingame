package com.codingame.game.core;

public interface RefereObserver {

    void PlayerKao(PlayerState player);

    void ScoreAB();

    void Score(TeamState team);

    void Outside(PlayerState player);

    void Collide();

    void WinTheGame(TeamState team);

    void Draw();
}
