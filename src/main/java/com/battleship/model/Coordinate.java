package com.battleship.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a single grid coordinate (X, Y).
 * Implements Comparable for potential use in sorted sets/maps, and
 * is used as a key in the map of fired coordinates.
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"x", "y"})
public class Coordinate implements Comparable<Coordinate> {
    private final int x;
    private final int y;

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int compareTo(Coordinate other) {
        if (this.x != other.x) {
            return Integer.compare(this.x, other.x);
        }
        return Integer.compare(this.y, other.y);
    }
}