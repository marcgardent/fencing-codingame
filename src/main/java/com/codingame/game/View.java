package com.codingame.game;

import com.codingame.game.core.GameState;
import com.codingame.game.core.PlayerState;
import com.codingame.game.core.TeamState;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

public class View {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    @Inject
    private GraphicEntityModule graphicEntityModule;
    private PlayerView AView;
    private PlayerView BView;

    public void Init(GameState state) {
        graphicEntityModule.createRectangle()
                .setWidth(WIDTH).setHeight(HEIGHT)
                .setX(0).setY(0)
                .setFillColor(0x000000).setZIndex(0);

        AView = createPlayer(state.TeamA.Player, 0x00FF00);
        BView = createPlayer(state.TeamB.Player, 0x0000FF);
        Tick(state);
    }

    public void Tick(GameState state) {
        drawPlayer(AView, state.TeamA);
        drawPlayer(BView, state.TeamB);

    }

    private void drawPlayer(PlayerView v, TeamState team) {
        graphicEntityModule.commitEntityState(0, v.Character);
        v.Character.setX((int) ((float) team.Player.Position / (float) PlayerState.MAX_POSITION * (float) WIDTH)).setY((int) (HEIGHT * 0.6));
        v.Label.setText("S" + team.Score + " E" + team.Player.Energy);
        graphicEntityModule.commitEntityState(1, v.Character);
    }

    private PlayerView createPlayer(PlayerState player, int color) {
        PlayerView ret = new PlayerView();

        Rectangle rect = graphicEntityModule.createRectangle()
                .setWidth(100).setHeight(100)
                .setX(-50).setY(-100)
                .setFillColor(color).setZIndex(10);
        ret.Label = graphicEntityModule.createText("").setX(0).setY(-200).setFillColor(color).setZIndex(20);
        ret.Character = graphicEntityModule.createGroup(rect, ret.Label);
        return ret;
    }

    private class PlayerView {
        public Group Character;
        public Text Label;
    }
}