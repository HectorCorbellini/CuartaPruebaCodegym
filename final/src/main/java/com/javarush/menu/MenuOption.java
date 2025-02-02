package com.javarush.menu;

/**
 * Enum representing available menu options in the application.
 * Each option has a display text and can be looked up by its numeric value.
 */
public enum MenuOption {
    PAGINATED_CITIES(1, "Show paginated cities"),
    POPULATION_RANGE(2, "Find cities by population range"),
    PERFORMANCE_COMPARISON(3, "Compare Redis vs Database performance"),
    CITY_CATEGORIES(4, "Show city categories"),
    EXIT(5, "Exit");

    private final int value;
    private final String displayText;

    MenuOption(int value, String displayText) {
        this.value = value;
        this.displayText = displayText;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayText() {
        return displayText;
    }

    /**
     * Find a menu option by its numeric value.
     * 
     * @param value The numeric value to look up
     * @return The corresponding MenuOption or null if not found
     */
    public static MenuOption fromValue(int value) {
        for (MenuOption option : values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

    /**
     * Check if a given value corresponds to a valid menu option.
     * 
     * @param value The value to check
     * @return true if the value is valid, false otherwise
     */
    public static boolean isValid(int value) {
        return fromValue(value) != null;
    }
}
