package com.battleship.service;

import com.battleship.exception.GameException;
import com.battleship.model.Coordinate;
import com.battleship.model.Game;
import com.battleship.model.Player;
import com.battleship.model.Ship;
import com.battleship.strategy.RandomCoordinateFireStrategy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage a single instance of the Battleship game.
 * All core game logic resides here.
 */
public class GameService {

    // --- API Methods ---

    /**
     * Initializes a new game instance.
     * @param N The size of the NxN battlefield.
     * @return The newly created Game object.
     */
    public Game initGame(int N) {
        if (N <= 0 || N % 2 != 0) {
            throw new GameException("N must be a positive even integer.");
        }
        String gameId = UUID.randomUUID().toString();
        // Default fire strategy is RandomCoordinateFireStrategy
        Game game = new Game(gameId, N, new RandomCoordinateFireStrategy());
        System.out.println(">> initGame(" + N + ")");
        System.out.printf("Game initialized with ID: %s. Battlefield size: %dx%d%n", gameId, N, N);
        return game;
    }

    /**
     * Adds a ship to both Player A and Player B.
     */
    public void addShip(Game game, String id, int size, int xA, int yA, int xB, int yB) {
        if (game.isGameOver()) {
            throw new GameException("Cannot add ships to an ended game.");
        }

        System.out.printf(">> addShip(\"%s\", size = %d, %d, %d, %d, %d)%n", id, size, xA, yA, xB, yB);

        validateShipPlacement(game, size, xA, yA, "PlayerA");
        validateShipPlacement(game, size, xB, yB, "PlayerB");

        // 1. Add Ship to PlayerA
        Set<Coordinate> coordsA = calculateShipCoordinates(game, size, xA, yA);
        Ship shipA = new Ship(id, size, coordsA);
        game.getPlayerA().addShip(shipA);
        updateBattlefieldGrid(game, coordsA, "A-" + id);

        // 2. Add Ship to PlayerB
        Set<Coordinate> coordsB = calculateShipCoordinates(game, size, xB, yB);
        Ship shipB = new Ship(id, size, coordsB);
        game.getPlayerB().addShip(shipB);
        updateBattlefieldGrid(game, coordsB, "B-" + id);
    }

    /**
     * Starts the game loop.
     */
    public void startGame(Game game) {
        System.out.println(">> startGame()");
        if (game.getPlayerA().getShips().isEmpty() || game.getPlayerB().getShips().isEmpty()) {
            throw new GameException("Cannot start game: both players must have ships.");
        }

        Player currentPlayer = game.getPlayerA(); // PlayerA starts first
        Player opponentPlayer = game.getPlayerB();

        while (!game.isGameOver()) {
            performTurn(game, currentPlayer, opponentPlayer);

            // Check for game over condition
            if (opponentPlayer.getRemainingShipsCount() == 0) {
                game.setWinner(currentPlayer);
                System.out.println("GameOver. " + currentPlayer.getName() + " wins.");
            }

            // Switch players for the next turn
            Player temp = currentPlayer;
            currentPlayer = opponentPlayer;
            opponentPlayer = temp;
        }
    }

    /**
     * Displays the current state of the battlefield grid.
     */
    public void viewBattleField(Game game) {
        System.out.println(">> viewBattleField()");
        int N = game.getN();
        System.out.println("\n(" + N + ", " + N + ")");

        // Use a 2D array for easier printing
        String[][] grid = new String[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = "      "; // 6 spaces for formatting
            }
        }

        // Populate grid with ship IDs
        for (Map.Entry<Coordinate, String> entry : game.getBattlefieldGrid().entrySet()) {
            Coordinate c = entry.getKey();
            String id = entry.getValue();
            // Assuming (0,0) is bottom-left (standard math/grid convention)
            // Need to map to array indices where (0, N-1) is top-left
            if (c.getX() >= 0 && c.getX() < N && c.getY() >= 0 && c.getY() < N) {
                // Adjust Y-axis for console printing (Y=0 is bottom row, Y=N-1 is top row)
                grid[N - 1 - c.getY()][c.getX()] = String.format("%-6s", id);
            }
        }

        // Print the grid
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < N; j++) {
                System.out.print(grid[i][j] + "|");
            }
            System.out.println();
        }

        System.out.println("\n(0, 0)\n");
    }

    // --- Core Logic Methods ---

    /**
     * Performs one turn of the game: fires a missile and resolves the outcome.
     */
    private void performTurn(Game game, Player currentPlayer, Player opponentPlayer) {
        int N = game.getN();
        // Determine the opponent's field boundaries
        int opponentMinX, opponentMaxX;
        if (currentPlayer.getName().equals("PlayerA")) {
            // Player A targets B's field (Right half)
            opponentMinX = N / 2;
            opponentMaxX = N; // exclusive
        } else {
            // Player B targets A's field (Left half)
            opponentMinX = 0;
            opponentMaxX = N / 2; // exclusive
        }

        // Get the target coordinate using the configured strategy
        Coordinate target = game.getFireStrategy().getNextTarget(N, opponentMinX, opponentMaxX, game.getFiredCoordinates());
        game.getFiredCoordinates().add(target);

        String result;
        String shipDestroyed = null;

        // Check if the target hits a ship
        String gridContent = game.getBattlefieldGrid().get(target);

        if (gridContent != null && gridContent.startsWith(opponentPlayer.getName().substring(6, 7))) { // "B-SH1" for PlayerA's turn
            // HIT! The gridContent stores the ship's full ID (e.g., "B-SH1")
            String shipId = gridContent.substring(2); // "SH1"
            Ship hitShip = opponentPlayer.getShips().get(shipId);

            if (hitShip != null && !hitShip.isDestroyed()) {
                hitShip.destroy();
                result = "Hit";
                shipDestroyed = String.format(" %s-%s destroyed", opponentPlayer.getName().substring(6, 7), shipId);
            } else {
                // Hit a destroyed ship's coordinate (shouldn't happen much with full ship destruction)
                result = "Miss";
            }
        } else {
            result = "Miss";
        }

        // Print turn summary
        System.out.printf("%s's turn: Missile fired at %s : \"%s\"%s : Ships Remaining - PlayerA:%d, PlayerB:%d%n",
                currentPlayer.getName(), target, result,
                shipDestroyed != null ? shipDestroyed : "",
                game.getPlayerA().getRemainingShipsCount(),
                game.getPlayerB().getRemainingShipsCount());
    }

    // --- Helper Methods ---

    private void validateShipPlacement(Game game, int size, int x, int y, String playerName) {
        int N = game.getN();
        int minX, maxX;

        if (playerName.equals("PlayerA")) {
            minX = 0;
            maxX = N / 2;
        } else {
            minX = N / 2;
            maxX = N;
        }

        // 1. Validate boundary: Center (x, y) must be within the player's field
        if (x < minX || x >= maxX || y < 0 || y >= N) {
            throw new GameException(String.format("Ship center (%d, %d) for %s is outside its assigned field [%d,%d) x [0,%d).", x, y, playerName, minX, maxX, N));
        }

        // 2. Validate boundary: Entire ship must be within the board
        int radius = size / 2;
        int minShipX = x - radius;
        int maxShipX = x + radius + (size % 2 == 1 ? 0 : -1); // inclusive end
        int minShipY = y - radius;
        int maxShipY = y + radius + (size % 2 == 1 ? 0 : -1); // inclusive end

        if (minShipX < 0 || maxShipX >= N || minShipY < 0 || maxShipY >= N) {
            throw new GameException(String.format("Ship of size %d centered at (%d, %d) for %s extends outside the overall %dx%d board boundary.", size, x, y, playerName, N, N));
        }

        // 3. Validate overlap: Entire ship must be within the player's field
        if (minShipX < minX || maxShipX >= maxX) {
            throw new GameException(String.format("Ship of size %d centered at (%d, %d) for %s extends outside its assigned field [%d,%d) x [0,%d).", size, x, y, playerName, minX, maxX, N));
        }

        // 4. Validate overlap: Check for overlap with existing ships
        Set<Coordinate> newShipCoordinates = calculateShipCoordinates(game, size, x, y);
        for (Coordinate coord : newShipCoordinates) {
            if (game.getBattlefieldGrid().containsKey(coord)) {
                throw new GameException(String.format("Ship centered at (%d, %d) overlaps with an existing ship at %s.", x, y, coord));
            }
        }
    }

    /**
     * Calculates all Coordinates occupied by a square ship given its center and size.
     * The corners of a ship of size 's' centered at (x, y) are:
     * (x - r, y - r) and (x + r', y + r'), where r=s/2 and r'=r + (s%2==1 ? 0 : -1)
     */
    private Set<Coordinate> calculateShipCoordinates(Game game, int size, int x, int y) {
        Set<Coordinate> coordinates = new HashSet<>();
        int radius = size / 2;

        int minX = x - radius;
        int maxX = x + radius + (size % 2 == 1 ? 0 : -1); // inclusive end
        int minY = y - radius;
        int maxY = y + radius + (size % 2 == 1 ? 0 : -1); // inclusive end

        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                coordinates.add(new Coordinate(i, j));
            }
        }
        return coordinates;
    }

    /**
     * Updates the global battlefield map with the new ship's coordinates.
     */
    private void updateBattlefieldGrid(Game game, Set<Coordinate> coordinates, String shipIdentifier) {
        for (Coordinate coord : coordinates) {
            game.getBattlefieldGrid().put(coord, shipIdentifier);
        }
    }
}