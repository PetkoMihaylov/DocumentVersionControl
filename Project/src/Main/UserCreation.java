package Main;

import java.util.List;
import java.util.regex.Pattern;

public class UserCreation {
    private final static String regex1="\\d{9}"; //test regex
    private final static Pattern emailPattern = Pattern.compile("[a-z]+@tu-sofia.bg");

    public static User createUser(String userName, String password, UserType userType) throws CredentialsException
    {
        switch (userType)
        {
            case ADMINISTRATOR:
            {
                //how to not be repetitive with the ifs when static doesn't work outside of this subclass?
                if (password.length() < 5)
                    throw new CredentialsException("Error: Password must be at least 5 characters");
                return new Administrator(userName, password);
            }
            case AUTHOR:
            {
                if (password.length() < 5)
                    throw new CredentialsException("Error: Password must be at least 5 characters");
                return new Author(userName, password);
            }
            case REVIEWER:
            {
                if (password.length() < 5)
                    throw new CredentialsException("Error: Password must be at least 5 characters");
                return new Reviewer(userName, password);
            }
            case READER:
            {
                if (password.length() < 5)
                    throw new CredentialsException("Error: Password must be at least 5 characters");
                return new Reader(userName, password);
            }

            default:
                return null;
        }
    }
    /*private static boolean checkPassword(password)
    {
        if (password.length() < 5)
            throw new CredentialsException("Error: Password must be at least 5 characters");
    }*/

}