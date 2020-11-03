package learn.solarfarm.domain;

import learn.solarfarm.data.DataAccessException;
import learn.solarfarm.data.SolarPanelRepository;
import learn.solarfarm.models.Material;
import learn.solarfarm.models.SolarPanel;
import learn.solarfarm.models.SolarPanelKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SolarPanelServiceTest {
    @MockBean
    SolarPanelRepository repository;

    @Autowired
    SolarPanelService service;

    @Test
    void shouldFindAll() throws DataAccessException {
        when(repository.findAll()).thenReturn(List.of(
                new SolarPanel(1, "Section One", 1, 1, 2020, Material.POLY_SI, true),
                new SolarPanel(2, "Section One", 1, 2, 2020, Material.POLY_SI, true),
                new SolarPanel(3, "Section Two", 10, 11, 2000, Material.A_SI, false)
        ));

        List<SolarPanel> solarPanels = service.findAll();
        assertEquals(3, solarPanels.size());
    }

    @Test
    void shouldFindTwoSolarPanelsForSectionOne() throws DataAccessException {
        when(repository.findBySection(any())).thenReturn(List.of(
                new SolarPanel(1, "Section One", 1, 1, 2020, Material.POLY_SI, true),
                new SolarPanel(2, "Section One", 1, 2, 2020, Material.POLY_SI, true)
        ));

        List<SolarPanel> solarPanels = service.findBySection("Section One");
        assertEquals(2, solarPanels.size());
    }

    @Test
    void shouldFindSolarPanelInSectionTwoRow10Column11() throws DataAccessException {
        when(repository.findByKey(any())).thenReturn(
                new SolarPanel(3, "Section Two", 10, 11, 2000, Material.A_SI, false));

        SolarPanel solarPanel = service.findByKey(new SolarPanelKey("Section Two", 10, 11));
        assertNotNull(solarPanel);
    }

    @Test
    void shouldNotCreateNull() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = null;

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("cannot be null"));
    }

    @Test
    void shouldNotCreateNullSection() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection(null);
        solarPanel.setRow(1);
        solarPanel.setColumn(1);
        solarPanel.setMaterial(Material.POLY_SI);

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`section`"));
    }

    @Test
    void shouldNotCreateEmptySection() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("");
        solarPanel.setRow(1);
        solarPanel.setColumn(1);
        solarPanel.setMaterial(Material.POLY_SI);

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`section`"));
    }

    @Test
    void shouldNotCreateNullMaterial() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(1);
        solarPanel.setColumn(1);
        solarPanel.setYearInstalled(2000);

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`material`"));
    }

    @Test
    void shouldNotCreateNonPositiveRow() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(0);
        solarPanel.setColumn(1);
        solarPanel.setMaterial(Material.POLY_SI);

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`row`"));
    }

    @Test
    void shouldNotCreateGreaterThanMaxRow() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(SolarPanelService.MAX_ROW_COLUMN + 1);
        solarPanel.setColumn(1);
        solarPanel.setMaterial(Material.POLY_SI);

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`row`"));
    }

    @Test
    void shouldNotCreateNonPositiveColumn() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(1);
        solarPanel.setColumn(0);
        solarPanel.setMaterial(Material.POLY_SI);

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`column`"));
    }

    @Test
    void shouldNotCreateGreaterThanMaxColumn() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(1);
        solarPanel.setColumn(SolarPanelService.MAX_ROW_COLUMN + 1);
        solarPanel.setMaterial(Material.POLY_SI);

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`column`"));
    }

    @Test
    void shouldNotCreateYearInstalledInTheFuture() throws DataAccessException {
        // Arrange
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(1);
        solarPanel.setColumn(1);
        solarPanel.setYearInstalled(Year.now().plusYears(1).getValue());
        solarPanel.setMaterial(Material.POLY_SI);

        // Act
        Result<SolarPanel> result = service.create(solarPanel);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`yearInstalled`"));
    }

    @Test
    void shouldNotCreateNonUniqueSectionRowColumn() throws DataAccessException {
        when(repository.findByKey(any())).thenReturn(
                new SolarPanel(1, "Section One", 1, 1, 2020, Material.POLY_SI, true));

        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(1);
        solarPanel.setColumn(1);
        solarPanel.setYearInstalled(2000);
        solarPanel.setMaterial(Material.POLY_SI);

        Result<SolarPanel> result = service.create(solarPanel);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("must be unique"));
    }

    @Test
    void shouldNotCreatePositiveId() throws DataAccessException {
        SolarPanel solarPanel = new SolarPanel(1, "Section One", 1, 1, 2020,
                Material.POLY_SI, true);

        Result<SolarPanel> result = service.create(solarPanel);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`id`"));
    }

    @Test
    void shouldCreate() throws DataAccessException {
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(1);
        solarPanel.setColumn(3);
        solarPanel.setYearInstalled(2000);
        solarPanel.setMaterial(Material.POLY_SI);

        Result<SolarPanel> result = service.create(solarPanel);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotUpdateEmptySection() throws DataAccessException {
        when(repository.findByKey(any())).thenReturn(
                new SolarPanel(1, "Section One", 1, 1, 2020, Material.POLY_SI, true));

        SolarPanel solarPanel = service.findByKey(new SolarPanelKey("Section One", 1, 1));
        solarPanel.setSection("");

        Result<SolarPanel> result = service.update(solarPanel);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`section`"));
    }

    @Test
    void shouldNotUpdateNonPositiveId() throws DataAccessException {
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setSection("Section One");
        solarPanel.setRow(1);
        solarPanel.setColumn(3);
        solarPanel.setYearInstalled(2000);
        solarPanel.setMaterial(Material.POLY_SI);

        Result<SolarPanel> result = service.update(solarPanel);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("`id`"));
    }

    @Test
    void shouldNotUpdateNonExistentSolarPanel() throws DataAccessException {
        SolarPanel solarPanel = new SolarPanel();
        solarPanel.setId(1000);
        solarPanel.setSection("Section One");
        solarPanel.setRow(1);
        solarPanel.setColumn(3);
        solarPanel.setYearInstalled(2000);
        solarPanel.setMaterial(Material.POLY_SI);

        Result<SolarPanel> result = service.update(solarPanel);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("was not found"));
    }

    @Test
    void shouldUpdate() throws DataAccessException {
        when(repository.findByKey(any())).thenReturn(
                new SolarPanel(1, "Section One", 1, 1, 2020, Material.POLY_SI, true));
        when(repository.update(any())).thenReturn(true);

        SolarPanel solarPanel = service.findByKey(new SolarPanelKey("Section One", 1, 1));
        solarPanel.setMaterial(Material.A_SI);

        Result<SolarPanel> result = service.update(solarPanel);

        assertTrue(result.isSuccess());

        // re-retrieve the solar panel and check if the material was updated
        SolarPanel updatedSolarPanel = service.findByKey(new SolarPanelKey("Section One", 1, 1));
        assertEquals(Material.A_SI, updatedSolarPanel.getMaterial());
    }

    @Test
    void shouldNotDeleteNonExistentSolarPanel() throws DataAccessException {
        Result<SolarPanel> result = service.deleteByKey(new SolarPanelKey("Section Two", 100, 100));

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("was not found"));
    }

    @Test
    void shouldDelete() throws DataAccessException {
        when(repository.deleteByKey(any())).thenReturn(true);

        Result<SolarPanel> result = service.deleteByKey(new SolarPanelKey("Section One", 1, 1));

        assertTrue(result.isSuccess());
    }
}
