package com.codingame.game.core;

public class Match {
    public static final int MAX_TICK = 200;

    private final GameState State;
    private final RefereObserver Observer;

    public Match(RefereObserver observer) {
        this.Observer = observer;
        State = InitGame();
    }

    public GameState InitGame() {
        GameState ret = new GameState();
        ret.Tick = 0;
        InitTeam(ret.TeamA, PlayerState.SPAWN_POSITION_A, PlayerState.LEFT_ORIENTATION);
        InitTeam(ret.TeamB, PlayerState.SPAWN_POSITION_B, PlayerState.RIGHT_ORIENTATION);

        return ret;
    }

    public GameState Tick(GameInput teamA, GameInput teamB) {
        //Moves
        ResolveMove(State.TeamA.Player, teamA.Move);
        ResolveMove(State.TeamB.Player, teamB.Move);

        ResolveScore(teamA.Action, teamB.Action);

        ResolveEnergy(State.TeamA, teamA);
        ResolveEnergy(State.TeamB, teamB);

        if (State.TeamA.Player.Position >= State.TeamB.Player.Position) {
            Observer.Collide();
        }

        {
            boolean timeout = State.Tick > Match.MAX_TICK;

            if (State.TeamA.Score >= TeamState.SCORE_MAX && State.TeamA.Score - State.TeamB.Score >= TeamState.SCORE_GAP) {
                Observer.WinTheGame(State.TeamA);
            } else if (State.TeamB.Score >= TeamState.SCORE_MAX && State.TeamB.Score - State.TeamA.Score >= TeamState.SCORE_GAP) {
                Observer.WinTheGame(State.TeamB);
            } else if (timeout && State.TeamA.Score > State.TeamB.Score) {
                Observer.WinTheGame(State.TeamA);
            } else if (timeout && State.TeamB.Score > State.TeamA.Score) {
                Observer.WinTheGame(State.TeamA);
            } else if (timeout) {
                Observer.Draw();
            }
        }
        State.Tick += 1;
        return State;
    }

    private void ResolveEnergy(TeamState team, GameInput input) {
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

        team.Player.Energy = (byte) Math.min(team.Player.Energy + gain, team.Player.EnergyMax);

        if (team.Player.Energy <= 0) {

            team.Player.Energy = 0;
            input.Action = GameInput.IDLE;
            input.Move = GameInput.IDLE;
            Observer.PlayerKao(team.Player);
        }
    }

    private void ResolveScore(byte aAction, byte bAction) {
        byte aResolved = ResolveAction(State.TeamA.Player, aAction, State.TeamB.Player);
        byte bResolved = ResolveAction(State.TeamB.Player, bAction, State.TeamA.Player);

        if (aResolved == bResolved) {
            State.TeamA.Score += 1;
            State.TeamB.Score += 1;
            Observer.ScoreAB();
        } else if (aResolved == GameInput.BASIC_ATTACK && bResolved == GameInput.COMPLEX_ATTACK) {
            State.TeamA.Score += 1;
            Observer.Score(State.TeamA);
        } else if (bResolved == GameInput.BASIC_ATTACK && aResolved == GameInput.COMPLEX_ATTACK) {
            State.TeamB.Score += 1;
        } else {
            TeamState winner = aResolved > bResolved ? State.TeamA : State.TeamB;
            winner.Score += 1;
            Observer.Score(State.TeamB);
        }
    }

    private byte ResolveAction(PlayerState player, byte action, PlayerState opponent) {
        if (action == GameInput.IDLE) {
            return action;
        } else if (player.Orientation * (opponent.Position - player.Position + player.Orientation + player.Range) > 0) {
            return action;
        } else {
            return GameInput.IDLE;
        }
    }

    private void ResolveMove(PlayerState player, byte move) {
        int p = player.Position;
        if (move == GameInput.FORWARD) {
            p += move * player.Orientation * player.Step;
        } else if (move == GameInput.BACKWARD) {
            p -= player.Orientation * player.Step;
        }

        if (p >= PlayerState.MIN_POSITION && p < PlayerState.MAX_POSITION) {
            player.Position = p;
        } else {
            Observer.Outside(player);
        }
        //else no move
    }

    public void Restart(TeamState team, int spawn, byte orientation) {
        team.Score = 0;
        team.Player.Energy = PlayerState.ENERGY_START;
        team.Player.Position = spawn;
        team.Player.Orientation = orientation;
    }

    private void InitTeam(TeamState team, int spawn, byte orientation) {
        team.Score = 0;
        team.Player.Energy = PlayerState.ENERGY_START;
        team.Player.Position = spawn;
        team.Player.Orientation = orientation;
    }

    private void Restart() {
        State.TeamA.Player.Position = PlayerState.SPAWN_POSITION_A;
        State.TeamB.Player.Position = PlayerState.SPAWN_POSITION_B;
    }
}