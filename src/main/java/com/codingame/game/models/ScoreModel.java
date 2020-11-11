package com.codingame.game.models;

public class ScoreModel {
    public final int teamA;
    public final int teamB;

    public ScoreModel(GameModel state, boolean isActiveA, boolean isActiveB) {
        boolean nonCombativityPenality =
                isActiveB && isActiveA &&
                        state.teamA.score == 0 && state.teamB.score == 0 &&
                        state.teamA.player.energy >= 0 && state.teamB.player.energy >= 0;

        boolean penalityA = nonCombativityPenality || !isActiveA || state.teamA.player.energy < 0;
        boolean penalityB = nonCombativityPenality || !isActiveB || state.teamA.player.energy < 0;

        teamA = (!penalityA ? (state.teamA.score - state.teamB.score) : -20);
        teamB = (!penalityB ? (state.teamB.score - state.teamA.score) : -20);
    }
}