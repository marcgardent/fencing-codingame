package com.codingame.game;

import com.codingame.game.models.*;
import com.codingame.game.views.MainView;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.google.inject.Inject;

import java.util.Random;

public class Referee extends AbstractReferee implements MatchObserver {
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private MainView view;

    private GameModel state;
    private MatchModel match;
    private Random random;
    private int leagueId;

    @Override
    public void init() {
        random = new Random(gameManager.getSeed());
        leagueId = gameManager.getLeagueLevel() - 1;
        match = new MatchModel(this);
        state = match.getState();


        Player playerA = gameManager.getPlayer(0);
        Player playerB = gameManager.getPlayer(1);

        view.init(state, playerA, playerB);

        gameManager.setFrameDuration(200);
        gameManager.setMaxTurns(MatchModel.MAX_TICK / 2);
    }

    @Override
    public void gameTurn(int turn) {

        if (state.restart) {
            state = match.restart();
            view.restart();
        } else {
            Player playerA = gameManager.getPlayer(0);
            Player playerB = gameManager.getPlayer(1);

            long timeoutA = playerA.sendInputs(state.teamA, state.teamB);
            long timeoutB = playerB.sendInputs(state.teamB, state.teamA);

//            gameManager.addToGameSummary(playerA.getNicknameToken() + "=" + timeoutA);
//            gameManager.addToGameSummary(playerB.getNicknameToken() + "=" + timeoutB);

            ActionType A = playerTurn(playerA, state.teamA, state.teamB);
            ActionType B = playerTurn(playerB, state.teamB, state.teamA);

            if (A == null && B == null) {
                playerA.setScore(-20);
                playerB.setScore(-20);
                endGame();
            } else if (A == null) {
                playerA.setScore(-20);
                playerB.setScore(20);
                endGame();
            } else if (B == null) {
                playerA.setScore(20);
                playerB.setScore(-20);
                endGame();
            } else {
                state = match.tick(A, B);
                view.tick();

                String msgA = String.join(", ", state.teamA.messages);
                String msgB = String.join(", ", state.teamB.messages);

//                gameManager.addToGameSummary(String.format("%s:%n", playerA.getNicknameToken()));
//                System.out.printf("%s:%n", playerA.getNicknameToken());
//                for (String msg : state.teamA.messages) {
//                    gameManager.addToGameSummary(String.format("%s%n", msg));
//                    System.out.printf("%s%n", msg);
//                }
//
//                gameManager.addToGameSummary(String.format("%s:%n", playerB.getNicknameToken()));
//                System.out.printf("%s:%n", playerB.getNicknameToken());
//                for (String msg : state.teamB.messages) {
//                    gameManager.addToGameSummary(String.format("%s%n", msg));
//                    System.out.printf("%s%n", msg);
//                }
            }
        }
    }

    private ActionType playerTurn(Player player, TeamModel me, TeamModel you) {
        try {
            final ActionType action = player.getAction(leagueId);
            gameManager.addToGameSummary(
                    String.format("%s played %s",
                            player.getNicknameToken(),
                            action.name()));
            return action;
        } catch (NumberFormatException e) {
            player.deactivate("Wrong output, excepted:integer!");
        } catch (TimeoutException e) {
            gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + " timeout!"));
            player.deactivate(player.getNicknameToken() + " timeout!");
        } catch (InvalidAction e) {
            player.deactivate(e.getMessage());
        }
        return null;
    }

    private void endGame() {
        setScore();
        gameManager.endGame();
        // TODO refresh view
    }

    private void setScore() {
        Player playerA = gameManager.getPlayer(0);
        Player playerB = gameManager.getPlayer(1);

        playerA.setScore(playerA.isActive() ? state.teamA.score - state.teamB.score : -20);
        playerB.setScore(playerB.isActive() ? state.teamB.score - state.teamA.score : -20);

        gameManager.addToGameSummary(GameManager.formatSuccessMessage(
                "Final result: " + playerA.getNicknameToken() + "(" + playerA.getScore() + "), "
                        + playerB.getNicknameToken() + "(" + playerB.getScore() + ")"
        ));
    }

    @Override
    public void playerTired(PlayerModel player) {
        view.playerKo(player);
    }

    @Override
    public void scored(TeamModel team) {
        view.scored(team);
    }

    @Override
    public void outside(PlayerModel player) {

    }

    @Override
    public void collided() {
    }

    @Override
    public void winTheGame() {
        endGame();
    }

    @Override
    public void onEnd() {
        setScore();
    }

    @Override
    public void draw() {
        gameManager.addToGameSummary("Draw!");

        endGame();
    }

    @Override
    public void move(PlayerModel player, int from, int to) {
        view.move(player, from, to);
    }

    @Override
    public void energyChanged(PlayerModel player, int delta) {
        view.energyChanged(player, delta);
    }

    @Override
    public void hit(PlayerModel player) {
        view.hit(player);
    }

    @Override
    public void missed(PlayerModel player) {
        view.missed(player);
    }
}