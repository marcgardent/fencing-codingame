package com.codingame.game;

import com.codingame.game.core.ActionType;
import com.codingame.game.core.GameState;
import com.codingame.game.core.PlayerState;
import com.codingame.game.core.TeamState;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;


public class View {
    private static final int WIDTH = 1920;
    private static final int HALF_WIDTH = 960;
    private static final int HEIGHT = 1080;
    private static final int LINE = 800;
    private static final int COLOR_OFF = 0xAAAAAA;
    private static final int WHITE = 0xFFFFFF;
    private static final int BLACK = 0x000000;
    private static final int GREEN = 0x00FFFF;
    private static final int RED = 0xFF00FF;
    private static final int ENERGY_BAR_SIZE = 100;
    private static final double BOTTOM_ANGLE = Math.PI / 4;
    private static final double MIDDLE_ANGLE = 0;
    private static final double TOP_ANGLE = -Math.PI / 4;
    private static final double BREAK_ANGLE = Math.PI / 3;
    private static final int ARM_DEFENSIVE = -10;
    private static final int ARM_OFFENSIVE = 0;


    @Inject
    private GraphicEntityModule g;
    private final Map<PlayerState, PlayerView> viewByPlayer = new HashMap<>();
    private final Map<TeamState, PlayerView> viewByTeam = new HashMap<>();
    private PlayerView aView;
    private PlayerView bView;

    public void init(GameState state, Player playerA, Player playerB) {
        addBackground();
        aView = createPlayer(state.teamA.player, playerA.getColorToken());
        bView = createPlayer(state.teamB.player, playerB.getColorToken());

        addScoreUI(aView).setX(HALF_WIDTH - 70).setY(LINE + 120);
        addScoreUI(bView).setX(HALF_WIDTH + 10).setY(LINE + 120);

        addEnergyBarUI(aView).setX(HALF_WIDTH - (ENERGY_BAR_SIZE + 20)).setY(LINE + 200);
        addEnergyBarUI(bView).setX(HALF_WIDTH + 10).setY(LINE + 200);

        addLightUI(aView).setX(HALF_WIDTH).setY(LINE + 60);
        addLightUI(bView).setX(0).setY(LINE + 60);

        viewByTeam.put(state.teamA, aView);
        viewByTeam.put(state.teamB, bView);
        viewByPlayer.put(state.teamA.player, aView);
        viewByPlayer.put(state.teamB.player, bView);
        tick(state);
    }

    private PlayerView createPlayer(PlayerState player, int color) {
        PlayerView ret = new PlayerView();
        Rectangle body = g.createRectangle()
                .setWidth(50).setHeight(100)
                .setX(-50).setY(-100)
                .setFillColor(WHITE).setZIndex(10);

        Rectangle head = g.createRectangle()
                .setWidth(50).setHeight(50)
                .setX(-50).setY(-160)
                .setFillColor(WHITE).setZIndex(10);

        Rectangle grid = g.createRectangle()
                .setWidth(15).setHeight(40)
                .setX(-50 + 30).setY(-160 + 5)
                .setFillColor(BLACK).setZIndex(10);

        int halfRange = getLogicToWorld(ActionType.OFFENSIVE_ATTITUDE.offensiveRange / 2);

        Rectangle arm = g.createRectangle()
                .setWidth(halfRange).setHeight(20)
                .setX(0).setY(-10)
                .setFillColor(WHITE).setZIndex(10);

        Rectangle hand = g.createRectangle()
                .setWidth(20).setHeight(20)
                .setX(halfRange - 20).setY(-10)
                .setLineWidth(4).setLineColor(WHITE)
                .setFillColor(color).setZIndex(10);

        Rectangle blade = g.createRectangle()
                .setWidth(halfRange + 10).setHeight(10)
                .setX(halfRange).setY(-5)
                .setFillColor(color).setZIndex(10);

        ret.ko = g.createText("~!#?").setFillColor(WHITE).setX(-20).setY(-220).setFontFamily("Lato").setFontSize(40).setVisible(false);
        ret.energy = g.createText("+2").setFillColor(WHITE).setX(-10).setY(-260).setFontFamily("Lato")
                .setFontSize(40).setVisible(false).setStrokeThickness(3).setFontWeight(Text.FontWeight.BOLD);
        ret.arm = g.createGroup(arm, hand, blade).setX(ARM_DEFENSIVE).setY(-80).setRotation(Math.PI / 4);

        Group p = g.createGroup(body, ret.ko, ret.arm, head, grid).setScaleX(player.orientation);
        Group texts = g.createGroup(ret.ko);
        ret.character = g.createGroup(p, texts).setY((int) (LINE));

        ret.Color = color;
        return ret;
    }

    private void addBackground() {
        g.createRectangle()
                .setWidth(WIDTH).setHeight(HEIGHT)
                .setX(0).setY(0)
                .setFillColor(BLACK).setZIndex(0);

        //blue floor
        g.createRectangle()
                .setWidth(WIDTH).setHeight(120)
                .setX(0).setY(LINE - 60)
                .setFillColor(0x2061D4).setZIndex(0);

        //red floor
        g.createRectangle()
                .setWidth(WIDTH - 100).setHeight(60)
                .setX(50).setY(LINE - 30)
                .setFillColor(0xD44020).setZIndex(0);
        // lines
        g.createRectangle()
                .setWidth(WIDTH - 100).setHeight(60)
                .setX(50).setY(LINE - 30).setFillAlpha(0)
                .setLineColor(WHITE).setLineWidth(5).setZIndex(0);
        //spawn line
        g.createRectangle()
                .setWidth(getLogicToWorld(PlayerState.SPAWN_POSITION_B - PlayerState.SPAWN_POSITION_A)).setHeight(60)
                .setX(getLogicToWorld(PlayerState.SPAWN_POSITION_A)).setY(LINE - 30).setFillAlpha(0)
                .setLineColor(WHITE).setLineWidth(5).setZIndex(0);
    }

    public void tick(GameState state) {
        drawPlayer(aView, state.teamA);
        drawPlayer(bView, state.teamB);
    }

    public void restart(GameState state) {
        restartPlayer(aView, state.teamA);
        restartPlayer(bView, state.teamB);
    }

    private void restartPlayer(PlayerView v, TeamState state) {
        v.Light.setFillColor(COLOR_OFF);
        g.commitEntityState(1, v.Light);
        drawPlayer(v, state);
    }

    private void drawPlayer(PlayerView v, TeamState team) {
        //Reset
        v.ko.setRotation(0).setVisible(false);
        g.commitEntityState(0, v.ko);
        v.energy.setVisible(false);
        g.commitEntityState(0, v.energy);

        {
            //posture & attitude
            double angle = MIDDLE_ANGLE;
            if (team.player.posture == ActionType.TOP_POSTURE) angle = TOP_ANGLE;
            else if (team.player.posture == ActionType.BOTTOM_POSTURE) angle = BOTTOM_ANGLE;
            else if (team.player.attitude == ActionType.BREAK_ATTITUDE) angle = BREAK_ANGLE;
            v.arm.setRotation(angle);
            v.arm.setX(ARM_DEFENSIVE);
            g.commitEntityState(0, v.arm);

            //offensive attitude
            if (team.player.attitude == ActionType.OFFENSIVE_ATTITUDE) {
                v.arm.setX(ARM_OFFENSIVE);
                g.commitEntityState(0.33, v.arm);
                v.arm.setX(ARM_DEFENSIVE);
                g.commitEntityState(0.66, v.arm);
                v.arm.setX(ARM_OFFENSIVE);
                g.commitEntityState(1, v.arm);
            }
        }
        v.energyBar.setWidth((int) (team.player.energy / (double) team.player.energyMax * ENERGY_BAR_SIZE));
        v.score.setText(String.format("%1$2s", team.score).replace(' ', '0'));
        g.commitEntityState(1, v.score);
        v.character.setX(getLogicToWorld(team.player.position));
        g.commitEntityState(1, v.character);
    }

    private Group addLightUI(PlayerView v) {
        Rectangle light = g.createRectangle()
                .setWidth(HALF_WIDTH).setHeight(40)
                .setX(0).setY(0)
                .setLineWidth(5).setLineColor(BLACK)
                .setFillColor(COLOR_OFF).setZIndex(0);
        v.Light = light;
        return g.createGroup(light);
    }

    private Group addScoreUI(PlayerView v) {
        v.score = g.createText("00")
                .setFontFamily("Lato").setFontSize(44)
                .setX(5).setY(5).setFillColor(WHITE).setZIndex(20);

        Rectangle rect = g.createRectangle().setFillAlpha(0).setLineColor(WHITE).setLineWidth(3)
                .setX(0).setY(0).setWidth(60).setHeight(60);

        return g.createGroup(v.score, rect);
    }

    private Group addEnergyBarUI(PlayerView v) {

        Rectangle jauge = g.createRectangle()
                .setWidth(ENERGY_BAR_SIZE + 10).setHeight(20)
                .setX(0).setY(0)
                .setLineColor(WHITE).setLineWidth(3)
                .setFillColor(BLACK).setZIndex(10);

        v.energyBar = g.createRectangle()
                .setWidth(ENERGY_BAR_SIZE).setHeight(10)
                .setX(5).setY(5).setFillColor(WHITE).setZIndex(10);
        return g.createGroup(jauge, v.energyBar);
    }

    private int getLogicToWorld(int v) {
        return (int) ((float) v / (float) (PlayerState.MAX_POSITION - PlayerState.MIN_POSITION) * (float) WIDTH);
    }

    public void move(PlayerState player, int from, int to) {

    }

    public void score(TeamState team) {
        PlayerView v = viewByTeam.get(team);
        v.Light.setFillColor(v.Color);
        g.commitEntityState(0, v.Light);

        v.Light.setAlpha(0.2);
        g.commitEntityState(0.25, v.Light);

        v.Light.setAlpha(1);
        g.commitEntityState(0.5, v.Light);

        v.Light.setAlpha(0.2);
        g.commitEntityState(0.75, v.Light);

        v.Light.setAlpha(1);
        g.commitEntityState(1, v.Light);
    }

    public void hit(PlayerState player) {

    }

    public void playerKo(PlayerState player) {
        PlayerView v = viewByPlayer.get(player);
        v.ko.setVisible(true).setRotation(Math.PI / 20);
        g.commitEntityState(0.1, v.ko);

        v.ko.setRotation(-Math.PI / 20);
        g.commitEntityState(0.5, v.ko);

        v.ko.setRotation(0);
        g.commitEntityState(1, v.ko);
    }

    public void energyChanged(PlayerState player, int delta) {

        PlayerView v = viewByPlayer.get(player);
        v.energy.setText((delta > 0 ? "+" : "") + Integer.toString(delta)).setVisible(true).setAlpha(0).setStrokeColor(delta > 0 ? GREEN : RED);
        g.commitEntityState(0.1, v.energy);

        v.energy.setAlpha(0.4);
        g.commitEntityState(0.3, v.energy);

    }

    private class PlayerView {
        public Group character;
        public Text score;
        public Rectangle Light;
        public int Color;
        public Group arm;
        public Text ko;
        public Text energy;
        public Rectangle energyBar;
    }
}