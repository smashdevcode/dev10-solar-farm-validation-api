package learn.solarfarm.data;

import learn.solarfarm.models.Material;
import learn.solarfarm.models.SolarPanel;
import learn.solarfarm.models.SolarPanelKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnabledIf(value = "#{'${spring.profiles.active}' == 'jdbc'}", loadContext = true)
class SolarPanelJdbcRepositoryTest {
    @Autowired
    SolarPanelJdbcRepository repository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    static boolean hasSetUp = false;

    @BeforeEach
    void setup() {
        if (!hasSetUp) {
            hasSetUp = true;
            jdbcTemplate.update("call set_known_good_state();");
        }
    }

    @Test
    void shouldFindAll() throws DataAccessException {
        List<SolarPanel> all = repository.findAll();
        System.out.println(all);

        assertNotNull(all);
        assertTrue(all.size() >= 4);

        SolarPanel expected = new SolarPanel(1, "The Ridge", 1, 1,
                2020, Material.POLY_SI, true);

        // Ensure the solar_panel_id `1` is present (`1` is always left alone).
        // Then confirm solal_panel_id `2` exists, though its fields may have changed.
        assertTrue(all.contains(expected)
                && all.stream().anyMatch(i -> i.getId() == 2));
    }

    @Test
    void shouldFindBySection() throws DataAccessException {
        List<SolarPanel> all = repository.findBySection("The Ridge");

        assertNotNull(all);
        assertTrue(all.size() >= 2);

        SolarPanel expected = new SolarPanel(1, "The Ridge", 1, 1,
                2020, Material.POLY_SI, true);

        // Ensure the solar_panel_id `1` is present (`1` is always left alone).
        // Then confirm solal_panel_id `2` exists, though its fields may have changed.
        assertTrue(all.contains(expected)
                && all.stream().anyMatch(i -> i.getId() == 2));
    }

    @Test
    void shouldFindByKey() throws DataAccessException {
        SolarPanel expected = new SolarPanel(1, "The Ridge", 1, 1,
                2020, Material.POLY_SI, true);

        SolarPanel actual = repository.findByKey(new SolarPanelKey("The Ridge", 1, 1));

        assertEquals(expected, actual);
    }

    @Test
    void shouldNotFindByKeyMissing() throws DataAccessException {
        SolarPanel actual = repository.findByKey(new SolarPanelKey("Missing", 1, 1));
        assertNull(actual);
    }

    @Test
    void shouldCreate() throws DataAccessException {
        SolarPanel solarPanel = new SolarPanel(0, "The Ridge", 10, 10,
                2020, Material.POLY_SI, true);

        SolarPanel actual = repository.create(solarPanel);

        System.out.println(actual);
        assertNotNull(actual);
        assertTrue(actual.getId() > 0);
    }

    @Test
    void shouldUpdateExisting() throws DataAccessException {
        SolarPanel solarPanel = new SolarPanel(2, "New Ridge", 20, 21,
                2000, Material.A_SI, false);

        assertTrue(repository.update(solarPanel));
        assertEquals(solarPanel, repository.findByKey(solarPanel.getKey()));
    }

    @Test
    void shouldNotUpdateMissing() throws DataAccessException {
        SolarPanel solarPanel = new SolarPanel(-1, "New Ridge", 20, 21,
                2000, Material.A_SI, false);

        assertFalse(repository.update(solarPanel));
    }

    @Test
    void shouldDeleteExisting() throws DataAccessException {
        // TODO establish known good state
        assertTrue(repository.deleteByKey(new SolarPanelKey("Flats", 3, 7)));
    }

    @Test
    void shouldNotDeleteMissing() throws DataAccessException {
        assertFalse(repository.deleteByKey(new SolarPanelKey("Missing", 1, 1)));
    }
}
