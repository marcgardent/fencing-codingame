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

    public GameState tick(GameInput teamA, GameInput teamB) {
        resolveEnergy(state.teamA, teamA);
        resolveEnergy(state.teamB, teamB);

        //Moves
        resolveMove(state.teamA.Player, teamA.Move);
        resolveMove(state.teamB.Player, teamB.Move);

        resolveScore(teamA.Action, teamB.Action);

        checkTheEnd();

        state.tick += 1;
        return state;
    }

    private void checkTheEnd() {

        boolean timeout = state.tick >= Match.MAX_TICK;

        if (state.teamA.Score >= TeamState.SCORE_MAX && state.teamA.Score - state.teamB.Score >= TeamState.SCORE_GAP) {
            observer.winTheGame(state.teamA, state.teamB);
        } else if (state.teamB.Score >= TeamState.SCORE_MAX && state.teamB.Score - state.teamA.Score >= TeamState.SCORE_GAP) {
            observer.winTheGame(state.teamB, state.teamA);
        } else if (timeout && state.teamA.Score > state.teamB.Score) {
            observer.winTheGame(state.teamA, state.teamB);
        } else if (timeout && state.teamB.Score > state.teamA.Score) {
            observer.winTheGame(state.teamB, state.teamA);
        } else if (timeout) {
            observer.draw();
        } else if (state.teamA.Player.Position >= state.teamB.Player.Position) {
            observer.collide();
            state.restart = true;
        }
    }

    private void resolveEnergy(TeamState team, GameInput input) {
        byte gain = 0;
        if (input.Action == GameInput.IDLE) {
            gain += 1;
        } else {
            gain -= input.Action;
        }

        if (input.Move == GameInput.IDLE) {
            gain += 1;
        } else {
            gain -= 1;
        }
        addEnergy(team.Player, gain);

        if (team.Player.Energy <= 0) {

            team.Player.Energy = 0;
            input.Action = GameInput.IDLE;
            input.Move = GameInput.IDLE;
            observer.playerIsKo(team.Player);
        }
    }

    private void resolveScore(byte aAction, byte bAction) {
        byte aResolved = resolveActionA(state.teamA.Player, aAction, state.teamB.Player);
        byte bResolved = resolveActionB(state.teamB.Player, bAction, state.teamA.Player);

        observer.actionResolved(state.teamA.Player, aResolved);
        observer.actionResolved(state.teamB.Player, bResolved);

        if (aResolved != GameInput.IDLE && aResolved == bResolved) {
            state.teamA.Score += 1;
            state.teamB.Score += 1;
            observer.scoreAB();

        } else if (aResolved == GameInput.BASIC_ATTACK && bResolved == GameInput.COMPLEX_ATTACK) {
            state.teamA.Score += 1;
            observer.score(state.teamA);
        } else if (bResolved == GameInput.BASIC_ATTACK && aResolved == GameInput.COMPLEX_ATTACK) {
            state.teamB.Score += 1;
        } else if (aResolved != GameInput.IDLE || bResolved != GameInput.IDLE) {
            TeamState winner = aResolved > bResolved ? state.teamA : state.teamB;
            winner.Score += 1;
            observer.score(state.teamB);
        }
    }

    private byte resolveActionA(PlayerState player, byte action, PlayerState opponent) {
        if (action == GameInput.IDLE) {
            return action;
        } else if (opponent.Position <= (player.Position + player.Orientation * player.Range)) {
            return action;
        } else {
            return GameInput.IDLE;
        }
    }

    private byte resolveActionB(PlayerState player, byte action, PlayerState opponent) {
        if (action == GameInput.IDLE) {
            return action;
        } else if (opponent.Position >= (player.Position + player.Orientation * player.Range)) {
            return action;
        } else {
            return GameInput.IDLE;
        }
    }

    private void resolveMove(PlayerState player, byte move) {
        int p = player.Position;
        if (move == GameInput.FORWARD) {
            p += move * player.Orientation * player.Step;
        } else if (move == GameInput.BACKWARD) {
            p -= player.Orientation * player.Step;
        }

        if (p >= PlayerState.MIN_POSITION && p < PlayerState.MAX_POSITION) {
            if (p != player.Position) {
                observer.move(player, p, player.Position);
            }
            player.Position = p;
        } else {
            observer.outside(player);
        }
        //else no move
    }

    private void addEnergy(PlayerState player, int delta) {
        byte total = (byte) Math.min(player.Energy + delta, player.EnergyMax);
        if (total != player.Energy) {
            observer.energyChanged(player, delta);
            player.Energy = total;
        }
    }

    private void initTeam(TeamState team, int spawn, byte orientation) {
        team.Score = 0;
        team.Player.Energy = PlayerState.ENERGY_START;
        team.Player.Position = spawn;
        team.Player.Orientation = orientation;
    }

    public GameState restart() {
        state.restart = false;

        observer.move(state.teamA.Player, state.teamA.Player.Position, PlayerState.SPAWN_POSITION_A);
        state.teamA.Player.Position = PlayerState.SPAWN_POSITION_A;

        observer.move(state.teamB.Player, state.teamB.Player.Position, PlayerState.SPAWN_POSITION_B);
        state.teamB.Player.Position = PlayerState.SPAWN_POSITION_B;

        addEnergy(state.teamA.Player, 2);
        addEnergy(state.teamA.Player, 2);

        checkTheEnd();
        state.tick += 1;
        return state;
    }
}