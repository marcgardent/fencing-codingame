package com.codingame.game.core;


public class PlayerState {

    public static final byte LEFT_ORIENTATION = 1;
    public static final byte RIGHT_ORIENTATION = -1;

    public static final int ENERGY_MAX = 20;
    public static final byte ENERGY_START = 20;
    public static final int SPAWN_POSITION_A = 200;
    public static final int SPAWN_POSITION_B = 300;
    public static final int STEP = 50;
    public static final int RANGE = 30;
    public static final int MIN_POSITION = 0;
    public static final int MAX_POSITION = 500;

    public int position;
    public byte energy = ENERGY_START;
    public byte energyMax = ENERGY_MAX;
    public byte orientation;
    public int step = STEP;
    public int range = RANGE;

}
