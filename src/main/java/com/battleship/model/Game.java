package com.battleship.model;

import com.battleship.strategy.FireStrategy;
import lombok.Getter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The main game entity. Holds all game state for a single instance.
 *
 */
@Getter
public class Game {
    private final String gameId;
    private final int N;
    private final Player playerA;
    private final Player playerB;

    // Store all coordinates that have been fired upon across both players
    private final Set<Coordinate> firedCoordinates;

    // Map of occupied Coordinate to Ship ID and Player Name (for viewBattleField)
    // Key: Coordinate, Value: Ship ID and Player Name (e.g., "A-SH1" or "B-SH2")
    private final Map<Coordinate, String> battlefieldGrid;

    private boolean isGameOver = false;
    private Player winner = null;
    private FireStrategy fireStrategy;


    public Game(String gameId, int n, FireStrategy fireStrategy) {
        this.gameId = gameId;
        this.N = n;
        this.playerA = new Player("PlayerA");
        this.playerB = new Player("PlayerB");
        this.fireStrategy = fireStrategy;
        this.firedCoordinates = new ConcurrentSkipListSet<>();
        this.battlefieldGrid = new ConcurrentHashMap<>();
    }

    public void setWinner(Player winner) {
        this.winner = winner;
        this.isGameOver = true;
    }
}