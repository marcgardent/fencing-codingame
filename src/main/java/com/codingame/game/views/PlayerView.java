package com.codingame.game.views;

import com.codingame.game.Player;
import com.codingame.game.core.ActionType;
import com.codingame.game.core.PlayerState;
import com.codingame.gameengine.module.entities.*;

public class PlayerView {

    private static final double BOTTOM_ANGLE = Math.PI / 8;
    private static final double MIDDLE_ANGLE = 0;
    private static final double TOP_ANGLE = -Math.PI / 8;
    private static final double BREAK_ANGLE = Math.PI / 3;
    private static final int ARM_DEFENSIVE = -30;
    private static final int ARM_OFFENSIVE = 0;

    private final GraphicEntityModule g;
    private final PlayerState playerState;
    private Group character;
    private int Color;
    private Group arm;
    private Text ko;
    private Text energy;

    PlayerView(GraphicEntityModule g, PlayerState playerState) {
        this.g = g;
        this.playerState = playerState;
    }


    public static PlayerView fromPlayer(GraphicEntityModule g, PlayerState playerState, Player player) {
        PlayerView ret = new PlayerView(g, playerState);
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

        int halfRange = StageView.getLogicToWorld(ActionType.OFFENSIVE_ATTITUDE.offensiveRange / 2);

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

        Sprite avatar = g.createSprite()
                .setX(0)
                .setY(-300)
                .setZIndex(20)
                .setImage(player.getAvatarToken())
                .setAnchor(0.5)
                .setBaseHeight(116)
                .setBaseWidth(116);

        ret.ko = g.createText("~!#?").setFillColor(Colors.WHITE).setX(-20).setY(-220).setFontFamily("Lato").setFontSize(40).setVisible(false);
        ret.energy = g.createText("+2").setFillColor(Colors.WHITE).setX(-10).setY(-260).setFontFamily("Lato")
                .setFontSize(40).setVisible(false).setStrokeThickness(3).setFontWeight(Text.FontWeight.BOLD);
        ret.arm = g.createGroup(arm, hand, blade).setX(ARM_DEFENSIVE).setY(-80).setRotation(Math.PI / 4);

        Group p = g.createGroup(body, ret.ko, ret.arm, head, grid).setScaleX(playerState.orientation);
        Group texts = g.createGroup(ret.ko, avatar);
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
            //posture & attitude
            double angle = MIDDLE_ANGLE;
            if (playerState.posture == ActionType.TOP_POSTURE) angle = TOP_ANGLE;
            else if (playerState.posture == ActionType.BOTTOM_POSTURE) angle = BOTTOM_ANGLE;
            else if (playerState.attitude == ActionType.BREAK_ATTITUDE) angle = BREAK_ANGLE;
            this.arm.setRotation(angle);
            this.arm.setX(ARM_DEFENSIVE);
            g.commitEntityState(0, this.arm);

            //offensive attitude
            if (playerState.attitude == ActionType.OFFENSIVE_ATTITUDE) {
                this.arm.setX(ARM_OFFENSIVE);
                g.commitEntityState(0.33, this.arm);
                this.arm.setX(ARM_DEFENSIVE);
                g.commitEntityState(0.66, this.arm);
                this.arm.setX(ARM_OFFENSIVE);
                g.commitEntityState(1, this.arm);
            }
        }

        this.character.setX(StageView.getLogicToWorld(playerState.position));
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
    }
}
