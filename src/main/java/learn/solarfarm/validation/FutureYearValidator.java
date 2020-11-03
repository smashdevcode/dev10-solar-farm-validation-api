package learn.solarfarm.validation;

import learn.solarfarm.domain.SolarPanelService;
import learn.solarfarm.models.SolarPanel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FutureYearValidator implements ConstraintValidator<NoFutureYear, Integer> {
    @Override
    public void initialize(NoFutureYear constraintAnnotation) {
        // Nothing to do.
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return value <= SolarPanelService.getMaxInstallationYear();
    }
}
