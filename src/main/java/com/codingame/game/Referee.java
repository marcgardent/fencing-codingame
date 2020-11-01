package com.codingame.game;

import com.codingame.game.models.*;
import com.codingame.game.views.MainView;
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
    private MainView view;

    private GameState state;
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

        gameManager.setFrameDuration(250);
        gameManager.setMaxTurns(MatchModel.MAX_TICK / 2);
    }

    private long sendInputs(Player player, TeamModel me, TeamModel you) {

        // posture:int attitude:int position:int move:int
        ResultType r = ResultType.CONTINUE;
        if (me.touched && you.touched) {
            r = ResultType.DOUBLE_TOUCH;
        } else if (me.touched) {
            r = ResultType.TOUCHED;
        } else if (you.touched) {
            r = ResultType.TOUCH;
        }

        player.sendInputLine(Integer.toString(r.code));
        player.sendInputLine(String.format("%d %d %d %d %d",
                me.player.getRelativePosition(),
                me.player.posture.code, me.player.attitude.code,
                me.player.energy, me.score));

        player.sendInputLine(String.format("%d %d %d %d %d",
                you.player.getRelativeOpponentPosition(),
                you.player.posture.code, you.player.attitude.code,
                you.player.energy, you.score));

        long s = System.nanoTime();
        player.execute();
        s = System.nanoTime() - s;
        return s;
    }

    @Override
    public void gameTurn(int turn) {

        if (state.restart) {
            state = match.restart();
            view.restart();
        } else {
            Player playerA = gameManager.getPlayer(0);
            Player playerB = gameManager.getPlayer(1);

            long timeoutA = sendInputs(playerA, state.teamA, state.teamB);
            long timeoutB = sendInputs(playerB, state.teamB, state.teamA);

            gameManager.addToGameSummary(playerA.getNicknameToken() + "=" + timeoutA);
            gameManager.addToGameSummary(playerB.getNicknameToken() + "=" + timeoutB);

            ActionType A = playerTurn(playerA, state.teamA, state.teamB);
            ActionType B = playerTurn(playerB, state.teamB, state.teamA);

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
                view.tick();

                String msgA = String.join(", ", state.teamA.messages);
                String msgB = String.join(", ", state.teamB.messages);

                gameManager.addToGameSummary(String.format("%s:%s", playerA.getNicknameToken(), msgA));
                gameManager.addToGameSummary(String.format("%s:%s", playerB.getNicknameToken(), msgB));
            }
        }
    }

    private ActionType playerTurn(Player player, TeamModel me, TeamModel you) {
        try {
            final ActionType action = player.getAction(leagueId);
            gameManager.addToGameSummary(
                    String.format("Player %s played (action=%s)",
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

        playerA.setScore(playerA.isActive() ? state.teamA.score : -1);
        playerB.setScore(playerB.isActive() ? state.teamB.score : -1);

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
        System.out.println("Score");
    }

    @Override
    public void outside(PlayerModel player) {
        System.out.println("Outside");
    }

    @Override
    public void collided() {
        System.out.println("Collide");
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
        view.hit(player);
    }
}