package com.battleship.strategy;

import com.battleship.model.Coordinate;

import java.util.Set;

public class NearestCoordinateFireStrategy implements FireStrategy{
    @Override
    public Coordinate getNextTarget(int boardSize, int opponentMinX, int opponentMaxX, Set<Coordinate> allFiredCoordinates) {
        return null;
    }
}
