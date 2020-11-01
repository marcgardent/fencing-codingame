package com.codingame.game.views;

import com.codingame.game.Player;
import com.codingame.game.models.GameModel;
import com.codingame.game.models.PlayerModel;
import com.codingame.game.models.TeamModel;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public class MainView {


    private final Map<PlayerModel, TeamView> viewByPlayer = new HashMap<>();
    private final Map<TeamModel, TeamView> viewByTeam = new HashMap<>();
    @Inject
    private GraphicEntityModule g;
    private TeamView aView;
    private TeamView bView;
    private StageView stage;
    private boolean restarted = true;


    public void init(GameModel state, Player playerA, Player playerB) {
        stage = new StageView(g);
        stage.init();

        aView = TeamView.fromPlayer(g, state.teamA, playerA)
                .addScoreUI(StageView.HALF_WIDTH - 110, StageView.LINE - 400)
                .addEnergyBarUI(StageView.HALF_WIDTH - (TeamView.ENERGY_BAR_SIZE + 20), StageView.LINE - 290)
                .setPlayerBlock(0, StageView.LINE + 60);

        bView = TeamView.fromPlayer(g, state.teamB, playerB)
                .addScoreUI(StageView.HALF_WIDTH + 10, StageView.LINE - 400)
                .addEnergyBarUI(StageView.HALF_WIDTH + 10, StageView.LINE - 290)
                .setPlayerBlock(StageView.HALF_WIDTH, StageView.LINE + 60);

        viewByTeam.put(state.teamA, aView);
        viewByTeam.put(state.teamB, bView);
        viewByPlayer.put(state.teamA.player, aView);
        viewByPlayer.put(state.teamB.player, bView);
        tick();
    }

    public void tick() {
        aView.draw();
        bView.draw();
        stage.reset();
        if (restarted) {
            stage.addMessage("Prêt, Allez");
            restarted = false;
        }
    }

    public void restart() {
        restarted = true;
        aView.restart();
        bView.restart();
        stage.reset();
        stage.addMessage("Halte, En Garde");
    }

    public void move(PlayerModel player, int from, int to) {

    }

    public void hit(PlayerModel player) {
        viewByPlayer.get(player).playerView.hit();

    }


    public void playerKo(PlayerModel player) {
        viewByPlayer.get(player).playerView.playerKo();
    }

    public void energyChanged(PlayerModel player, int delta) {
        viewByPlayer.get(player).playerView.energyChanged(delta);
    }

    public void scored(TeamModel team) {
        viewByTeam.get(team).scored();
    }
}