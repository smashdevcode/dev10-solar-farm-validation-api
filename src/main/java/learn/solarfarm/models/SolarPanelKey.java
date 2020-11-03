package learn.solarfarm.models;

import java.util.Objects;

/**
 * Represents the natural key for a solar panel.
 * Having a class for the key makes it possible for a method
 * to return a key (which is the composite of three values) to the caller.
 *
 * NOTE: This class isn't a requirement of this project. The UI layer could be
 * updated to work without needing this data structure.
 */
public class SolarPanelKey {
    private String section;
    private int row;
    private int column;

    public SolarPanelKey(String section, int row, int column) {
        this.section = section;
        this.row = row;
        this.column = column;
    }

    public String getSection() {
        return section;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolarPanelKey that = (SolarPanelKey) o;
        return row == that.row &&
                column == that.column &&
                section.equalsIgnoreCase(that.section);
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, row, column);
    }

    @Override
    public String toString() {
        return String.format("%s-%s-%s", section, row, column);
    }
}
