package learn.solarfarm.data;

import learn.solarfarm.models.Material;
import learn.solarfarm.models.SolarPanel;
import learn.solarfarm.models.SolarPanelKey;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@Profile("jdbc-template")
public class SolarPanelJdbcTemplateRepository implements SolarPanelRepository {
    private static final String SOLAR_PANEL_COLUMN_NAMES =
            "solar_panel_id, section, `row`, `column`, year_installed, material_id, is_tracking";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SolarPanel> mapper = (rs, i) -> {
        SolarPanel sp = new SolarPanel();
        sp.setId(rs.getInt("solar_panel_id"));
        sp.setSection(rs.getString("section"));
        sp.setRow(rs.getInt("row"));
        sp.setColumn(rs.getInt("column"));
        sp.setYearInstalled(rs.getInt("year_installed"));
        sp.setMaterial(Material.findByValue(rs.getInt("material_id")));
        sp.setTracking(rs.getBoolean("is_tracking"));
        return sp;
    };

    public SolarPanelJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SolarPanel> findAll() throws DataAccessException {
        final String sql = String.format("select %s from solar_panel;", SOLAR_PANEL_COLUMN_NAMES);
//        return jdbcTemplate.query(sql, new SolarPanelMapper());
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public List<SolarPanel> findBySection(String section) throws DataAccessException {
        final String sql = String.format("select %s from solar_panel " +
                "where section = ?;", SOLAR_PANEL_COLUMN_NAMES);
        return jdbcTemplate.query(sql, mapper, section);
    }

    @Override
    public SolarPanel findByKey(SolarPanelKey key) throws DataAccessException {
        final String sql = String.format("select %s from solar_panel " +
                "where section = ? and `row` = ? and `column` = ?;", SOLAR_PANEL_COLUMN_NAMES);
        try {
            return jdbcTemplate.queryForObject(sql, mapper,
                    key.getSection(), key.getRow(), key.getColumn());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public SolarPanel create(SolarPanel solarPanel) throws DataAccessException {
        final String sql = "insert into solar_panel " +
                "(section, `row`, `column`, year_installed, material_id, is_tracking) " +
                "values (?, ?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, solarPanel.getSection());
            statement.setInt(2, solarPanel.getRow());
            statement.setInt(3, solarPanel.getColumn());
            statement.setInt(4, solarPanel.getYearInstalled());
            statement.setInt(5, solarPanel.getMaterial().getValue());
            statement.setBoolean(6, solarPanel.isTracking());
            return statement;
        }, keyHolder);

        if (rowsAffected == 0) {
            return null;
        }

        solarPanel.setId(keyHolder.getKey().intValue());

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

        int rowsUpdated = jdbcTemplate.update(sql, solarPanel.getSection(), solarPanel.getRow(),
                solarPanel.getColumn(), solarPanel.getYearInstalled(),
                solarPanel.getMaterial().getValue(), solarPanel.isTracking(),
                solarPanel.getId());

        return rowsUpdated > 0;
    }

    @Override
    public boolean deleteByKey(SolarPanelKey key) throws DataAccessException {
        final String sql = "delete from solar_panel where section = ? and `row` = ? and `column` = ?;";
        return jdbcTemplate.update(sql, key.getSection(), key.getRow(), key.getColumn()) > 0;
    }
}
