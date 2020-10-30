package com.codingame.game;

import com.codingame.game.core.GameInput;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

public class Player extends AbstractMultiplayerPlayer {

    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public GameInput getAction() throws TimeoutException, NumberFormatException, InvalidAction {
        String line = getOutputs().get(0);
        String[] output = line.split(" ");
        if (output.length != 2)
            throw new InvalidAction("Excepted: '" + GameInput.excepted + "' found: '" + line + "'");
        GameInput ret = new GameInput();
        ret.Move = Byte.parseByte(output[0]);
        ret.Action = Byte.parseByte(output[1]);

        if (!ret.isValid())
            throw new InvalidAction("Excepted: '" + GameInput.excepted + "' found: '" + line + "'");
        return ret;
    }
}
