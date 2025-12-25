package com.battleship.strategy;

import com.battleship.model.Coordinate;
import java.util.Set;

/**
 * Strategy interface for determining the next fire coordinate.
 */
public interface FireStrategy {
    /**
     * Finds a new, untargeted coordinate to fire upon in the opponent's field.
     * @param boardSize The size of the N x N battlefield.
     * @param opponentMinX The minimum X coordinate (inclusive) of the opponent's field.
     * @param opponentMaxX The maximum X coordinate (exclusive) of the opponent's field.
     * @param allFiredCoordinates A set of all coordinates fired upon so far in the game.
     * @return A valid, new Coordinate to fire at.
     */
    Coordinate getNextTarget(int boardSize, int opponentMinX, int opponentMaxX, Set<Coordinate> allFiredCoordinates);
}