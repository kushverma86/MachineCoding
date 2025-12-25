package com.battleship.model;

import com.battleship.strategy.FireStrategy;
import com.battleship.strategy.RandomCoordinateFireStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a Player, holding their fleet of ships.
 */
@Getter
@RequiredArgsConstructor
public class Player {
    private final String name;

    // Map of Ship ID to Ship object
    private final Map<String, Ship> ships = new ConcurrentHashMap<>();

    public void addShip(Ship ship) {
        ships.put(ship.getId(), ship);
    }

    public int getRemainingShipsCount() {
        return (int) ships.values().stream()
                .filter(ship -> !ship.isDestroyed())
                .count();
    }

    public Map<String, Ship> getShips() {
        return Collections.unmodifiableMap(ships);
    }
}