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
        State = Match.InitGame();
        view.Init(State);

        gameManager.setFrameDuration(100);
        gameManager.setMaxTurns(com.codingame.game.core.Match.MAX_TICK);
    }

    private void sendInputs(Player player, TeamState me, TeamState you) {
        player.sendInputLine(me.Player.Position + " " + me.Player.Energy + " " + me.Score + " ");
        player.sendInputLine(you.Player.Position + " " + you.Player.Energy + " " + you.Score + " ");
        player.execute();
    }

    @Override
    public void gameTurn(int turn) {

        Player playerA = gameManager.getPlayer(0);
        Player playerB = gameManager.getPlayer(1);

        sendInputs(playerA, State.TeamA, State.TeamB);
        sendInputs(playerB, State.TeamB, State.TeamA);

        GameInput A = PlayerTurn(playerA, State.TeamA, State.TeamB);
        GameInput B = PlayerTurn(playerB, State.TeamB, State.TeamA);

        if (A == null && B == null) {
            playerA.setScore(-1);
            playerB.setScore(-1);
            endGame();
        } else if (A == null) {
            playerA.setScore(-1);
            playerB.setScore(10);
            endGame();
        } else if (B == null) {
            playerA.setScore(10);
            playerB.setScore(-1);
            endGame();
        } else {
            State = Match.Tick(A, B);
            view.Init(State);
        }
    }

    private GameInput PlayerTurn(Player player, TeamState me, TeamState you) {
        try {
            player.execute();
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

    }

    @Override
    public void ScoreAB() {

    }

    @Override
    public void Score(TeamState team) {

    }

    @Override
    public void Outside(PlayerState player) {

    }

    @Override
    public void Collide() {

    }

    @Override
    public void WinTheGame(TeamState team) {
        Player winner = gameManager.getPlayer(State.TeamA == team ? 0 : 1);

        gameManager.addToGameSummary(GameManager.formatSuccessMessage(winner.getNicknameToken() + " won!"));
        winner.setScore(10);
        endGame();
    }

    @Override
    public void Draw() {
        gameManager.addToGameSummary("Draw!");
        endGame();
    }
}