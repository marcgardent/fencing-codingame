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
        state.teamA.messages.clear();
        state.teamB.messages.clear();
        resolveEnergy(state.teamA, teamA);
        resolveEnergy(state.teamB, teamB);

        //Moves
        resolveMove(state.teamA, teamA.move);
        resolveMove(state.teamB, teamB.move);

        resolveScore(teamA.action, teamB.action);

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

    private void resolveEnergy(TeamState team, GameInput input) {
        byte gain = 0;
        gain -= input.action;
        if (input.move == GameInput.IDLE) {
            gain += 1;
        } else {
            gain -= 1;
        }
        addEnergy(team.player, gain);

        if (team.player.energy <= 0) {
            team.player.energy = 0;
            if (input.action != GameInput.IDLE)
                team.messages.add(GameInput.getLabel(input.action) + " suppressed because of the KO");
            if (input.action != GameInput.IDLE)
                team.messages.add(GameInput.getLabel(input.move) + " suppressed  because of the KO");

            input.action = GameInput.IDLE;
            input.move = GameInput.IDLE;
            observer.playerIsKo(team.player);
        }
        if (gain != 0) team.messages.add("energy " + (gain >= 0 ? "+" : "") + gain);
    }

    private void resolveScore(byte aAction, byte bAction) {
        byte aResolved = resolveActionA(state.teamA, aAction, state.teamB.player);
        byte bResolved = resolveActionB(state.teamB, bAction, state.teamA.player);

        if (aResolved != GameInput.IDLE && aResolved == bResolved) {
            state.teamA.score += 1;
            state.teamB.score += 1;
            state.teamA.messages.add("score +1 due to an equality");
            state.teamB.messages.add("score +1 due to an equality");
            observer.scoreAB();

        } else if (aResolved == GameInput.BASIC_ATTACK && bResolved == GameInput.COMPLEX_ATTACK) {
            state.teamA.score += 1;
            state.teamA.messages.add("score +1 thanks to " + GameInput.getLabel(aResolved) + ">" + GameInput.getLabel(bResolved));
            observer.score(state.teamA);
        } else if (bResolved == GameInput.BASIC_ATTACK && aResolved == GameInput.COMPLEX_ATTACK) {
            state.teamB.score += 1;
            state.teamB.messages.add("score +1 thanks to " + GameInput.getLabel(bResolved) + ">" + GameInput.getLabel(aResolved));
        } else if (aResolved != GameInput.IDLE || bResolved != GameInput.IDLE) {
            TeamState winner = aResolved > bResolved ? state.teamA : state.teamB;
            winner.score += 1;
            winner.messages.add("score +1 thanks to " + GameInput.getLabel(aResolved > bResolved ? aResolved : bResolved) + ">" + GameInput.getLabel(aResolved < bResolved ? aResolved : bResolved));
            observer.score(state.teamB);
        }
    }

    private byte resolveActionA(TeamState team, byte action, PlayerState opponent) {
        PlayerState player = team.player;
        if (action == GameInput.IDLE) {
            return action;
        } else if (opponent.position <= (player.position + player.orientation * player.range)) {
            team.messages.add(GameInput.getLabel(action) + " done");
            observer.hit(player, action);
            return action;
        } else {
            team.messages.add(GameInput.getLabel(action) + " missed");
            observer.miss(player, action);
            return GameInput.IDLE;
        }
    }

    private byte resolveActionB(TeamState team, byte action, PlayerState opponent) {
        PlayerState player = team.player;
        if (action == GameInput.IDLE) {
            return action;
        } else if (opponent.position >= (player.position + player.orientation * player.range)) {
            team.messages.add(GameInput.getLabel(action) + " done");
            observer.hit(player, action);
            return action;
        } else {
            team.messages.add(GameInput.getLabel(action) + " missed");
            observer.miss(player, action);
            return GameInput.IDLE;
        }
    }

    private byte resolveAction(TeamState team, byte action, PlayerState opponent) {
        PlayerState player = team.player;
        if (action == GameInput.IDLE) {
            return action;
        } else if (opponent.position * opponent.orientation >= (player.position + player.orientation * player.range)) {
            team.messages.add(GameInput.getLabel(action) + " done");
            observer.hit(player, action);
            return action;
        } else {
            team.messages.add(GameInput.getLabel(action) + " missed");
            observer.miss(player, action);
            return GameInput.IDLE;
        }
    }

    private void resolveMove(TeamState team, byte move) {
        PlayerState player = team.player;
        int p = player.position;
        if (move == GameInput.FORWARD) {
            p += move * player.orientation * player.step;

        } else if (move == GameInput.BACKWARD) {
            p -= player.orientation * player.step;
        }

        if (p >= PlayerState.MIN_POSITION && p < PlayerState.MAX_POSITION) {
            if (p != player.position) {
                observer.move(player, p, player.position);
                team.messages.add(GameInput.getLabel(move) + " done");
            }
            player.position = p;
        } else {
            observer.outside(player);
            team.messages.add(GameInput.getLabel(move) + " suppressed due to the limits");
        }
    }

    private void addEnergy(PlayerState player, int delta) {
        byte total = (byte) Math.min(player.energy + delta, player.energyMax);
        if (total != player.energy) {
            observer.energyChanged(player, delta);
            player.energy = total;
        }
    }

    private void initTeam(TeamState team, int spawn, byte orientation) {
        team.score = 0;
        team.player.energy = PlayerState.ENERGY_START;
        team.player.position = spawn;
        team.player.orientation = orientation;
    }

    public GameState restart() {
        state.restart = false;

        observer.move(state.teamA.player, state.teamA.player.position, PlayerState.SPAWN_POSITION_A);
        state.teamA.player.position = PlayerState.SPAWN_POSITION_A;

        observer.move(state.teamB.player, state.teamB.player.position, PlayerState.SPAWN_POSITION_B);
        state.teamB.player.position = PlayerState.SPAWN_POSITION_B;

        addEnergy(state.teamA.player, 2);
        addEnergy(state.teamA.player, 2);
        checkTheEnd();
        state.tick += 1;
        return state;
    }
}