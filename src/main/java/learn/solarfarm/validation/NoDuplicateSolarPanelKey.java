package learn.solarfarm.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {DuplicateSolarPanelKeyValidator.class})
@Documented
public @interface NoDuplicateSolarPanelKey {
    String message() default "{SolarPanel `section`, `row`, and `column` must be unique}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
