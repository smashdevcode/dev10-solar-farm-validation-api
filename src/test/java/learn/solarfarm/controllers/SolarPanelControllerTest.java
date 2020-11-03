package learn.solarfarm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.solarfarm.data.SolarPanelRepository;
import learn.solarfarm.models.Material;
import learn.solarfarm.models.SolarPanel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

// If you mock the service class you can tell Spring Boot to just start the web layer
// instead of the whole application context. You can go a step further and just
// tell Spring Boot to just instantiate a controller that you're testing.
//@WebMvcTest(SolarPanelController.class)
@SpringBootTest
@AutoConfigureMockMvc
class SolarPanelControllerTest {
    @MockBean
    SolarPanelRepository repository;

//    @MockBean
//    SolarPanelService service;

    @Autowired
    MockMvc mvc;

    @Test
    void shouldGetAll() throws Exception {
        List<SolarPanel> solarPanels = List.of(
                new SolarPanel(1, "Section One", 1, 1, 2020, Material.POLY_SI, true),
                new SolarPanel(2, "Section One", 1, 2, 2020, Material.POLY_SI, true),
                new SolarPanel(3, "Section Two", 10, 11, 2000, Material.A_SI, false)
        );

        ObjectMapper jsonMapper = new ObjectMapper();
        String expectedJson = jsonMapper.writeValueAsString(solarPanels);

        when(repository.findAll()).thenReturn(solarPanels);

        mvc.perform(get("/solarpanels"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldCreate() throws Exception {
        SolarPanel solarPanelIn = new SolarPanel(0, "Section One", 1, 3, 2000, Material.POLY_SI, false);
        SolarPanel expected = new SolarPanel(1, "Section One", 1, 3, 2000, Material.POLY_SI, false);

        when(repository.create(any())).thenReturn(expected);

        // Example of mocking the service.

//        SolarPanel solarPanelIn = new SolarPanel(0, "Section One", 1, 3, 2000, Material.POLY_SI, false);
//        SolarPanel expected = new SolarPanel(100, "Section One", 1, 3, 2000, Material.POLY_SI, false);
//
//        Result<SolarPanel> expectedResult = new Result<>();
//        expectedResult.setPayload(expected);
//
//        when(service.create(any())).thenReturn(expectedResult);

        ObjectMapper jsonMapper = new ObjectMapper();
        String jsonIn = jsonMapper.writeValueAsString(solarPanelIn);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/solarpanels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    void addShouldReturn400WhenAddingNull() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        String jsonIn = jsonMapper.writeValueAsString(null);

        var request = post("/solarpanels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldReturn415WhenMultipart() throws Exception {
        SolarPanel solarPanelIn = new SolarPanel(0, null, 1, 3, 2000, Material.POLY_SI, false);

        ObjectMapper jsonMapper = new ObjectMapper();
        String jsonIn = jsonMapper.writeValueAsString(solarPanelIn);

        var request = post("/solarpanels")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldNotCreateNullSection() throws Exception {
        SolarPanel solarPanelIn = new SolarPanel(0, null, 1, 3, 2000, Material.POLY_SI, false);

        ObjectMapper jsonMapper = new ObjectMapper();
        String jsonIn = jsonMapper.writeValueAsString(solarPanelIn);

        var request = post("/solarpanels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdate() throws Exception {
        SolarPanel solarPanelIn = new SolarPanel(1, "Section One", 1, 3, 2000, Material.POLY_SI, false);

        when(repository.update(any())).thenReturn(true);

        ObjectMapper jsonMapper = new ObjectMapper();
        String jsonIn = jsonMapper.writeValueAsString(solarPanelIn);

        var request = put("/solarpanels/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }
}