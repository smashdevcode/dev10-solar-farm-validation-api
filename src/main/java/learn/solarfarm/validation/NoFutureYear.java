package learn.solarfarm.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {FutureYearValidator.class})
@Documented
public @interface NoFutureYear {
    String message() default "{year must be in the past}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
