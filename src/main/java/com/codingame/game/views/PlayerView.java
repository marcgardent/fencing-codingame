package com.codingame.game.views;

import com.codingame.game.Player;
import com.codingame.game.models.PlayerModel;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Line;
import com.codingame.gameengine.module.entities.Rectangle;
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


    public PlayerView init(PlayerModel playerModel, Player player, char teamId) {
        this.playerModel = playerModel;
        animation.init(teamId);
        color = player.getColorToken();
        int lungeDistance = StageView.getDistanceLogicToWorld(playerModel.getLungeDistance());
        int parryDistance = StageView.getDistanceLogicToWorld(playerModel.getParryDistance());


        Rectangle debug = g.createRectangle()
                .setX(parryDistance).setY(-264).setWidth((-parryDistance) + lungeDistance).setHeight(260).setFillAlpha(0.1)
                .setLineColor(color).setLineAlpha(1).setLineWidth(1)
                .setZIndex(5).setFillColor(color);

        Line debugP0 = g.createLine().setX(0).setY(10).setX2(0).setY2(-266).setLineWidth(1).setLineColor(color);

        toggleModule.displayOnToggleState(debug, "debugInfo", true);
        toggleModule.displayOnToggleState(debugP0, "debugInfo", true);

        Group p = g.createGroup(debug, debugP0);
        if (playerModel.orientation < 0) {
            p.setScaleX(playerModel.orientation);
        }

        character = g.createGroup(this.animation.getGroup(), p).setY((int) (StageView.LINE));
        reset();
        return this;
    }

    private void reset() {
        animation.toRespawn();
        character.setX(StageView.getPositionLogicToWorld(playerModel.position));
        g.commitEntityState(1, character);
    }

    public void restartPlayer() {
        reset();
        draw();
    }

    public PlayerView setSpritePadding(int x) {
        this.animation.getGroup().setX(x);
        return this;
    }

    public void draw() {


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
    }

    public void energyChanged(int delta) {
        if (delta > 0) {
            animation.toBreak();
        }
    }

    public void hit(boolean succeeded) {
        animation.toLunge(succeeded);
    }

    public void defended(boolean succeeded) {
        animation.toParry(succeeded);
    }

    public void move(int from, int to) {
        if (to > from) {
            animation.toWalk();
        } else {
            animation.toRetreat();
        }
        g.commitEntityState(1, character.setX(StageView.getPositionLogicToWorld(to)));
    }
}