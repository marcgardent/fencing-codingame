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
    @CartesianValueSource(booleans = {true, false})
    public void DoubleTouch(int postureCode, int position, boolean AB) {
        ActionType posture = ActionType.fromInteger(postureCode);
        MatchModel target = new MatchModel(new DummyObserver());
        GameModel model = target.getState();

        TeamModel left = AB ? model.teamA : model.teamB;
        TeamModel right = AB ? model.teamB : model.teamA;

        left.player.position = position;
        right.player.position = ActionType.LUNGE.distance * left.player.orientation + position;
        left.player.posture = right.player.posture = posture;

        target.tick(ActionType.LUNGE, ActionType.LUNGE);
        assertTrue(left.player.touched, "touched A");
        assertTrue(right.player.touched, "touched B");
        assertEquals(1, left.score, "score A +1");
        assertEquals(1, right.score, "score B +1");
        assertTrue(model.restart, "restarted");
    }

    @CartesianProductTest
    @CartesianValueSource(ints = {TOP_POSTURE, BOTTOM_POSTURE, MIDDLE_POSTURE})
    @CartesianValueSource(ints = {100, 400, 250})
    @CartesianValueSource(booleans = {true, false})
    public void DoubleTouchMissed1pixel(int postureCode, int position, boolean AB) {
        ActionType posture = ActionType.fromInteger(postureCode);
        MatchModel target = new MatchModel(new DummyObserver());
        GameModel model = target.getState();

        TeamModel left = AB ? model.teamA : model.teamB;
        TeamModel right = AB ? model.teamB : model.teamA;

        left.player.position = position;
        right.player.position = (ActionType.LUNGE.distance + 1) * left.player.orientation + position;
        left.player.posture = model.teamB.player.posture = posture;
        target.tick(ActionType.LUNGE, ActionType.LUNGE);
        assertFalse(left.player.touched, "touched L");
        assertFalse(right.player.touched, "touched R");
        assertFalse(model.restart, "restarted");
        assertEquals(0, left.score, "score L +0");
        assertEquals(0, right.score, "score R +0");

    }

    @CartesianProductTest
    @CartesianValueSource(ints = {TOP_POSTURE, BOTTOM_POSTURE, MIDDLE_POSTURE})
    @CartesianValueSource(ints = {100, 400, 250})
    @CartesianValueSource(booleans = {true, false})
    public void Parry(int postureCode, int position, boolean AB) {
        ActionType posture = ActionType.fromInteger(postureCode);
        MatchModel target = new MatchModel(new DummyObserver());
        GameModel model = target.getState();

        TeamModel left = AB ? model.teamA : model.teamB;
        TeamModel right = AB ? model.teamB : model.teamA;

        left.player.position = position;
        right.player.position = ActionType.LUNGE.distance * left.player.orientation + position;
        left.player.posture = right.player.posture = posture;
        target.tick(ActionType.LUNGE, ActionType.PARRY);
        assertFalse(left.player.touched, "touched S");
        assertFalse(right.player.touched, "touched D");
        assertEquals(0, left.score, "score S +0");
        assertEquals(0, right.score, "score D +0");
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

        @Override
        public void doped(PlayerModel player, ActionType a) {

        }
    }
}