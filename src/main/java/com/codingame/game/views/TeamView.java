package com.codingame.game.views;

import com.codingame.game.Player;
import com.codingame.game.models.TeamModel;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Text;

public class TeamView {
    public static final int ENERGY_BAR_SIZE = 100;
    public final PlayerView playerView;
    private final GraphicEntityModule g;
    private final TeamModel teamModel;
    private final int color;
    private Text score;
    private Rectangle Light;

    private Rectangle energyBar;


    TeamView(GraphicEntityModule g, TeamModel teamState, PlayerView playerView, int color) {
        this.g = g;
        this.teamModel = teamState;
        this.playerView = playerView;
        this.color = color;
    }

    public static TeamView fromPlayer(GraphicEntityModule g, TeamModel teamState, Player player) {
        PlayerView v = PlayerView.fromPlayer(g, teamState.player, player);
        return new TeamView(g, teamState, v, player.getColorToken());
    }

    public void restart() {
        this.Light.setFillColor(Colors.COLOR_OFF);
        g.commitEntityState(1, this.Light);
    }

    public void draw() {
        this.score.setText(String.format("%1$2s", teamModel.score).replace(' ', '0'));
        g.commitEntityState(1, this.score);
        this.energyBar.setWidth((int) (teamModel.player.energy / (double) teamModel.player.energyMax * ENERGY_BAR_SIZE));
        playerView.draw();
    }

    public TeamView addLightUI(int x, int y) {
        Rectangle light = g.createRectangle()
                .setWidth(StageView.HALF_WIDTH).setHeight(40)
                .setX(0).setY(0)
                .setLineWidth(5).setLineColor(Colors.BLACK)
                .setFillColor(Colors.COLOR_OFF).setZIndex(0);
        this.Light = light;
        g.createGroup(light).setX(x).setY(y);
        return this;
    }

    public TeamView addScoreUI(int x, int y) {
        this.score = g.createText("00")
                .setFontFamily("Lato").setFontSize(44)
                .setX(5).setY(5).setFillColor(Colors.WHITE).setZIndex(20);

        Rectangle rect = g.createRectangle().setFillAlpha(0).setLineColor(Colors.WHITE).setLineWidth(3)
                .setX(0).setY(0).setWidth(60).setHeight(60);

        g.createGroup(this.score, rect).setX(x).setY(y);
        return this;
    }

    public TeamView addEnergyBarUI(int x, int y) {

        Rectangle jauge = g.createRectangle()
                .setWidth(ENERGY_BAR_SIZE + 10).setHeight(20)
                .setX(0).setY(0)
                .setLineColor(Colors.WHITE).setLineWidth(3)
                .setFillColor(Colors.BLACK).setZIndex(10);

        this.energyBar = g.createRectangle()
                .setWidth(ENERGY_BAR_SIZE).setHeight(10)
                .setX(5).setY(5).setFillColor(Colors.WHITE).setZIndex(10);
        g.createGroup(jauge, this.energyBar).setX(x).setY(y);
        return this;
    }

    public void scored() {

        this.Light.setFillColor(this.color);
        g.commitEntityState(0, this.Light);

        this.Light.setAlpha(0.2);
        g.commitEntityState(0.25, this.Light);

        this.Light.setAlpha(1);
        g.commitEntityState(0.5, this.Light);

        this.Light.setAlpha(0.2);
        g.commitEntityState(0.75, this.Light);

        this.Light.setAlpha(1);
        g.commitEntityState(1, this.Light);
    }
}
