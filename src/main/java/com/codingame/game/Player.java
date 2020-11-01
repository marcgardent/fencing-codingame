package com.codingame.game;

import com.codingame.game.core.ActionType;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

public class Player extends AbstractMultiplayerPlayer {

    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public ActionType getAction(int leagueId) throws TimeoutException, NumberFormatException, InvalidAction {
        String line = getOutputs().get(0);
        String[] output = line.split(" ");
        if (output.length != 1) throw new InvalidAction("Excepted: '<action>' found: '" + line + "'");
        int value = Integer.parseInt(output[0]);
        ActionType ret = ActionType.fromInteger(value);

        if (ret == null) throw new InvalidAction("Excepted: '<action>' found: '" + line + "'");
        //else if (ret.league > leagueId) throw new InvalidAction("Illegal action for your current league level " + (leagueId + 1) + "), available in the league level " + ret.league);
        return ret;
    }
}