package ar.edu.itba.paw.webapp.validators;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StringDiffValidator implements ConstraintValidator<StringDiff, Object> {

    private String string1;
    private String string2;

    @Override
    public void initialize(StringDiff constraintAnnotation) {
        this.string1 = constraintAnnotation.string1();
        this.string2 = constraintAnnotation.string2();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object string1Value = new BeanWrapperImpl(value).getPropertyValue(string1);
        Object string2Value = new BeanWrapperImpl(value).getPropertyValue(string2);

        if (string1Value == null && string2Value == null)
            return true;

        if (string1Value == null || string2Value == null)
            return true;

        if(string1Value.equals("") && string2Value.equals(""))
            return true;

        return !string1Value.equals(string2Value);
    }
}

