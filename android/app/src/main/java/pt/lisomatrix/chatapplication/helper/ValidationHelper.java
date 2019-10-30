package pt.lisomatrix.chatapplication.helper;

import java.util.regex.Pattern;

public class ValidationHelper {

    private static final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    public static boolean emailIsValid(String email) {
        Pattern pat = Pattern.compile(emailRegex);

        if (email == null) {
            return false;
        }

        return pat.matcher(email).matches();
    }
}
