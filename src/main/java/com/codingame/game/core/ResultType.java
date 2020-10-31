package com.codingame.game.core;

public enum ResultType {

    CONTINUE(0),
    TOUCH(1),
    DOUBLE_TOUCH(2),
    TOUCHED(3);


    public final int code;

    ResultType(int code) {
        this.code = code;
    }
}
