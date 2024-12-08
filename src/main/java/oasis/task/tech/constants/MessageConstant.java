package oasis.task.tech.constants;

public interface MessageConstant {
    String EMPTY_DATA_FIELDS = "All required fields must contain data";
    String NO_EXISTING_APPLICATION = "No existing application";
    String EMAIL_EXISTS = "Email already exists";
    String PROGRAMME_DOES_NOT_EXIST = "Programme does not exist";
    String PHONE_NUMBER_EXISTS = "Phone Number already exists";
    String PASSWORD_MISMATCH = "Password mismatch";
    String INVALID_IMAGE_FORMAT = "Invalid Image format";
    String IMAGE_UPLOAD_ERROR = "There was an image upload error";
    String IMAGE_IS_EMPTY = "No image uploaded for this user";
    String SCHOOL_ALREADY_EXISTS = "School already exists";
    String SESSION_ALREADY_EXISTS = "Session already exists";
    String SEMESTER_ALREADY_EXISTS = "Semester already exists";
    String COURSE_CODE_EXISTS ="Course already exists";
    String SESSION_DURATION_INVALID = "End of session must be at lest four months greater than start of session";
    String DEPT_ALREADY_EXISTS = "Department already exists";
    String NO_ACTIVE_SESSION = "No active session, PLEASE START SESSION";
    String COURSE_ALLOCATED = "Course is already allocated";
    String EMAIL_OR_PHONE_NUMBER_EXISTS = "Email or phone number already exists";
    String EXISTING_ACTIVE_SEMESTER = "End existing semester before starting another semester";
    String SEMESTER_ENDED = "Semester already ended";
}
