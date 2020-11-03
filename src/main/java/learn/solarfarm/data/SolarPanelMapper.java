package learn.solarfarm.data;

import learn.solarfarm.models.Material;
import learn.solarfarm.models.SolarPanel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SolarPanelMapper implements RowMapper<SolarPanel> {
    @Override
    public SolarPanel mapRow(ResultSet rs, int i) throws SQLException {
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
}
