package com.codingame.game.views;

import com.codingame.game.models.PlayerModel;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Text;

import java.util.LinkedList;
import java.util.List;

public class StageView {

    public static final int WIDTH = 1920;
    private static final int X_RING = 50;
    private static final int WIDTH_RING = WIDTH - (X_RING * 2);

    public static final int HALF_WIDTH = 960;
    public static final int HEIGHT = 1080;
    public static final int LINE = 800;

    private GraphicEntityModule g;
    private Text refereeMessage;

    private final List<String> messages = new LinkedList<>();

    StageView(GraphicEntityModule g) {

        this.g = g;
    }

    public static int getLogicToWorld(int v) {
        return (int) ((float) v / (float) (PlayerModel.MAX_POSITION - PlayerModel.MIN_POSITION)
                * (float) StageView.WIDTH_RING) + X_RING;
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
                .setWidth(WIDTH_RING).setHeight(60)
                .setX(X_RING).setY(LINE - 30)
                .setFillColor(0xD44020).setZIndex(0);
        // lines
        g.createRectangle()
                .setWidth(WIDTH_RING).setHeight(60)
                .setX(X_RING).setY(LINE - 30).setFillAlpha(0)
                .setLineColor(Colors.WHITE).setLineWidth(5).setZIndex(0);
        //spawn line
        int spanWidth = getLogicToWorld(PlayerModel.SPAWN_POSITION_B) - getLogicToWorld(PlayerModel.SPAWN_POSITION_A);
        g.createRectangle()
                .setX(StageView.getLogicToWorld(PlayerModel.SPAWN_POSITION_A)).setY(LINE - 30).setFillAlpha(0)
                .setWidth(spanWidth).setHeight(60)
                .setLineColor(Colors.WHITE).setLineWidth(5).setZIndex(0);

//        //TODO remove DEBUG
//        for (int i = 0; i <=500; i+=20) {
//            g.createCircle().setRadius(5).setFillColor(Colors.WHITE).setX(getLogicToWorld(i)).setY(600);
//            g.createText(Integer.toString(i)).setX(getLogicToWorld(i)).setY(500).setFillColor(Colors.WHITE);
//        }

        refereeMessage = g.createText("GO!").setAnchor(0.5)
                .setFontWeight(Text.FontWeight.BOLD)
                .setX(HALF_WIDTH).setY(200).setFontSize(100).setFillColor(Colors.WHITE).setAlpha(0.5);
        //.setStrokeColor(Colors.WHITE).setStrokeThickness(20);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public void reset() {
        refereeMessage.setAlpha(0);
        g.commitEntityState(0, refereeMessage);
        if (messages.size() > 0) {
            String txt = String.join(", ", messages) + "!";
            refereeMessage.setText(txt).setAlpha(1);
            g.commitEntityState(0, refereeMessage);
            messages.clear();
        }
    }
}
