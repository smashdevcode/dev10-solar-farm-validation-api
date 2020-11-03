package learn.solarfarm.validation;

import learn.solarfarm.data.DataAccessException;
import learn.solarfarm.data.SolarPanelJdbcTemplateRepository;
import learn.solarfarm.data.SolarPanelRepository;
import learn.solarfarm.domain.ResultType;
import learn.solarfarm.models.SolarPanel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DuplicateSolarPanelKeyValidator
        implements ConstraintValidator<NoDuplicateSolarPanelKey, SolarPanel> {
    @Autowired
    SolarPanelRepository repository;

    @Override
    public void initialize(NoDuplicateSolarPanelKey constraintAnnotation) {
        // Nothing to do.
    }

    @Override
    public boolean isValid(SolarPanel solarPanel, ConstraintValidatorContext constraintValidatorContext) {
        if (solarPanel == null || solarPanel.getKey() == null) {
            return true;
        }

        SolarPanel existingSolarPanel = null;
        try {
            existingSolarPanel = repository.findByKey(solarPanel.getKey());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return (existingSolarPanel == null || existingSolarPanel.getId() == solarPanel.getId());
    }
}
