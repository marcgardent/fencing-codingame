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

    private GameState State;
    private Match Match;
    private Random random;

    @Override
    public void init() {
        random = new Random(gameManager.getSeed());
        Match = new Match(this);
        State = Match.getState();

        Player playerA = gameManager.getPlayer(0);
        Player playerB = gameManager.getPlayer(1);

        view.Init(State, playerA, playerB);

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

        if (State.Restart) {
            State = Match.Restart();
            view.Restart(State);
        } else {
            Player playerA = gameManager.getPlayer(0);
            Player playerB = gameManager.getPlayer(1);

            long timeoutA = sendInputs(playerA, State.TeamA, State.TeamB);
            long timeoutB = sendInputs(playerB, State.TeamB, State.TeamA);

            gameManager.addToGameSummary(playerA.getNicknameToken() + "=" + timeoutA);
            gameManager.addToGameSummary(playerB.getNicknameToken() + "=" + timeoutB);

            GameInput A = PlayerTurn(playerA, State.TeamA, State.TeamB);
            GameInput B = PlayerTurn(playerB, State.TeamB, State.TeamA);

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
                State = Match.Tick(A, B);
                view.Tick(State);
                gameManager.addToGameSummary("turn=" + turn);
                gameManager.addToGameSummary("Tick=" + State.Tick);
            }
        }
    }

    private GameInput PlayerTurn(Player player, TeamState me, TeamState you) {
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
    public void PlayerKao(PlayerState player) {
        view.PlayerKao(player);
    }

    @Override
    public void ScoreAB() {
        view.Score(State.TeamA);
        view.Score(State.TeamB);
        System.out.println(String.format("ScoreAB"));
    }

    @Override
    public void Score(TeamState team) {
        view.Score(team);
        System.out.println(String.format("Score"));
    }

    @Override
    public void Outside(PlayerState player) {
        System.out.println(String.format("Outside"));
    }

    @Override
    public void Collide() {
        System.out.println(String.format("Collide"));
    }

    @Override
    public void WinTheGame(TeamState winner, TeamState losser) {
        Player winnerP = gameManager.getPlayer(State.TeamA == winner ? 0 : 1);
        Player losserP = gameManager.getPlayer(State.TeamA == losser ? 0 : 1);

        winnerP.setScore(winner.Score);
        losserP.setScore(losser.Score);

        gameManager.addToGameSummary(GameManager.formatSuccessMessage(winnerP.getNicknameToken() + " won!"));
        endGame();
    }

    @Override
    public void Draw() {
        gameManager.addToGameSummary("Draw!");
        endGame();
    }

    @Override
    public void Move(PlayerState player, int from, int to) {
        view.Move(player, from, to);
    }

    @Override
    public void EnergyChanged(PlayerState player, int delta) {
        view.EnergyChanged(player, delta);
    }

    @Override
    public void ActionResolved(PlayerState player, byte aResolved) {
        view.ActionResolved(player, aResolved);
    }
}