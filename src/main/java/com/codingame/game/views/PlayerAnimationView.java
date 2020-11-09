package com.codingame.game.views;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import com.google.inject.Inject;

public class PlayerAnimationView {
    private static final String BREAK1 = "break-1";
    private static final String BREAK2 = "break-2";
    private static final String LUNGE1 = "lunge-1";
    private static final String LUNGE2FAILED = "lunge-2-failed";
    private static final String LUNGE2SUCCEEDED = "lunge-2-succeeded";
    private static final String PARRY1 = "parry-1";
    private static final String PARRY2FAILED = "parry-2-failed";
    private static final String PARRY2SUCCEEDED = "parry-2-succeeded";
    private static final String WALK1 = "walk-2";
    private static final String WALK2 = "walk-retreat-2";
    private static final String RETREAT1 = "retreat-1";
    private static final String RETREAT2 = "walk-retreat-2";
    @Inject
    private GraphicEntityModule g;
    private Group group;
    private Sprite currentSprite;
    private char suffix;

    public void init(char teamChar) {
        suffix = teamChar;
        currentSprite = g.createSprite().setImage(fullname(WALK2)).setBaseHeight(400).setBaseWidth(400);
        group = g.createGroup(currentSprite).setX(-180).setY(-400).setZIndex(20);
        //currentSprite = g.createSprite().setImage(fullname(WALK2)).setBaseHeight(128).setBaseWidth(128);
        //group = g.createGroup(currentSprite).setX(-60).setY(-128).setZIndex(20);
    }

    private String fullname(String name) {
        return name + suffix;
    }

    public Group getGroup() {
        return group;
    }

    private void fadeTo(String n1, String n2) {
        g.commitEntityState(0, currentSprite.setImage(fullname(n1)));
        g.commitEntityState(0.5, currentSprite.setImage(fullname(n2)));
    }

    private void fadeToDouble(String n1, String n2) {
        g.commitEntityState(0, currentSprite.setImage(fullname(n1)));
        g.commitEntityState(0.25, currentSprite.setImage(fullname(n2)));
        g.commitEntityState(0.5, currentSprite.setImage(fullname(n1)));
        g.commitEntityState(0.75, currentSprite.setImage(fullname(n2)));
    }

    public void toRespawn() {
        g.commitEntityState(0, currentSprite.setImage(fullname(WALK2)));
    }

    public void toWalk() {
        fadeTo(WALK1, WALK2);
    }

    public void toLunge(boolean succeeded) {
        fadeTo(LUNGE1, succeeded ? LUNGE2SUCCEEDED : LUNGE2FAILED);
    }

    public void toParry(boolean succeeded) {
        fadeTo(PARRY1, succeeded ? PARRY2SUCCEEDED : PARRY2FAILED);
    }

    public void toRetreat() {
        fadeTo(RETREAT1, RETREAT2);
    }

    public void toBreak() {
        fadeTo(BREAK1, BREAK2);
    }

    public void toDoubleWalk() {
        fadeToDouble(WALK1, WALK2);
    }

    public void toDoubleRetreat() {
        fadeToDouble(RETREAT1, RETREAT2);
    }
}

