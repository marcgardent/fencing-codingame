package com.codingame.game;

import com.codingame.game.models.ActionType;
import com.codingame.game.models.TeamModel;
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
        else if (ret.league > leagueId)
            throw new InvalidAction("Illegal action for your current league level " + (leagueId + 1) + "), available in the league level " + ret.league);
        return ret;
    }

    public long sendInputs(TeamModel me, TeamModel you) {

        this.sendInputLine(String.format("%d %d %d %d",
                me.player.getRelativePosition(),
                me.player.energy, me.score, me.player.posture.code));

        this.sendInputLine(String.format("%d %d %d %d",
                you.player.getRelativeOpponentPosition(),
                you.player.energy, you.score, you.player.posture.code));

        long s = System.nanoTime();
        this.execute();
        s = System.nanoTime() - s;
        return s;
    }
}