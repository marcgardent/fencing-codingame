package com.codingame.game.views;

import com.codingame.game.core.PlayerState;
import com.codingame.gameengine.module.entities.GraphicEntityModule;

public class StageView {

    public static final int WIDTH = 1920;
    public static final int HALF_WIDTH = 960;
    public static final int HEIGHT = 1080;
    public static final int LINE = 800;

    private GraphicEntityModule g;

    StageView(GraphicEntityModule g) {

        this.g = g;
    }

    public static int getLogicToWorld(int v) {
        return (int) ((float) v / (float) (PlayerState.MAX_POSITION - PlayerState.MIN_POSITION) * (float) StageView.WIDTH);
    }

    public void init() {
        g.createRectangle()
                .setWidth(WIDTH).setHeight(HEIGHT)
                .setX(0).setY(0)
                .setFillColor(Colors.BLACK).setZIndex(0);

        //blue floor
        g.createRectangle()
                .setWidth(WIDTH).setHeight(120)
                .setX(0).setY(LINE - 60)
                .setFillColor(0x2061D4).setZIndex(0);

        //red floor
        g.createRectangle()
                .setWidth(WIDTH - 100).setHeight(60)
                .setX(50).setY(LINE - 30)
                .setFillColor(0xD44020).setZIndex(0);
        // lines
        g.createRectangle()
                .setWidth(WIDTH - 100).setHeight(60)
                .setX(50).setY(LINE - 30).setFillAlpha(0)
                .setLineColor(Colors.WHITE).setLineWidth(5).setZIndex(0);
        //spawn line
        g.createRectangle()
                .setWidth(StageView.getLogicToWorld(PlayerState.SPAWN_POSITION_B - PlayerState.SPAWN_POSITION_A)).setHeight(60)
                .setX(StageView.getLogicToWorld(PlayerState.SPAWN_POSITION_A)).setY(LINE - 30).setFillAlpha(0)
                .setLineColor(Colors.WHITE).setLineWidth(5).setZIndex(0);
    }
}
