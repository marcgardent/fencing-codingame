package com.codingame.game.core;

public interface RefereObserver {

    void PlayerKao(PlayerState player);

    void ScoreAB();

    void Score(TeamState team);

    void Outside(PlayerState player);

    void Collide();

    void WinTheGame(TeamState winner, TeamState looser);

    void Draw();

    void Move(PlayerState player, int from, int to);

    void EnergyChanged(PlayerState player, int delta);

    void ActionResolved(PlayerState player, byte aResolved);
}
