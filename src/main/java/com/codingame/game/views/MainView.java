package com.codingame.game.views;

import com.codingame.game.Player;
import com.codingame.game.core.GameState;
import com.codingame.game.core.PlayerState;
import com.codingame.game.core.TeamState;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public class MainView {


    private final Map<PlayerState, TeamView> viewByPlayer = new HashMap<>();
    private final Map<TeamState, TeamView> viewByTeam = new HashMap<>();
    @Inject
    private GraphicEntityModule g;
    private TeamView aView;
    private TeamView bView;


    public void init(GameState state, Player playerA, Player playerB) {
        new StageView(g).init();

        aView = TeamView.fromPlayer(g, state.teamA, playerA)
                .addScoreUI(StageView.HALF_WIDTH - 70, StageView.LINE + 120)
                .addEnergyBarUI(StageView.HALF_WIDTH - (TeamView.ENERGY_BAR_SIZE + 20), StageView.LINE + 200)
                .addLightUI(StageView.HALF_WIDTH, StageView.LINE + 60);

        bView = TeamView.fromPlayer(g, state.teamB, playerB)
                .addScoreUI(StageView.HALF_WIDTH + 10, StageView.LINE + 120)
                .addEnergyBarUI(StageView.HALF_WIDTH + 10, StageView.LINE + 200)
                .addLightUI(0, StageView.LINE + 60);

        viewByTeam.put(state.teamA, aView);
        viewByTeam.put(state.teamB, bView);
        viewByPlayer.put(state.teamA.player, aView);
        viewByPlayer.put(state.teamB.player, bView);
        tick();
    }

    public void tick() {
        aView.draw();
        bView.draw();
    }

    public void restart() {
        aView.restart();
        bView.restart();
    }


    public void move(PlayerState player, int from, int to) {

    }

    public void hit(PlayerState player) {
        viewByPlayer.get(player).playerView.hit();
    }


    public void playerKo(PlayerState player) {
        viewByPlayer.get(player).playerView.playerKo();
    }

    public void energyChanged(PlayerState player, int delta) {
        viewByPlayer.get(player).playerView.energyChanged(delta);
    }

    public void scored(TeamState team) {
        viewByTeam.get(team).scored();
    }
}