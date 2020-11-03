package learn.solarfarm.models;

/**
 * Represents a solar panel material.
 */
public enum Material {
    POLY_SI(1,"Multicrystalline Silicon", "poly-Si"),
    MONO_SI(2, "Monocrystalline Silicon", "mono-Si"),
    A_SI(3, "Amorphous Silicon", "a-Si"),
    CD_TE(4, "Cadmium Telluride", "CdTe"),
    CIGS(5, "Copper Indium Gallium Selenide", "CIGS");

    private final int value;
    private final String name;
    private final String abbreviation;

    Material(int value, String name, String abbreviation) {
        this.value = value;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * The value.
     * @return An int representing the value for this enum.
     */
    public int getValue() {
        return value;
    }

    /**
     * The material name.
     * @return A String representing the material name.
     */
    public String getName() {
        return name;
    }

    /**
     * The abbreviation.
     * @return A String representing the material abbreviation.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Find a Material by its value.
     * @param value The value of the Material to find.
     * @return A Material enum value.
     */
    public static Material findByValue(int value) {
        for (Material material : Material.values()) {
            if (material.getValue() == value) {
                return material;
            }
        }
        String message = String.format("No Material with value: %s.", value);
        throw new RuntimeException(message);
    }

    /**
     * Find a Material by its name.
     * @param name The name of the Material to find.
     * @return A Material enum value.
     */
    public static Material findByName(String name) {
        for (Material material : Material.values()) {
            if (material.getName().equalsIgnoreCase(name)) {
                return material;
            }
        }
        String message = String.format("No Material with name: %s.", name);
        throw new RuntimeException(message);
    }
}
