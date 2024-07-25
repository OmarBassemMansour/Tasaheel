package Graduation.Project.Tasaheel;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static boolean isValidEmail(String email) {
            return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }

}
