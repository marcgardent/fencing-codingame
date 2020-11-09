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

    @Inject
    PlayerAnimationView animation;

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
    private Sprite attackFlag;
    private Sprite knockoutFlag;
    private Sprite energyFlag;
    private Sprite defenceFlag;


    public PlayerView init(PlayerModel playerModel, Player player) {
        this.playerModel = playerModel;
        animation.init('A');
        color = player.getColorToken();
        int lungeDistance = StageView.getLogicToWorld(ActionType.LUNGE.distance);
        int parryDistance = StageView.getLogicToWorld(ActionType.PARRY.distance);

        attackFlag = g.createSprite().setImage("attack.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        defenceFlag = g.createSprite().setImage("defence.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        energyFlag = g.createSprite().setImage("energy.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        knockoutFlag = g.createSprite().setImage("knockout.png").setVisible(false)
                .setBaseHeight(FLAG_SIZE).setBaseWidth(FLAG_SIZE).setAnchor(0.5);

        //TODO ADD SKILL
        Rectangle debug = g.createRectangle()
                .setX(parryDistance).setY(-220).setWidth(-parryDistance + lungeDistance).setHeight(10).setAlpha(0.5)
                .setZIndex(20).setFillColor(color);
        Line debugP0 = g.createLine().setX(0).setY(0).setX2(0).setY2(-220).setLineWidth(2).setLineColor(color);

        toggleModule.displayOnToggleState(debug, "debugInfo", true);
        toggleModule.displayOnToggleState(debugP0, "debugInfo", true);

        Group flags = g.createGroup(attackFlag, defenceFlag, energyFlag, knockoutFlag)
                .setX(-25).setY(-220);

        Group p = g.createGroup(this.animation.getGroup(), flags, flags, debug, debugP0);
        if (playerModel.orientation < 0) {
            p.setScaleX(playerModel.orientation);
        }

        character = g.createGroup(p).setY((int) (StageView.LINE));
        reset();
        return this;
    }

    private void reset() {
        animation.toRespawn();
        character.setX(StageView.getLogicToWorld(playerModel.position));
        g.commitEntityState(1, character);
    }

    public void restartPlayer() {
        reset();
        draw();
    }

    public void draw() {
        //Reset
        attackFlag.setVisible(false);
        defenceFlag.setVisible(false);
        energyFlag.setVisible(false);
        knockoutFlag.setVisible(false);
        g.commitEntityState(0, attackFlag, defenceFlag, energyFlag, knockoutFlag);

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
            animation.toBreak();
        }
    }

    public void hit(boolean succeeded) {
        g.commitEntityState(0.00001, attackFlag.setVisible(true).setAlpha(succeeded ? 1 : 0.5));
        animation.toLunge(succeeded);
    }

    public void defended(boolean succeeded) {
        g.commitEntityState(0.00001, defenceFlag.setVisible(true).setAlpha(succeeded ? 1 : 0.5));
        animation.toParry(succeeded);
    }

    public void move(int from, int to) {
        if (to > from) {
            animation.toWalk();
        } else {
            animation.toRetreat();
        }
        g.commitEntityState(1, character.setX(StageView.getLogicToWorld(to)));
    }
}