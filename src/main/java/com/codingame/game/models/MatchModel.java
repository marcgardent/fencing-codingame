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

        setDoping(state.teamA, actionA);
        setDoping(state.teamB, actionB);

        boolean legalMoveA = applyMove(state.teamA.player, actionA);
        boolean legalMoveB = applyMove(state.teamB.player, actionB);

        if (legalMoveA && legalMoveB) {
            setPose(state.teamA, actionA);
            setPose(state.teamA, actionB);
            resolveScore(actionA, actionB);
        }
        if (!legalMoveA) {
            state.teamB.score += 1;
            observer.scored(state.teamB);
            state.restart = true;

        }
        if (!legalMoveB) {
            state.teamB.score += 1;
            observer.scored(state.teamB);
            state.restart = true;
        }

        checkTheEnd();
        checkTheRestart(legalMoveA, legalMoveB);

        state.tick += 1;
        return state;
    }

    private void setDoping(TeamModel team, ActionType a) {
        PlayerModel player = team.player;

        if (a.doping > 0) {
            if (a == ActionType.DEFENSIVE_RANGE_SKILL) {
                player.defensiveRangeSkill += a.doping;
            } else if (a == ActionType.BACKWARD_SKILL) {
                player.backwardSkill += a.doping;
            } else if (a == ActionType.DOUBLE_BACKWARD_SKILL) {
                player.doubleBackwardSkill += a.doping;
            } else if (a == ActionType.FORWARD_SKILL) {
                player.forwardSkill += a.doping;
            } else if (a == ActionType.DOUBLE_FORWARD_SKILL) {
                player.doubleBackwardSkill += a.doping;
            } else if (a == ActionType.OFFENSIVE_RANGE_SKILL) {
                player.offensiveRangeSkill += a.doping;
            } else if (a == ActionType.ENERGY_MAX_SKILL) {
                player.energyMax += a.doping;
            }

            player.dopings.add(a);
            observer.doped(player, a);
        }
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
        }
    }

    private void checkTheRestart(boolean legalMoveA, boolean legalMoveB) {
        if (state.teamA.player.touched || state.teamB.player.touched) {
            state.restart = true;
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
            return ActionType.SUPPRESSED;
        }
        if (gain != 0) {
            team.messages.add("energy " + (gain >= 0 ? "+" : "") + gain);
        }

        return action;
    }

    private void resolveScore(ActionType actionA, ActionType actionB) {
        state.teamA.player.touched = false;
        state.teamB.player.touched = false;
        if (actionA.distance > 0) {
            state.teamB.player.touched = isTouched(state.teamA, actionA, state.teamB, actionB);
            if (state.teamB.player.touched) {
                state.teamA.score += 1;
                observer.scored(state.teamA);
            }
        } else if (actionB.distance < 0) {
            observer.defended(state.teamB.player, false);
        }

        if (actionB.distance > 0) {
            state.teamA.player.touched = isTouched(state.teamB, actionB, state.teamA, actionA);
            if (state.teamA.player.touched) {
                state.teamB.score += 1;
                observer.scored(state.teamB);
            }
        } else if (actionA.distance < 0) {
            observer.defended(state.teamA.player, false);
        }
    }

    private boolean isTouched(TeamModel striker, ActionType offensiveAction, TeamModel defender, ActionType defenseAction) {
        int defenseDistance = (state.teamA.player.posture == state.teamB.player.posture && defenseAction.distance < 0)
                ? defenseAction.distance + defender.player.defensiveRangeSkill : 0;
        int offensiveDistance = offensiveAction.distance + striker.player.offensiveRangeSkill;

        int defenseLength = defender.player.getRelativePosition() + defenseDistance;
        int offensiveLength = striker.player.getRelativePosition() + offensiveDistance;

        if (defenseLength + offensiveLength >= PlayerModel.MAX_POSITION) {
            if (defenseAction.distance > 0) {
                defender.messages.add(defenseAction.name() + "(" + defenseDistance + ")" + " failed");
                observer.defended(defender.player, false);
            }
            striker.messages.add(offensiveAction.name() + "(" + offensiveDistance + ")" + " touched");
            observer.hit(striker.player, true);

            return true;
        } else {
            if (defenseAction.distance > 0) {
                defender.messages.add(defenseAction.name() + "(" + defenseDistance + ")" + " succeeded");
                observer.hit(defender.player, true);
            }
            striker.messages.add(offensiveAction.name() + "(" + offensiveDistance + ")" + " failed");
            observer.hit(striker.player, false);

            return false;
        }
    }

    private void setPose(TeamModel team, ActionType action) {
        if (action == ActionType.BOTTOM_POSTURE || action == ActionType.TOP_POSTURE || action == ActionType.MIDDLE_POSTURE) {
            if (action != team.player.posture) team.messages.add("posture changed:" + action.name());
            else team.messages.add("posture ignored:" + action.name());
            team.player.posture = action;
        }
    }

    private boolean applyMove(PlayerModel player, ActionType action) {
        boolean ret = true;
        if (action.move != 0) {
            int move = player.getMove(action);
            if (move != 0) {
                int p = player.position + player.orientation * move;
                if (p < PlayerModel.MIN_POSITION || p > PlayerModel.MAX_POSITION) {
                    observer.outside(player);
                    ret = false;
                }
                p = Math.max(p, PlayerModel.MIN_POSITION);
                p = Math.min(p, PlayerModel.MAX_POSITION);

                observer.move(player, p, player.position);
                player.position = p;
            }
        }
        return ret;
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
        state.teamA.player.reset();

        observer.move(state.teamB.player, state.teamB.player.position, PlayerModel.SPAWN_POSITION_B);
        state.teamB.player.position = PlayerModel.SPAWN_POSITION_B;
        state.teamB.player.reset();

        checkTheEnd();
        state.tick += 1;
        return state;
    }
}