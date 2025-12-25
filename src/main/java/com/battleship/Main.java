package com.battleship;

import com.battleship.exception.GameException;
import com.battleship.service.GameManager;

public class Main {
    public static void main(String[] args) {
        // Get the global game manager instance
        GameManager manager = GameManager.getInstance();
        String gameId = null;

        try {
            // Sample Execution:
            // >> initGame(6)
            gameId = manager.initGame(6);

            // >> addShip(“SH1”, size = 2, 1, 5, 4, 4)
            // Ship SH1 size 2, center at (1, 5) for A, (4, 4) for B.
            // Player A field: X=[0, 3). Player B field: X=[3, 6).
            // A-SH1: (0, 4), (1, 4), (0, 5), (1, 5)
            // B-SH1: (3, 3), (4, 3), (3, 4), (4, 4)
            manager.addShip(gameId, "SH1", 2, 1, 5, 4, 4);

            // Add another ship manually to match the diagram for the hit sample (A-SH2/B-SH2)
            // We assume B-SH2 is at (5, 3) and is size 2, as the hit in the sample is at (5, 3).
            // B-SH2 at (5, 3) coordinates: (4, 2), (5, 2), (4, 3), (5, 3)
            // We must add a ship for Player A as well to maintain equal fleet.
            // Let A-SH2 be at (2, 2) size 2. Coordinates: (1, 1), (2, 1), (1, 2), (2, 2)
//            manager.addShip(gameId, "SH2", 2, 2, 2, 5, 3);

            // >> viewBattleField()
            manager.viewBattleField(gameId);

            // The Sample Execution diagram shows A-SH1's coordinates:
            // (1, 4), (0, 4), (1, 5), (0, 5) - Correct.
            // The Sample Execution diagram shows B-SH1's coordinates:
            // (4, 3), (3, 3), (4, 4), (3, 4) - Correct.
            // The Sample Execution diagram shows B-SH2's coordinates (implied by the hit at (5, 3)):
            // (4, 2), (5, 2), (4, 3), (5, 3) - Correct. (Though only (5,3) is shown in the image grid, the ship is 2x2).

            // >> startGame()
            manager.startGame(gameId);

        } catch (GameException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (gameId != null) {
                manager.endGame(gameId);
            }
        }
    }
}