package com.codingame.game.models;

public class MatchModel {
    public static final int MAX_TICK = 400;

    private final GameModel state;
    private final MatchObserver observer;

    public MatchModel(MatchObserver observer) {
        this.observer = observer;
        state = initGame();
    }

    public GameModel getState() {
        return state;
    }

    private GameModel initGame() {
        GameModel ret = new GameModel();
        ret.tick = 0;
        initTeam(ret.teamA, PlayerModel.SPAWN_POSITION_A, PlayerModel.LEFT_ORIENTATION);
        initTeam(ret.teamB, PlayerModel.SPAWN_POSITION_B, PlayerModel.RIGHT_ORIENTATION);
        return ret;
    }

    public GameModel tick(ActionType actionA, ActionType actionB) {
        state.teamA.messages.clear();
        state.teamB.messages.clear();

        actionB = resolveEnergy(state.teamB, actionB);
        actionA = resolveEnergy(state.teamA, actionA);

        setMoveAndVelocity(state.teamB, actionB);
        setMoveAndVelocity(state.teamA, actionA);

        applyMove(state.teamA.player);
        applyMove(state.teamB.player);

        setPose(state.teamA, actionA);
        setPose(state.teamA, actionB);

        resolveScore();

        checkTheEnd();

        state.tick += 1;
        return state;
    }

    private void checkTheEnd() {

        boolean timeout = state.tick >= MatchModel.MAX_TICK;

        if (state.teamA.score >= TeamModel.SCORE_MAX && state.teamA.score - state.teamB.score >= TeamModel.SCORE_GAP) {
            observer.winTheGame();
        } else if (state.teamB.score >= TeamModel.SCORE_MAX && state.teamB.score - state.teamA.score >= TeamModel.SCORE_GAP) {
            observer.winTheGame();
        } else if (timeout && state.teamA.score > state.teamB.score) {
            observer.winTheGame();
        } else if (timeout && state.teamB.score > state.teamA.score) {
            observer.winTheGame();
        } else if (timeout) {
            observer.draw();
        } else if (state.teamA.player.position >= state.teamB.player.position) {
            observer.collided();
            state.restart = true;
        }
    }

    private ActionType resolveEnergy(TeamModel team, ActionType action) {
        int gain = action.energy;
        addEnergy(team.player, gain);

        if (team.player.energy <= 0) {
            team.player.energy = 0;
            team.messages.add(action.name() + " suppressed because of the KO");
            observer.playerTired(team.player);
            team.player.attitude = ActionType.SUPPRESSED;
            team.player.move = ActionType.SUPPRESSED;
            return ActionType.SUPPRESSED;
        }
        if (gain != 0) {
            team.messages.add("energy " + (gain >= 0 ? "+" : "") + gain);
        }
        if (action == ActionType.BREAK) {
            team.player.attitude = ActionType.BREAK;
            team.player.move = ActionType.BREAK;
        }
        return action;
    }

    private void resolveScore() {
        state.teamA.touched = false;
        state.teamB.touched = false;

        if (state.teamA.player.attitude.offensiveRange > 0) {
            state.teamB.touched = isTouched(state.teamA, state.teamB);
            if (state.teamB.touched) {
                state.teamA.score += 1;
                observer.scored(state.teamA);
            }
        }
        if (state.teamB.player.attitude.offensiveRange > 0) {
            state.teamA.touched = isTouched(state.teamB, state.teamA);
            if (state.teamA.touched) {
                state.teamB.score += 1;
                observer.scored(state.teamB);
            }
        }
    }

    private boolean isTouched(TeamModel striker, TeamModel defender) {
        ActionType defenseAction = defender.player.attitude;
        ActionType offensiveAction = striker.player.attitude;
        int defenseRange = (state.teamA.player.posture == state.teamB.player.posture) ? defenseAction.defensiveRange + defender.player.defensiveRangeSkill : 0;
        int offensiveRange = offensiveAction.offensiveRange + striker.player.offensiveRangeSkill;

        int defenseLength = defender.player.getRelativePosition() - defenseRange;
        int offensiveLength = striker.player.getRelativePosition() + offensiveRange;

        if (defenseLength + offensiveLength > PlayerModel.MAX_POSITION) {
            if (defenseAction.defensiveRange > 0) {
                defender.messages.add(defenseAction.name() + "(" + defenseRange + ")" + " failed");
            }
            striker.messages.add(offensiveAction.name() + "(" + offensiveRange + ")" + " touched");
            observer.hit(striker.player);
            observer.missed(striker.player);
            return true;
        } else {
            if (defenseAction.defensiveRange > 0) {
                defender.messages.add(defenseAction.name() + "(" + defenseRange + ")" + " succeeded");
            }
            striker.messages.add(offensiveAction.name() + "(" + offensiveRange + ")" + " failed");
            observer.missed(striker.player);
            observer.hit(striker.player);
            return false;
        }
    }


    private void setPose(TeamModel team, ActionType action) {
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
    }

    private void setMoveAndVelocity(TeamModel team, ActionType action) {
        PlayerModel player = team.player;
        if (action.move != 0) {
            boolean noChanged = (action.move > 0 && player.move.move > 0) || (action.move < 0 && player.move.move < 0);
            player.velocity = noChanged ? player.velocity + 1 : 0;
            player.move = action;
        } else if (action == ActionType.BREAK) {
            player.velocity = 0;
            player.move = action;
        } else {
            player.velocity = 0;
        }
    }

    private void applyMove(PlayerModel player) {
        int move = player.getMove();
        if (move != 0) {
            int p = player.position + player.orientation * move;
            p = Math.max(p, PlayerModel.MIN_POSITION);
            p = Math.min(p, PlayerModel.MAX_POSITION);
            if (p == PlayerModel.MIN_POSITION || p == PlayerModel.MAX_POSITION) {
                observer.outside(player);
                player.velocity = 0;
            } else {
                observer.move(player, p, player.position);
                player.position = p;
            }
        }
    }

    private void addEnergy(PlayerModel player, int delta) {
        byte total = (byte) Math.min(player.energy + delta, player.energyMax);
        if (total != player.energy) {
            observer.energyChanged(player, delta);
            player.energy = total;
        }
    }

    private void initTeam(TeamModel team, int spawn, int orientation) {
        team.score = 0;
        team.player.energy = PlayerModel.ENERGY_START;
        team.player.position = spawn;
        team.player.orientation = orientation;
        team.player.reset();
    }

    public GameModel restart() {
        state.restart = false;

        observer.move(state.teamA.player, state.teamA.player.position, PlayerModel.SPAWN_POSITION_A);
        state.teamA.player.position = PlayerModel.SPAWN_POSITION_A;
        state.teamB.player.reset();

        observer.move(state.teamB.player, state.teamB.player.position, PlayerModel.SPAWN_POSITION_B);
        state.teamB.player.position = PlayerModel.SPAWN_POSITION_B;
        state.teamB.player.reset();

//        addEnergy(state.teamA.player, 2);
//        addEnergy(state.teamA.player, 2);
//
        checkTheEnd();
        state.tick += 1;
        return state;
    }
}