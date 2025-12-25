package com.battleship.strategy;

import com.battleship.exception.GameException;
// Assuming you use the custom GameException for graceful errors

public class FireStrategyFactory {

    // 1. Keep the Enum in the same class or move it out as a public contract
    public enum FireStrategyType {
        RANDOM,
        NEAREST
    }

    // 2. Factory method accepts the strategy name as a String
    public static FireStrategy getStrategy(String strategyName) {
        if (strategyName == null || strategyName.trim().isEmpty()) {
            throw new GameException("Fire strategy name cannot be null or empty.");
        }

        FireStrategyType type;
        try {
            // Convert input String to the Type-Safe Enum
            type = FireStrategyType.valueOf(strategyName.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle cases where the input string is not a valid enum name (e.g., "BOGUS")
            throw new GameException("Invalid fire strategy name: " + strategyName +
                    ". Must be one of: RANDOM, NEAREST.");
        }

        // 3. Use switch for instantiation based on the Type-Safe Enum
        switch (type) {
            case RANDOM:
                return new RandomCoordinateFireStrategy();

            case NEAREST:
                return new NearestCoordinateFireStrategy();

            default:
                // This case should theoretically not be reachable, but serves as a safeguard.
                throw new GameException("Unsupported strategy type: " + type);
        }
    }
}