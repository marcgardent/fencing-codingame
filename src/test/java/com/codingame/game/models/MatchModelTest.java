package com.codingame.game.models;


import org.junitpioneer.jupiter.CartesianProductTest;
import org.junitpioneer.jupiter.CartesianValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class MatchModelTest {

    private static final int SUPPRESSED = 0;
    private static final int BREAK = 1;
    private static final int WALK = 2;
    private static final int RETREAT = 3;
    private static final int LUNGE = 4;
    private static final int PARRY = 5;
    private static final int MIDDLE_POSTURE = 6;
    private static final int TOP_POSTURE = 7;
    private static final int BOTTOM_POSTURE = 8;

    @CartesianProductTest
    @CartesianValueSource(ints = {TOP_POSTURE, BOTTOM_POSTURE, MIDDLE_POSTURE})
    @CartesianValueSource(ints = {100, 400, 250})
    public void DoubleTouch(int postureCode, int position) {
        ActionType posture = ActionType.fromInteger(postureCode);
        MatchModel target = new MatchModel(new DummyObserver());
        GameModel model = target.getState();

        model.teamA.player.position = position;
        model.teamB.player.position = ActionType.LUNGE.distance + position;
        model.teamA.player.posture = model.teamB.player.posture = posture;
        target.tick(ActionType.LUNGE, ActionType.LUNGE);
        assertTrue(model.teamA.player.touched, "touched A");
        assertTrue(model.teamB.player.touched, "touched B");
        assertEquals(1, model.teamA.score, "score A +1");
        assertEquals(1, model.teamB.score, "score B +1");
        assertTrue(model.restart, "restarted");
    }

    @CartesianProductTest
    @CartesianValueSource(ints = {TOP_POSTURE, BOTTOM_POSTURE, MIDDLE_POSTURE})
    @CartesianValueSource(ints = {100, 400, 250})
    public void DoubleTouchMissed1pixel(int postureCode, int position) {
        ActionType posture = ActionType.fromInteger(postureCode);
        MatchModel target = new MatchModel(new DummyObserver());
        GameModel model = target.getState();

        model.teamA.player.position = position;
        model.teamB.player.position = ActionType.LUNGE.distance + position + 1;
        model.teamA.player.posture = model.teamB.player.posture = posture;

        target.tick(ActionType.LUNGE, ActionType.LUNGE);
        assertEquals(model.teamB.player.touched, model.teamA.player.touched, "both");
        assertFalse(model.teamA.player.touched, "touched A");
        assertFalse(model.teamB.player.touched, "touched B");
        assertFalse(model.restart, "restarted");
        assertEquals(0, model.teamA.score, "score A +0");
        assertEquals(0, model.teamB.score, "score B +0");

    }


    @CartesianProductTest
    @CartesianValueSource(ints = {TOP_POSTURE, BOTTOM_POSTURE, MIDDLE_POSTURE})
    @CartesianValueSource(ints = {100, 400, 250})
    public void Parry(int postureCode, int position) {
        ActionType posture = ActionType.fromInteger(postureCode);
        MatchModel target = new MatchModel(new DummyObserver());
        GameModel model = target.getState();

        model.teamA.player.position = position;
        model.teamB.player.position = ActionType.LUNGE.distance + position;
        model.teamA.player.posture = model.teamB.player.posture = posture;
        target.tick(ActionType.LUNGE, ActionType.PARRY);
        assertFalse(model.teamA.player.touched, "touched A");
        assertFalse(model.teamB.player.touched, "touched B");
        assertEquals(0, model.teamA.score, "score A +0");
        assertEquals(0, model.teamB.score, "score B +0");
        assertFalse(model.restart, "restarted");
    }

    static class DummyObserver implements MatchObserver {

        @Override
        public void playerTired(PlayerModel player) {

        }

        @Override
        public void scored(TeamModel team) {

        }

        @Override
        public void outside(PlayerModel player) {

        }

        @Override
        public void collided() {

        }

        @Override
        public void winTheGame() {

        }

        @Override
        public void draw() {

        }

        @Override
        public void move(PlayerModel player, int from, int to) {

        }

        @Override
        public void energyChanged(PlayerModel player, int delta) {

        }

        @Override
        public void hit(PlayerModel player, boolean succeeded) {

        }

        @Override
        public void defended(PlayerModel player, boolean succeeded) {

        }
    }
}