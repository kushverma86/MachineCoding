package com.battleship.service;

import com.battleship.exception.GameException;
import com.battleship.model.Game;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages multiple concurrent games. Implements a Singleton pattern
 * at the application level to hold the map of all active games.
 */
public class GameManager {
    private static final GameManager INSTANCE = new GameManager();
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private final GameService gameService = new GameService();

    private GameManager() {
        // Private constructor for Singleton
    }

    public static GameManager getInstance() {
        return INSTANCE;
    }

    private Game getGame(String gameId) {
        Game game = activeGames.get(gameId);
        if (game == null) {
            throw new GameException("Game with ID " + gameId + " not found.");
        }
        return game;
    }

    // --- Public API for the Web Layer ---

    public String initGame(int N) {
        Game game = gameService.initGame(N);
        activeGames.put(game.getGameId(), game);
        return game.getGameId();
    }

    public void addShip(String gameId, String id, int size, int xA, int yA, int xB, int yB) {
        gameService.addShip(getGame(gameId), id, size, xA, yA, xB, yB);
    }

    public void viewBattleField(String gameId) {
        gameService.viewBattleField(getGame(gameId));
    }

    public void startGame(String gameId) {
        gameService.startGame(getGame(gameId));
    }

    // Cleanup method
    public void endGame(String gameId) {
        activeGames.remove(gameId);
        System.out.println("Game " + gameId + " cleaned up.");
    }
}