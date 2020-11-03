package learn.solarfarm.data;

import com.mysql.cj.jdbc.MysqlDataSource;
import learn.solarfarm.models.Material;
import learn.solarfarm.models.SolarPanel;
import learn.solarfarm.models.SolarPanelKey;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("jdbc")
public class SolarPanelJdbcRepository implements SolarPanelRepository {
    private static final String SOLAR_PANEL_COLUMN_NAMES =
            "solar_panel_id, section, `row`, `column`, year_installed, material_id, is_tracking";

    private final DataSource dataSource;

    public SolarPanelJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<SolarPanel> findAll() throws DataAccessException {
        ArrayList<SolarPanel> result = new ArrayList<>();

        final String sql = String.format("select %s from solar_panel;", SOLAR_PANEL_COLUMN_NAMES);

        try (Connection conn = dataSource.getConnection();
                 Statement statement = conn.createStatement();
                 ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                SolarPanel sp = getSolarPanelFromResultSet(rs);
                result.add(sp);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error finding all solar panels.", ex);
        }

        return result;
    }

    @Override
    public List<SolarPanel> findBySection(String section) throws DataAccessException {
        ArrayList<SolarPanel> result = new ArrayList<>();

        final String sql = String.format("select %s from solar_panel " +
                "where section = ?;", SOLAR_PANEL_COLUMN_NAMES);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, section);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    SolarPanel sp = getSolarPanelFromResultSet(rs);
                    result.add(sp);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error finding solar panels by section.", ex);
        }

        return result;
    }

    @Override
    public SolarPanel findByKey(SolarPanelKey key) throws DataAccessException {
        final String sql = String.format("select %s from solar_panel " +
                "where section = ? and `row` = ? and `column` = ?;", SOLAR_PANEL_COLUMN_NAMES);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, key.getSection());
            statement.setInt(2, key.getRow());
            statement.setInt(3, key.getColumn());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return getSolarPanelFromResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error finding a solar panel by its key.", ex);
        }

        return null;
    }

    @Override
    public SolarPanel create(SolarPanel solarPanel) throws DataAccessException {
        final String sql = "insert into solar_panel " +
                "(section, `row`, `column`, year_installed, material_id, is_tracking) " +
                "values (?, ?, ?, ?, ?, ?);";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setPreparedStatementValues(solarPanel, statement, false);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted == 0) {
                return null;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    solarPanel.setId(keys.getInt(1));
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error creating a solar panel.", ex);
        }

        return solarPanel;
    }

    @Override
    public boolean update(SolarPanel solarPanel) throws DataAccessException {
        final String sql = "update solar_panel set " +
                "section = ?, " +
                "`row` = ?, " +
                "`column` = ?, " +
                "year_installed = ?, " +
                "material_id = ?, " +
                "is_tracking = ? " +
                "where solar_panel_id = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            setPreparedStatementValues(solarPanel, statement, true);

            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error updating a solar panel.", ex);
        }
    }

    @Override
    public boolean deleteByKey(SolarPanelKey key) throws DataAccessException {
        final String sql = "delete from solar_panel where section = ? and `row` = ? and `column` = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, key.getSection());
            statement.setInt(2, key.getRow());
            statement.setInt(3, key.getColumn());

            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error deleting a solar panel.", ex);
        }
    }

    private SolarPanel getSolarPanelFromResultSet(ResultSet rs) throws SQLException {
        SolarPanel sp = new SolarPanel();
        sp.setId(rs.getInt("solar_panel_id"));
        sp.setSection(rs.getString("section"));
        sp.setRow(rs.getInt("row"));
        sp.setColumn(rs.getInt("column"));
        sp.setYearInstalled(rs.getInt("year_installed"));
        sp.setMaterial(Material.findByValue(rs.getInt("material_id")));
        sp.setTracking(rs.getBoolean("is_tracking"));
        return sp;
    }

    private void setPreparedStatementValues(
            SolarPanel solarPanel, PreparedStatement statement,
            boolean setId) throws SQLException {
        statement.setString(1, solarPanel.getSection());
        statement.setInt(2, solarPanel.getRow());
        statement.setInt(3, solarPanel.getColumn());
        statement.setInt(4, solarPanel.getYearInstalled());
        statement.setInt(5, solarPanel.getMaterial().getValue());
        statement.setBoolean(6, solarPanel.isTracking());

        if (setId) {
            statement.setInt(7, solarPanel.getId());
        }
    }
}
