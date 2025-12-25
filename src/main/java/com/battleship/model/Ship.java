package com.battleship.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Collections;
import java.util.Set;

/**
 * Represents a Ship with an ID, size, and the set of coordinates it occupies.
 */
@Getter
@RequiredArgsConstructor
public class Ship {
    private final String id;
    private final int size;
    // The set of coordinates occupied by this ship.
    private final Set<Coordinate> occupiedCoordinates;

    // Tracks if the ship is destroyed. In this simple game, a single hit destroys the whole ship.
    private boolean isDestroyed = false;

    public void destroy() {
        this.isDestroyed = true;
    }
}