package com.codingame.game.views;

import com.codingame.game.Player;
import com.codingame.game.models.ActionType;
import com.codingame.game.models.PlayerModel;
import com.codingame.gameengine.module.entities.*;
import com.codingame.gameengine.module.toggle.ToggleModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;

public class PlayerView {

    private static final double PARRY_ANGLE = -Math.PI / 3;
    @Inject
    ToggleModule toggleModule;
    @Inject
    TooltipModule tooltips;

    private static final double BOTTOM_ANGLE = Math.PI / 8;
    private static final double MIDDLE_ANGLE = -Math.PI / 32;
    private static final double TOP_ANGLE = -Math.PI / 8;


    private static final int ARM_DEFENSIVE_X = -50;
    private static final int ARM_DEFENSIVE_Y = -60;
    @Inject
    private GraphicEntityModule g;
    private static final int ARM_WIDTH = 100;

    private static final int ARM_OFFENSIVE_X = 0;
    private static final int ARM_OFFENSIVE_Y = -80;
    private static final int FLAG_SIZE = 64;
    private PlayerModel playerModel;
    private Group character;
    private int color;
    private Group arm;
    private Sprite attackFlag;
    private Sprite knockoutFlag;
    private Sprite energyFlag;
    private Sprite defenceFlag;
    private Group bladeGroup;


    public PlayerView init(PlayerModel playerModel, Player player) {
        this.playerModel = playerModel;

        color = player.getColorToken();

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

        int range = StageView.getLogicToWorld(ActionType.LUNGE.distance);

        Rectangle arm = g.createRectangle()
                .setWidth(ARM_WIDTH).setHeight(20)
                .setX(0).setY(-10)
                .setFillColor(Colors.WHITE).setZIndex(10);


        Rectangle hand = g.createRectangle()
                .setWidth(20).setHeight(20)
                .setX(-10).setY(-10)
                .setLineWidth(4).setLineColor(Colors.WHITE)
                .setFillColor(player.getColorToken()).setZIndex(11);

        Rectangle blade = g.createRectangle()
                .setWidth(range - ARM_WIDTH).setHeight(10)
                .setX(10).setY(-5)
                .setFillColor(player.getColorToken()).setZIndex(11);
        Circle c = g.createCircle().setRadius(5).setFillColor(Colors.GREEN);

        bladeGroup = g.createGroup(c, hand, blade).setX(ARM_WIDTH - 10).setY(0).setZIndex(11);

        attackFlag = g.createSprite().setImage("attack.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        defenceFlag = g.createSprite().setImage("defence.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        energyFlag = g.createSprite().setImage("energy.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        knockoutFlag = g.createSprite().setImage("knockout.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        Circle debug = g.createCircle().setX(range).setX(range)
                .setY(-200).setZIndex(20).setFillColor(color).setRadius(5);

        toggleModule.displayOnToggleState(debug, "debugInfo", true);

        Group flags = g.createGroup(attackFlag, defenceFlag, energyFlag, knockoutFlag)
                .setX(-25).setY(-220);

        this.arm = g.createGroup(arm, bladeGroup).setX(ARM_DEFENSIVE_X).setY(ARM_DEFENSIVE_Y).setRotation(Math.PI / 4);

        Group p = g.createGroup(body, flags, head, grid, this.arm, flags, debug);
        if (playerModel.orientation < 0) {
            p.setScaleX(playerModel.orientation);
        }

        character = g.createGroup(p).setY((int) (StageView.LINE));


        return this;
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
            if (playerModel.posture == ActionType.RIGHT_POSTURE) angle = TOP_ANGLE;
            else if (playerModel.posture == ActionType.LEFT_POSTURE) angle = BOTTOM_ANGLE;
            this.arm.setRotation(angle);
            this.arm.setX(ARM_DEFENSIVE_X).setY(ARM_DEFENSIVE_Y);
            g.commitEntityState(0, this.arm);
            bladeGroup.setRotation(0);
            g.commitEntityState(0, bladeGroup);
        }

        this.character.setX(StageView.getLogicToWorld(playerModel.position));
        g.commitEntityState(1, this.character);

        StringBuilder sb = new StringBuilder();
        sb.append("position=").append(playerModel.position)
                .append("[").append(PlayerModel.MAX_POSITION - playerModel.position).append("]")
                .append("\n");
        sb.append("energy=").append(playerModel.energy).append("\n");
        if (playerModel.energyMax > PlayerModel.ENERGY_MAX_SKILL)
            sb.append("energyMax=").append(playerModel.energyMax).append("\n");
        if (playerModel.breakSkill > 0) sb.append("breakSkill=").append(playerModel.breakSkill).append("\n");
        if (playerModel.lungeDistanceSkill > 0)
            sb.append("lungeDistanceSkill=").append(playerModel.lungeDistanceSkill).append("\n");
        if (playerModel.parryDistanceSkill > 0)
            sb.append("parryDistanceSkill=").append(playerModel.parryDistanceSkill).append("\n");
        if (playerModel.walkSkill > 0) sb.append("walkSkill=").append(playerModel.walkSkill).append("\n");
        if (playerModel.doubleWalkSkill > 0)
            sb.append("doubleWalkSkill=").append(playerModel.doubleWalkSkill).append("\n");
        if (playerModel.retreatSkill > 0) sb.append("retreatSkill=").append(playerModel.retreatSkill).append("\n");
        if (playerModel.doubleRetreatSkill > 0)
            sb.append("doubleRetreatSkill=").append(playerModel.doubleRetreatSkill).append("\n");
        tooltips.setTooltipText(character, sb.toString());
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
        g.commitEntityState(0.00001, defenceFlag);

        bladeGroup.setRotation(PARRY_ANGLE);
        arm.setRotation(TOP_ANGLE);
        g.commitEntityState(0.00001, arm);
        g.commitEntityState(0.00001, bladeGroup);
    }
}