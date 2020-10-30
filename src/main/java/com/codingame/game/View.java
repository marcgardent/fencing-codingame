package com.codingame.game;

import com.codingame.game.core.GameInput;
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

    @Inject
    private GraphicEntityModule g;
    private PlayerView AView;
    private PlayerView BView;
    private Map<PlayerState, PlayerView> viewByPlayer = new HashMap<>();
    private Map<TeamState, PlayerView> viewByTeam = new HashMap<>();

    public void Init(GameState state, Player playerA, Player playerB) {
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

        Text scoreA = g.createText("00")
                .setFontFamily("Lato").setFontSize(50)
                .setX(HALF_WIDTH - 70).setY(LINE + 100).setFillColor(WHITE).setZIndex(20);

        Text scoreB = g.createText("00")
                .setFontFamily("Lato").setFontSize(50)
                .setX(HALF_WIDTH + 10).setY(LINE + 100).setFillColor(WHITE).setZIndex(20);

        Rectangle lightA = g.createRectangle()
                .setWidth(HALF_WIDTH).setHeight(40)
                .setX(0).setY(LINE + 60)
                .setLineWidth(5).setLineColor(BLACK)
                .setFillColor(COLOR_OFF).setZIndex(0);

        Rectangle lightB = g.createRectangle()
                .setWidth(HALF_WIDTH).setHeight(40)
                .setX(HALF_WIDTH).setY(LINE + 60)
                .setLineWidth(5).setLineColor(BLACK)
                .setFillColor(COLOR_OFF).setZIndex(0);

        AView = createPlayer(state.TeamA.Player, lightA, scoreA, playerA.getColorToken());
        BView = createPlayer(state.TeamB.Player, lightB, scoreB, playerB.getColorToken());
        viewByTeam.put(state.TeamA, AView);
        viewByTeam.put(state.TeamB, BView);
        viewByPlayer.put(state.TeamA.Player, AView);
        viewByPlayer.put(state.TeamB.Player, BView);
        Tick(state);
    }


    public void Score(TeamState team) {
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

    public void Tick(GameState state) {

        drawPlayer(AView, state.TeamA);
        drawPlayer(BView, state.TeamB);
    }

    public void Restart(GameState state) {
        restartPlayer(AView, state.TeamA);
        restartPlayer(BView, state.TeamB);
    }

    private void restartPlayer(PlayerView v, TeamState state) {
        v.Light.setFillColor(COLOR_OFF);
        g.commitEntityState(1, v.Light);
        drawPlayer(v, state);
    }

    private void drawPlayer(PlayerView v, TeamState team) {
        //Reset
        v.Kao.setRotation(0).setVisible(false);
        g.commitEntityState(0, v.Kao);
        v.Energy.setVisible(false);
        g.commitEntityState(0, v.Energy);

        v.EnergyBar.setWidth((int) (team.Player.Energy / (double) team.Player.EnergyMax * 100));

        v.Score.setText(String.format("%1$2s", team.Score).replace(' ', '0'));
        g.commitEntityState(1, v.Score);
        v.Character.setX(getLogicToWorld(team.Player.Position));
        g.commitEntityState(1, v.Character);
    }

    private PlayerView createPlayer(PlayerState player, Rectangle light, Text score, int color) {
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
        int halfRange = getLogicToWorld(player.Range / 2);

        Rectangle arm = g.createRectangle()
                .setWidth(halfRange).setHeight(20)
                .setX(0).setY(-10)
                .setFillColor(WHITE).setZIndex(10);

        Rectangle blade = g.createRectangle()
                .setWidth(halfRange + 10).setHeight(10)
                .setX(halfRange).setY(-5)
                .setFillColor(color).setZIndex(10);

        Rectangle jauge = g.createRectangle()
                .setWidth(110).setHeight(30)
                .setX(-55).setY(-305)
                .setLineColor(WHITE).setLineWidth(3)
                .setFillColor(BLACK).setZIndex(10);

        ret.EnergyBar = g.createRectangle()
                .setWidth((int) (player.Energy / (double) player.EnergyMax * 100)).setHeight(20)
                .setX(-50).setY(-300)
                .setFillColor(WHITE).setZIndex(10);


        ret.Kao = g.createText("~!#?").setFillColor(WHITE).setX(-20).setY(-220).setFontFamily("Lato").setFontSize(40).setVisible(false);
        ret.Energy = g.createText("+2").setFillColor(WHITE).setX(-10).setY(-260).setFontFamily("Lato")
                .setFontSize(40).setVisible(false).setStrokeThickness(3).setFontWeight(Text.FontWeight.BOLD);
        ret.Arm = g.createGroup(arm, blade).setX(-10).setY(-80).setRotation(Math.PI / 4);

        Group p = g.createGroup(body, ret.Kao, ret.Arm, head, grid).setScaleX(player.Orientation);
        Group texts = g.createGroup(ret.Kao, ret.Energy, jauge, ret.EnergyBar);
        ret.Character = g.createGroup(p, texts).setY((int) (LINE));

        ret.Score = score;
        ret.Light = light;
        ret.Color = color;
        return ret;
    }

    private int getLogicToWorld(int v) {
        return (int) ((float) v / (float) (PlayerState.MAX_POSITION - PlayerState.MIN_POSITION) * (float) WIDTH);
    }

    public void Move(PlayerState player, int from, int to) {

    }


    public void ActionResolved(PlayerState player, byte action) {

        PlayerView v = viewByPlayer.get(player);
        if (action == GameInput.BASIC_ATTACK) {
            v.Arm.setRotation(0);
            g.commitEntityState(0, v.Arm);

            v.Arm.setRotation(Math.PI / 4);
            g.commitEntityState(1, v.Arm);
        } else if (action == GameInput.NORMAL_ATTACK) {
            v.Arm.setRotation(-Math.PI / 4);
            g.commitEntityState(0, v.Arm);

            v.Arm.setRotation(0);
            g.commitEntityState(0.25, v.Arm);

            v.Arm.setRotation(0);
            g.commitEntityState(0.75, v.Arm);

            v.Arm.setRotation(Math.PI / 4);
            g.commitEntityState(1, v.Arm);
        } else if (action == GameInput.COMPLEX_ATTACK) {
            v.Arm.setRotation(-Math.PI / 4);
            g.commitEntityState(0, v.Arm);

            v.Arm.setRotation(Math.PI / 4);
            g.commitEntityState(0.5, v.Arm);

            v.Arm.setRotation(0);
            g.commitEntityState(0.75, v.Arm);

            v.Arm.setRotation(Math.PI / 4);
            g.commitEntityState(1, v.Arm);
        }


    }

    public void PlayerKao(PlayerState player) {
        PlayerView v = viewByPlayer.get(player);
        v.Kao.setVisible(true).setRotation(Math.PI / 20);
        g.commitEntityState(0.1, v.Kao);

        v.Kao.setRotation(-Math.PI / 20);
        g.commitEntityState(0.5, v.Kao);

        v.Kao.setRotation(0);
        g.commitEntityState(1, v.Kao);
    }

    public void EnergyChanged(PlayerState player, int delta) {

        PlayerView v = viewByPlayer.get(player);
        v.Energy.setText((delta > 0 ? "+" : "") + Integer.toString(delta)).setVisible(true).setAlpha(0).setStrokeColor(delta > 0 ? GREEN : RED);
        g.commitEntityState(0.1, v.Energy);

        v.Energy.setAlpha(0.4);
        g.commitEntityState(0.3, v.Energy);

    }


    private class PlayerView {
        public Group Character;
        public Text Score;
        public Rectangle Light;
        public int Color;
        public Group Arm;
        public Text Kao;
        public Text Energy;
        public Rectangle EnergyBar;
    }
}