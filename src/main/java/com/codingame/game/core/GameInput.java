package com.codingame.game.core;

public class GameInput {

    public final static byte IDLE = 0;
    public final static byte FORWARD = 1;
    public final static byte BACKWARD = 2;

    public final static byte BASIC_ATTACK = 3;
    public final static byte NORMAL_ATTACK = 4;
    public final static byte COMPLEX_ATTACK = 5;

    public byte Move;
    public byte Action;

    public static String GetExcepted() {
        return "<Move:0,1,2> <Action:0,3,4,5>";
    }

    public boolean IsValid() {
        return (Move == IDLE || Move == FORWARD || Move == BACKWARD) && (Action == IDLE || Action == BASIC_ATTACK || Action == NORMAL_ATTACK || Action == COMPLEX_ATTACK);
    }
}
