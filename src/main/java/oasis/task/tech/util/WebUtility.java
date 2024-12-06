package oasis.task.tech.util;

import oasis.task.tech.dao.Accessor;
import oasis.task.tech.dao.Filter;
import oasis.task.tech.domains.actors.User;
import oasis.task.tech.exception.ValidationConstraintException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;

import java.util.stream.Collectors;

public class WebUtility {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    public static boolean confirmPasswordIsCorrect(String password, String hashedPassword) {
        return PASSWORD_ENCODER.matches(password, hashedPassword);
    }

    public static boolean confirmMatchingPassword(String password, String matchingPassword) {
        return password.equals(matchingPassword);
    }

    public static boolean isBcryptHashed(String password) {
        return StringUtils.isNotBlank(password) && password.length() == 60 && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    public static boolean userExist(final String username, final String email) {
        return Accessor.findOne(User.class, Filter.get().field("username", username).or().field("email", email)) != null;
    }

    public static void validate(Errors errors) throws ValidationConstraintException {
        if (errors.hasFieldErrors()) {
            throw new ValidationConstraintException(getErrorMessage(errors));
        }
    }

    public static String getErrorMessage(Errors errors) {
        return errors
                .getAllErrors()
                .stream()
                .map(x -> x.toString())
                .collect(Collectors.joining(", "));
    }

    public static <ID> void validateUpdateRequest(ID id) throws ValidationConstraintException {
        if (id == null) throw new ValidationConstraintException("ID can not be null for update operation");
    }
}
