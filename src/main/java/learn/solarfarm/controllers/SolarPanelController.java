package learn.solarfarm.controllers;

import learn.solarfarm.data.DataAccessException;
import learn.solarfarm.domain.Result;
import learn.solarfarm.domain.ResultType;
import learn.solarfarm.domain.SolarPanelService;
import learn.solarfarm.models.SolarPanel;
import learn.solarfarm.models.SolarPanelKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = {"localhost:3000"})
@RequestMapping("/solarpanels")
public class SolarPanelController {
    private final SolarPanelService service;

    public SolarPanelController(SolarPanelService service) {
        this.service = service;
    }

    @GetMapping
    public List<SolarPanel> findAll() throws DataAccessException {
        return service.findAll();
    }

    @GetMapping("/{section}")
    public List<SolarPanel> findBySection(@PathVariable String section) throws DataAccessException {
        return service.findBySection(section);
    }

    @GetMapping("/{section}/{row}/{column}")
    public ResponseEntity<SolarPanel> findByKey(
            @PathVariable String section,
            @PathVariable int row,
            @PathVariable int column) throws DataAccessException {
        SolarPanelKey key = new SolarPanelKey(section, row, column);
        SolarPanel solarPanel = service.findByKey(key);
        if (solarPanel != null) {
            return new ResponseEntity<>(solarPanel, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Spring automatic validation...

//    @PostMapping
//    public ResponseEntity<?> create(
//            @RequestBody(required = false) @Valid SolarPanel solarPanel,
//            BindingResult result) throws DataAccessException {
//        if (result.hasErrors()) {
//            ValidationErrorResult validationErrorResult = new ValidationErrorResult();
//            result.getAllErrors().forEach((error) -> validationErrorResult.addMessage(error.getDefaultMessage()));
//            return new ResponseEntity<>(validationErrorResult, HttpStatus.BAD_REQUEST);
//        }
//
//        Result<SolarPanel> serviceResult = service.create(solarPanel);
//
//        return new ResponseEntity<>(serviceResult.getPayload(), HttpStatus.CREATED);
//    }

    // Validation in the service...

    @PostMapping
    public ResponseEntity<?> create(@RequestBody(required = false) SolarPanel solarPanel) throws DataAccessException {
        Result<SolarPanel> result = service.create(solarPanel);
        if (result.getType() == ResultType.INVALID) {
            ValidationErrorResult validationErrorResult = new ValidationErrorResult();
            result.getMessages().forEach(validationErrorResult::addMessage);
            return new ResponseEntity<>(validationErrorResult, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    // Validation in the service but no messages returned to the client...

//    @PostMapping
//    public ResponseEntity<SolarPanel> create(@RequestBody SolarPanel solarPanel) throws DataAccessException {
//        Result<SolarPanel> result = service.create(solarPanel);
//        if (result.getType() == ResultType.INVALID) {
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
//    }

    @PutMapping("/{solarPanelId}")
    public ResponseEntity<Void> update(
            @PathVariable int solarPanelId,
            @RequestBody SolarPanel solarPanel) throws DataAccessException {
        if (solarPanelId != solarPanel.getId()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Result<SolarPanel> result = service.update(solarPanel);
        if (result.getType() == ResultType.INVALID) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (result.getType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{section}/{row}/{column}")
    public ResponseEntity<Void> delete(
            @PathVariable String section,
            @PathVariable int row,
            @PathVariable int column) throws DataAccessException {
        SolarPanelKey key = new SolarPanelKey(section, row, column);
        Result<SolarPanel> result = service.deleteByKey(key);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
