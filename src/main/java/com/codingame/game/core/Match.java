package com.codingame.game.core;

public class Match {
    public static final int MAX_TICK = 400;

    private final GameState state;
    private final RefereObserver observer;

    public Match(RefereObserver observer) {
        this.observer = observer;
        state = initGame();
    }

    public GameState getState() {
        return state;
    }

    private GameState initGame() {
        GameState ret = new GameState();
        ret.tick = 0;
        initTeam(ret.teamA, PlayerState.SPAWN_POSITION_A, PlayerState.LEFT_ORIENTATION);
        initTeam(ret.teamB, PlayerState.SPAWN_POSITION_B, PlayerState.RIGHT_ORIENTATION);
        return ret;
    }

    public GameState tick(ActionType actionA, ActionType actionB) {
        state.teamA.messages.clear();
        state.teamB.messages.clear();

        actionB = resolveEnergy(state.teamB, actionB);
        actionA = resolveEnergy(state.teamA, actionA);

        setMoveAndVelocity(state.teamB, actionB);
        setMoveAndVelocity(state.teamA, actionA);

        applyMove(state.teamA.player);
        applyMove(state.teamB.player);

        actionA = resolvePose(state.teamA, actionA);
        actionB = resolvePose(state.teamA, actionB);

        resolveScore(actionA, actionB);

        checkTheEnd();

        state.tick += 1;
        return state;
    }

    private void checkTheEnd() {

        boolean timeout = state.tick >= Match.MAX_TICK;

        if (state.teamA.score >= TeamState.SCORE_MAX && state.teamA.score - state.teamB.score >= TeamState.SCORE_GAP) {
            observer.winTheGame();
        } else if (state.teamB.score >= TeamState.SCORE_MAX && state.teamB.score - state.teamA.score >= TeamState.SCORE_GAP) {
            observer.winTheGame();
        } else if (timeout && state.teamA.score > state.teamB.score) {
            observer.winTheGame();
        } else if (timeout && state.teamB.score > state.teamA.score) {
            observer.winTheGame();
        } else if (timeout) {
            observer.draw();
        } else if (state.teamA.player.position >= state.teamB.player.position) {
            observer.collide();
            state.restart = true;
        }
    }

    private ActionType resolveEnergy(TeamState team, ActionType action) {
        int gain = action.energy;
        addEnergy(team.player, gain);

        if (team.player.energy <= 0) {
            team.player.energy = 0;
            team.messages.add(action.name() + " suppressed because of the KO");
            observer.playerIsKo(team.player);
            return ActionType.SUPPRESSED;
        }
        if (gain != 0) team.messages.add("energy " + (gain >= 0 ? "+" : "") + gain);
        return action;
    }

    private void resolveScore(ActionType actionA, ActionType actionB) {
        state.teamA.touched = false;
        state.teamB.touched = false;

        if (actionA.offensiveRange > 0) {
            state.teamB.touched = isTouched(state.teamA, actionA, state.teamB, actionB);
            if (state.teamB.touched) {
                state.teamA.score += 1;
                observer.score(state.teamA);
            }
        }
        if (actionB.offensiveRange > 0) {
            state.teamA.touched = isTouched(state.teamB, actionB, state.teamA, actionA);
            if (state.teamA.touched) {
                state.teamB.score += 1;
                observer.score(state.teamB);
            }
        }
    }

    private boolean isTouched(TeamState striker, ActionType offensiveAction, TeamState defender, ActionType defenseAction) {

        int defenseRange = (state.teamA.player.posture == state.teamB.player.posture) ? defenseAction.defensiveRange + defender.player.defensiveRangeSkill : 0;
        int offensiveRange = offensiveAction.offensiveRange + striker.player.offensiveRangeSkill;

        int defenseLength = defender.player.getRelativePosition() - defenseRange;
        int offensiveLength = striker.player.getRelativePosition() + offensiveRange;

        if (defenseLength + offensiveLength > PlayerState.MAX_POSITION) {
            if (defenseAction.defensiveRange > 0) {
                defender.messages.add(defenseAction.name() + "(" + defenseRange + ")" + " failed");
            }
            striker.messages.add(offensiveAction.name() + "(" + offensiveAction + ")" + " touched");
            observer.hit(striker.player);
            observer.miss(striker.player);
            return true;
        } else {
            if (defenseAction.defensiveRange > 0) {
                defender.messages.add(defenseAction.name() + "(" + defenseRange + ")" + " succeeded");
            }
            striker.messages.add(offensiveAction.name() + "(" + offensiveAction + ")" + " failed");
            observer.miss(striker.player);
            observer.hit(striker.player);
            return false;
        }
    }


    private ActionType resolvePose(TeamState team, ActionType action) {
        if (action == ActionType.BOTTOM_POSTURE || action == ActionType.TOP_POSTURE || action == ActionType.MIDDLE_POSTURE) {
            if (action != team.player.posture) team.messages.add("posture changed:" + action.name());
            else team.messages.add("posture ignored:" + action.name());
            team.player.posture = action;
        }
        if (action == ActionType.DEFENSIVE_ATTITUDE || action == ActionType.OFFENSIVE_ATTITUDE) {
            if (action != team.player.posture) team.messages.add("attitude changed:" + action.name());
            else team.messages.add("attitude ignored:" + action.name());
            team.player.attitude = action;
        }
        return action;
    }

    private void setMoveAndVelocity(TeamState team, ActionType action) {
        PlayerState player = team.player;
        if (action.move != 0) {
            boolean noChanged = (action.move > 0 && player.move.move > 0) || (action.move < 0 && player.move.move < 0);
            player.velocity = noChanged ? player.velocity + 1 : 0;
            player.move = action;
        } else if (action == ActionType.BREAK_ATTITUDE) {
            player.velocity = 0;
            player.move = action;
        } else {
            player.velocity = 0;
        }
    }

    private void applyMove(PlayerState player) {
        int move = player.getMove();
        if (move > 0) {
            int p = player.position + player.orientation * move;
            p = Math.max(p, PlayerState.MIN_POSITION);
            p = Math.min(p, PlayerState.MAX_POSITION);
            if (p == PlayerState.MIN_POSITION || p == PlayerState.MAX_POSITION) {
                observer.outside(player);
                player.velocity = 0;
            } else {
                observer.move(player, p, player.position);
                player.position = p;
            }
        }
    }

    private void addEnergy(PlayerState player, int delta) {
        byte total = (byte) Math.min(player.energy + delta, player.energyMax);
        if (total != player.energy) {
            observer.energyChanged(player, delta);
            player.energy = total;
        }
    }

    private void initTeam(TeamState team, int spawn, int orientation) {
        team.score = 0;
        team.player.energy = PlayerState.ENERGY_START;
        team.player.position = spawn;
        team.player.orientation = orientation;
        team.player.reset();
    }

    public GameState restart() {
        state.restart = false;

        observer.move(state.teamA.player, state.teamA.player.position, PlayerState.SPAWN_POSITION_A);
        state.teamA.player.position = PlayerState.SPAWN_POSITION_A;
        state.teamB.player.reset();

        observer.move(state.teamB.player, state.teamB.player.position, PlayerState.SPAWN_POSITION_B);
        state.teamB.player.position = PlayerState.SPAWN_POSITION_B;
        state.teamB.player.reset();

//        addEnergy(state.teamA.player, 2);
//        addEnergy(state.teamA.player, 2);
//
        checkTheEnd();
        state.tick += 1;
        return state;
    }
}