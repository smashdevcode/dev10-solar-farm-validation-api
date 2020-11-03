package learn.solarfarm.domain;

import learn.solarfarm.data.DataAccessException;
import learn.solarfarm.data.SolarPanelRepository;
import learn.solarfarm.models.SolarPanel;
import learn.solarfarm.models.SolarPanelKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.Year;
import java.util.List;
import java.util.Set;

@Service
public class SolarPanelService {
    public final static int MAX_ROW_COLUMN = 250;

    private enum ValidationMode {
        CREATE, UPDATE;
    }

    @Autowired
    private Validator validator;

    private final SolarPanelRepository repository;

    public SolarPanelService(SolarPanelRepository repository) {
        this.repository = repository;
    }

    public static int getMaxInstallationYear() {
        return Year.now().getValue();
    }

    public List<SolarPanel> findAll() throws DataAccessException {
        return repository.findAll();
    }

    public List<SolarPanel> findBySection(String section) throws DataAccessException {
        return repository.findBySection(section);
    }

    public SolarPanel findByKey(SolarPanelKey key) throws DataAccessException {
        return repository.findByKey(key);
    }

    public Result<SolarPanel> create(SolarPanel solarPanel) throws DataAccessException {
        Result<SolarPanel> result = validate(solarPanel, ValidationMode.CREATE);

        if (result.isSuccess()) {
            solarPanel = repository.create(solarPanel);
            result.setPayload(solarPanel);
        }

        return result;
    }

    public Result<SolarPanel> update(SolarPanel solarPanel) throws DataAccessException {
        Result<SolarPanel> result = validate(solarPanel, ValidationMode.UPDATE);

        if (result.isSuccess()) {
            if (repository.update(solarPanel)) {
                result.setPayload(solarPanel);
            } else {
                result.addMessage("SolarPanel id %s was not found.",
                        ResultType.NOT_FOUND, solarPanel.getId());
            }
        }

        return result;
    }

    public Result<SolarPanel> deleteByKey(SolarPanelKey key) throws DataAccessException {
        Result<SolarPanel> result = new Result<>();
        if (!repository.deleteByKey(key)) {
            result.addMessage("SolarPanel %s was not found.", ResultType.NOT_FOUND, key);
        }
        return result;
    }

    private Result<SolarPanel> validate(SolarPanel solarPanel, ValidationMode validationMode)
            throws DataAccessException {
        Result<SolarPanel> result = new Result<>();

        if (solarPanel == null) {
            result.addMessage("SolarPanel cannot be null.", ResultType.INVALID);
        } else if (validationMode == ValidationMode.CREATE && solarPanel.getId() > 0) {
            result.addMessage("SolarPanel `id` should not be set.", ResultType.INVALID);
        } else if (validationMode == ValidationMode.UPDATE && solarPanel.getId() <= 0) {
            result.addMessage("SolarPanel `id` is required.", ResultType.INVALID);
        }

        if (result.isSuccess()) {
            Set<ConstraintViolation<SolarPanel>> violations = validator.validate(solarPanel);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<SolarPanel> violation : violations) {
                    result.addMessage(violation.getMessage(), ResultType.INVALID);
                }
            }
        }

        return result;
    }
}
