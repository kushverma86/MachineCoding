package com.battleship.strategy;

import com.battleship.exception.GameException;
import com.battleship.model.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Implementation of the FireStrategy using random coordinates.
 */
public class RandomCoordinateFireStrategy implements FireStrategy {
    @Override
    public Coordinate getNextTarget(int boardSize, int opponentMinX, int opponentMaxX, Set<Coordinate> allFiredCoordinates) {
        List<Coordinate> availbleCoordinates = new ArrayList<>();

        for (int x = opponentMinX; x<opponentMaxX;x++){
            for (int y= 0; y<boardSize; y++){
                availbleCoordinates.add(new Coordinate(x,y));
            }
        }

        availbleCoordinates.removeAll(allFiredCoordinates);

        if (availbleCoordinates.isEmpty())
            throw new GameException("All Targets in the game has been fired");

        int random = new Random().nextInt(availbleCoordinates.size());

        return availbleCoordinates.get(random);
//
//        int maxAttempts = boardSize * boardSize; // safety break
//
//        // Loop until a coordinate not in allFiredCoordinates is found
//        for (int i = 0; i < maxAttempts; i++) {
//            // X coordinate ranges from opponentMinX to opponentMaxX - 1
//            int x = RANDOM.nextInt(opponentMaxX - opponentMinX) + opponentMinX;
//            // Y coordinate ranges from 0 to boardSize - 1
//            int y = RANDOM.nextInt(boardSize);
//
//            target = new Coordinate(x, y);
//
//            // Check if the coordinate has already been fired upon
//            if (!allFiredCoordinates.contains(target)) {
//                return target;
//            }
//        }
//
//        // Should theoretically not happen if the game logic is sound (i.e., when all cells are fired, the game ends)
//        throw new IllegalStateException("Could not find a unique target coordinate after many attempts. Game board is likely full.");
    }
}