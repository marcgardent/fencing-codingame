package com.codingame.game.core;

public class GameInput {

    public final static byte IDLE = 0;
    public final static byte FORWARD = 1;
    public final static byte BACKWARD = 2;

    public final static byte BASIC_ATTACK = 3;
    public final static byte NORMAL_ATTACK = 4;
    public final static byte COMPLEX_ATTACK = 5;

    public byte move;
    public byte action;

    public static String getLabel(byte flag) {
        if (flag == IDLE) return "Idle";
        if (flag == FORWARD) return "Forward";
        if (flag == BACKWARD) return "Backward";
        if (flag == BASIC_ATTACK) return "BasicAttack";
        if (flag == NORMAL_ATTACK) return "NormalAttack";
        if (flag == COMPLEX_ATTACK) return "ComplexAttack";
        return Integer.toString(flag);
    }

    public final static String excepted = "[Move:0,1,2] [Action:0,3,4,5]";


    public boolean isValid() {
        return (move == IDLE || move == FORWARD || move == BACKWARD) && (action == IDLE || action == BASIC_ATTACK || action == NORMAL_ATTACK || action == COMPLEX_ATTACK);
    }
}

