package learn.solarfarm.data;

import learn.solarfarm.models.SolarPanel;
import learn.solarfarm.models.SolarPanelKey;

import java.util.List;

public interface SolarPanelRepository {
    List<SolarPanel> findAll() throws DataAccessException;

    List<SolarPanel> findBySection(String section) throws DataAccessException;

    SolarPanel findByKey(SolarPanelKey key) throws DataAccessException;

    SolarPanel create(SolarPanel solarPanel) throws DataAccessException;

    boolean update(SolarPanel solarPanel) throws DataAccessException;

    boolean deleteByKey(SolarPanelKey key) throws DataAccessException;
}
