package com.codingame.game;

import com.codingame.game.core.*;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.google.inject.Inject;

import java.util.Random;

public class Referee extends AbstractReferee implements RefereObserver {
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private View view;

    private GameState state;
    private Match match;
    private Random random;

    @Override
    public void init() {
        random = new Random(gameManager.getSeed());
        match = new Match(this);
        state = match.getState();

        Player playerA = gameManager.getPlayer(0);
        Player playerB = gameManager.getPlayer(1);

        view.init(state, playerA, playerB);

        gameManager.setFrameDuration(250);
        gameManager.setMaxTurns(com.codingame.game.core.Match.MAX_TICK / 2);
    }

    private long sendInputs(Player player, TeamState me, TeamState you) {
        player.sendInputLine(me.Player.Position + " " + me.Player.Energy + " " + me.Score);
        player.sendInputLine(you.Player.Position + " " + you.Player.Energy + " " + you.Score);
        long s = System.nanoTime();
        player.execute();
        s = System.nanoTime() - s;
        return s;
    }

    @Override
    public void gameTurn(int turn) {

        if (state.restart) {
            state = match.restart();
            view.restart(state);
        } else {
            Player playerA = gameManager.getPlayer(0);
            Player playerB = gameManager.getPlayer(1);

            long timeoutA = sendInputs(playerA, state.teamA, state.teamB);
            long timeoutB = sendInputs(playerB, state.teamB, state.teamA);

            gameManager.addToGameSummary(playerA.getNicknameToken() + "=" + timeoutA);
            gameManager.addToGameSummary(playerB.getNicknameToken() + "=" + timeoutB);

            GameInput A = playerTurn(playerA, state.teamA, state.teamB);
            GameInput B = playerTurn(playerB, state.teamB, state.teamA);

            if (A == null && B == null) {
                playerA.setScore(-1);
                playerB.setScore(-1);
                endGame();
            } else if (A == null) {
                playerA.setScore(-1);
                playerB.setScore(20);
                endGame();
            } else if (B == null) {
                playerA.setScore(20);
                playerB.setScore(-1);
                endGame();
            } else {
                state = match.tick(A, B);
                view.tick(state);
                gameManager.addToGameSummary("turn=" + turn);
                gameManager.addToGameSummary("Tick=" + state.tick);
            }
        }
    }

    private GameInput playerTurn(Player player, TeamState me, TeamState you) {
        try {
            final GameInput action = player.getAction();
            gameManager.addToGameSummary(String.format("Player %s played (move=%d action=%d)", player.getNicknameToken(), action.Move, action.Action));
            return action;
        } catch (NumberFormatException e) {
            player.deactivate("Wrong output!");
        } catch (TimeoutException e) {
            gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + " timeout!"));
            player.deactivate(player.getNicknameToken() + " timeout!");
        } catch (InvalidAction e) {
            player.deactivate(e.getMessage());
        }
        return null;
    }

    private void endGame() {
        gameManager.endGame();
        // TODO refresh view
    }

    @Override
    public void playerIsKo(PlayerState player) {
        view.playerKo(player);
    }

    @Override
    public void scoreAB() {
        view.score(state.teamA);
        view.score(state.teamB);
        System.out.println(String.format("ScoreAB"));
    }

    @Override
    public void score(TeamState team) {
        view.score(team);
        System.out.println(String.format("Score"));
    }

    @Override
    public void outside(PlayerState player) {
        System.out.println(String.format("Outside"));
    }

    @Override
    public void collide() {
        System.out.println(String.format("Collide"));
    }

    @Override
    public void winTheGame(TeamState winner, TeamState losser) {
        Player winnerP = gameManager.getPlayer(state.teamA == winner ? 0 : 1);
        Player losserP = gameManager.getPlayer(state.teamA == losser ? 0 : 1);

        winnerP.setScore(winner.Score);
        losserP.setScore(losser.Score);

        gameManager.addToGameSummary(GameManager.formatSuccessMessage(winnerP.getNicknameToken() + " won!"));
        endGame();
    }

    @Override
    public void draw() {
        gameManager.addToGameSummary("Draw!");
        endGame();
    }

    @Override
    public void move(PlayerState player, int from, int to) {
        view.move(player, from, to);
    }

    @Override
    public void energyChanged(PlayerState player, int delta) {
        view.energyChanged(player, delta);
    }

    @Override
    public void actionResolved(PlayerState player, byte aResolved) {
        view.actionResolved(player, aResolved);
    }
}