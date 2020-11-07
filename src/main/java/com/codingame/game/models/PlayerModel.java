package com.codingame.game.models;

import java.util.ArrayList;

public class PlayerModel {

    public static final int LEFT_ORIENTATION = 1;
    public static final int RIGHT_ORIENTATION = -1;

    //Position
    public static final int SPAWN_POSITION_A = 200;
    public static final int SPAWN_POSITION_B = 300;
    public static final int MIN_POSITION = 0;
    public static final int MAX_POSITION = 500;

    //Energy
    public static final int ENERGY_MAX_SKILL = 20;
    public static final int ENERGY_START = 20;
    public static final int DRUG_MAX = 7;

    public int position;
    public int orientation;
    public int energy = ENERGY_START;

    public int energyMax = ENERGY_MAX_SKILL;
    public int breakSkill = 0;

    public int doubleWalkSkill = 0;
    public int doubleRetreatSkill = 0;

    public int walkSkill = 0;
    public int retreatSkill = 0;

    public int lungeDistanceSkill = 0;
    public int parryDistanceSkill = 0;

    public ActionType posture;
    public boolean touched = false;

    public ArrayList<ActionType> drugs = new ArrayList<ActionType>();

    public int getDrugCount() {
        return drugs.size();
    }

    ;

    public int getRelativePosition() {
        if (orientation < 0) return MAX_POSITION - position;
        else return position;
    }

    public int getRelativeOpponentPosition() {
        if (orientation > 0) return MAX_POSITION - position;
        else return position;
    }

    public int getMove(ActionType move) {
        int gain = 0;
        if (move == ActionType.RETREAT) return move.move + retreatSkill + gain;
        if (move == ActionType.WALK) return move.move + walkSkill + gain;
        if (move == ActionType.DOUBLE_RETREAT) return move.move + doubleRetreatSkill + gain;
        if (move == ActionType.DOUBLE_WALK) return move.move + doubleWalkSkill + gain;
        else return move.move + gain;
    }

    public void reset() {
        posture = ActionType.MIDDLE_POSTURE;
        touched = false;
    }
}