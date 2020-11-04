package com.codingame.game.views;

import com.codingame.game.Player;
import com.codingame.game.models.ActionType;
import com.codingame.game.models.PlayerModel;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;

public class PlayerView {

    private static final double BOTTOM_ANGLE = Math.PI / 8;
    private static final double MIDDLE_ANGLE = -Math.PI / 32;
    private static final double TOP_ANGLE = -Math.PI / 8;
    private static final double BREAK_ANGLE = Math.PI / 3;

    private static final int ARM_DEFENSIVE_X = -50;
    private static final int ARM_DEFENSIVE_Y = -60;

    private static final int ARM_OFFENSIVE_X = 0;
    private static final int ARM_OFFENSIVE_Y = -80;
    private static final int FLAG_SIZE = 64;

    private final GraphicEntityModule g;
    private final PlayerModel playerModel;
    private Group character;
    private int Color;
    private Group arm;
    private Sprite attackFlag;
    private Sprite knockoutFlag;
    private Sprite energyFlag;
    private Sprite defenceFlag;


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

        int halfRange = StageView.getLogicToWorld(ActionType.LUNGE.distance / 2);

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

        ret.attackFlag = g.createSprite().setImage("attack.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        ret.defenceFlag = g.createSprite().setImage("defence.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        ret.energyFlag = g.createSprite().setImage("energy.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        ret.knockoutFlag = g.createSprite().setImage("knockout.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        Group flags = g.createGroup(ret.attackFlag, ret.defenceFlag, ret.energyFlag, ret.knockoutFlag)
                .setX(-25).setY(-220);

        ret.arm = g.createGroup(arm, hand, blade).setX(ARM_DEFENSIVE_X).setY(ARM_DEFENSIVE_Y).setRotation(Math.PI / 4);

        Group p = g.createGroup(body, flags, head, grid, ret.arm, flags);
        if (playerModel.orientation < 0) {
            p.setScaleX(playerModel.orientation).setX(50);
        }

        ret.character = g.createGroup(p).setY((int) (StageView.LINE));
        ret.Color = player.getColorToken();

        return ret;
    }

    public void restartPlayer() {
        draw();
    }

    public void draw() {
        //Reset
        attackFlag.setVisible(false);
        defenceFlag.setVisible(false);
        energyFlag.setVisible(false);
        knockoutFlag.setVisible(false);
        g.commitEntityState(0, attackFlag, defenceFlag, energyFlag, knockoutFlag);
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
        this.knockoutFlag.setVisible(true);
        g.commitEntityState(0.00001, knockoutFlag);
    }

    public void energyChanged(int delta) {
        if (delta > 0) {
            this.energyFlag.setVisible(true);
            g.commitEntityState(0.00001, energyFlag);
        }
    }

    public void hit(boolean succeeded) {
        this.attackFlag.setVisible(true).setAlpha(succeeded ? 1 : 0.5);
        g.commitEntityState(0.00001, attackFlag);

        this.arm.setX(ARM_OFFENSIVE_X).setY(ARM_OFFENSIVE_Y);
        g.commitEntityState(1, this.arm);
    }

    public void defended(boolean succeeded) {
        this.defenceFlag.setVisible(true).setAlpha(succeeded ? 1 : 0.5);
        ;
        g.commitEntityState(0.00001, defenceFlag);
    }
}
