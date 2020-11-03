package com.codingame.game.views;

import com.codingame.game.Player;
import com.codingame.game.models.ActionType;
import com.codingame.game.models.PlayerModel;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Text;

public class PlayerView {

    private static final double BOTTOM_ANGLE = Math.PI / 8;
    private static final double MIDDLE_ANGLE = -Math.PI / 32;
    private static final double TOP_ANGLE = -Math.PI / 8;
    private static final double BREAK_ANGLE = Math.PI / 3;

    private static final int ARM_DEFENSIVE_X = -50;
    private static final int ARM_DEFENSIVE_Y = -60;

    private static final int ARM_OFFENSIVE_X = 0;
    private static final int ARM_OFFENSIVE_Y = -80;

    private final GraphicEntityModule g;
    private final PlayerModel playerModel;
    private Group character;
    private int Color;
    private Group arm;
    private Text ko;
    private Text energy;

    PlayerView(GraphicEntityModule g, PlayerModel playerModel) {
        this.g = g;
        this.playerModel = playerModel;
    }

    public static PlayerView fromPlayer(GraphicEntityModule g, PlayerModel playerModel, Player player) {
        PlayerView ret = new PlayerView(g, playerModel);
        Rectangle body = g.createRectangle()
                .setWidth(50).setHeight(100)
                .setX(-50).setY(-100)
                .setFillColor(Colors.WHITE).setZIndex(10);

        Rectangle head = g.createRectangle()
                .setWidth(50).setHeight(50)
                .setX(-50).setY(-160)
                .setFillColor(Colors.WHITE).setZIndex(10);

        Rectangle grid = g.createRectangle()
                .setWidth(15).setHeight(40)
                .setX(-50 + 30).setY(-160 + 5)
                .setFillColor(Colors.BLACK).setZIndex(10);

        int halfRange = StageView.getLogicToWorld(ActionType.LUNGE.offensiveRange / 2);

        Rectangle arm = g.createRectangle()
                .setWidth(halfRange).setHeight(20)
                .setX(0).setY(-10)
                .setFillColor(Colors.WHITE).setZIndex(10);

        Rectangle hand = g.createRectangle()
                .setWidth(20).setHeight(20)
                .setX(halfRange - 20).setY(-10)
                .setLineWidth(4).setLineColor(Colors.WHITE)
                .setFillColor(player.getColorToken()).setZIndex(10);

        Rectangle blade = g.createRectangle()
                .setWidth(halfRange + 10).setHeight(10)
                .setX(halfRange).setY(-5)
                .setFillColor(player.getColorToken()).setZIndex(10);

        ret.ko = g.createText("~!#?").setFillColor(Colors.WHITE).setX(-20).setY(-220).setFontFamily("Lato").setFontSize(40).setVisible(false);
        ret.energy = g.createText("+2").setFillColor(Colors.WHITE).setX(-10).setY(-260).setFontFamily("Lato")
                .setFontSize(40).setVisible(false).setStrokeThickness(3).setFontWeight(Text.FontWeight.BOLD);
        ret.arm = g.createGroup(arm, hand, blade).setX(ARM_DEFENSIVE_X).setY(ARM_DEFENSIVE_Y).setRotation(Math.PI / 4);

        Group p = g.createGroup(body, ret.ko, ret.arm, head, grid);
        if (playerModel.orientation < 0) {
            p.setScaleX(playerModel.orientation).setX(50);
        }
        Group texts = g.createGroup(ret.ko);
        ret.character = g.createGroup(p, texts).setY((int) (StageView.LINE));

        ret.Color = player.getColorToken();

        return ret;
    }

    public void restartPlayer() {
        draw();
    }

    public void draw() {
        //Reset
        this.ko.setRotation(0).setVisible(false);
        g.commitEntityState(0, this.ko);
        this.energy.setVisible(false);
        g.commitEntityState(0, this.energy);

        {
            //posture
            double angle = MIDDLE_ANGLE;
            if (playerModel.posture == ActionType.TOP_POSTURE) angle = TOP_ANGLE;
            else if (playerModel.posture == ActionType.BOTTOM_POSTURE) angle = BOTTOM_ANGLE;
            this.arm.setRotation(angle);
            this.arm.setX(ARM_DEFENSIVE_X).setY(ARM_DEFENSIVE_Y);
            g.commitEntityState(0, this.arm);

        }

        this.character.setX(StageView.getLogicToWorld(playerModel.position));
        g.commitEntityState(1, this.character);
    }

    public void playerKo() {
        this.ko.setVisible(true).setRotation(Math.PI / 20);
        g.commitEntityState(0.1, this.ko);

        this.ko.setRotation(-Math.PI / 20);
        g.commitEntityState(0.5, this.ko);

        this.ko.setRotation(0);
        g.commitEntityState(1, this.ko);
    }

    public void energyChanged(int delta) {
        this.energy.setText((delta > 0 ? "+" : "") + Integer.toString(delta)).setVisible(true).setAlpha(0).setStrokeColor(delta > 0 ? Colors.GREEN : Colors.RED);
        g.commitEntityState(0.1, this.energy);

        this.energy.setAlpha(0.4);
        g.commitEntityState(0.3, this.energy);

    }

    public void hit() {
        this.arm.setX(ARM_OFFENSIVE_X).setY(ARM_OFFENSIVE_Y);
        g.commitEntityState(1, this.arm);
    }

    public void missed() {
        hit();
    }
}
