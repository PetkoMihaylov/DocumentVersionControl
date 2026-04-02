package manager;
import java.util.logging.Logger;
import java.util.logging.Level;
import customExceptions.CredentialsException;
import model.*;
import model.Reader;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class UserManager {
    private final static String regex1="\\d{9}"; //test regex
    private final static Pattern emailPattern = Pattern.compile("[a-z]+@tu-sofia.bg");

    private static final String USERS_FILENAME = "users.bin";
    private static final Logger logger = Logger.getLogger(UserManager.class.getName());


    private enum adminActions {
        CREATEUSER,
        CHANGEROLE,
        EDITCONFIGURATION
    }

    private final Object usersLock = new Object();

    public UserManager() {
        initAdmins();
    }

    public void initAdmins() {
        if (new File(USERS_FILENAME).exists())
            return;
        List<User> users = new ArrayList<>();
        users.add(new Administrator("admin", "12345"));
        saveUsers(users);
    }



    public static User createUser(String userName, String password, UserType userType) throws CredentialsException {
        switch (userType) {
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

    public User login(String userName, String password) {
        synchronized (usersLock) {
            for (User user : loadUsers())
            {
                if (Objects.equals(user.getUserName(), userName) && Objects.equals(user.getPassword(), password))
                    return user;
            }

            return null;
        }
    }

    public void registerUser(String userName, String password, UserType userType) throws CredentialsException {
        User user  = UserManager.createUser(userName, password, userType);
        synchronized (usersLock) {
            List<User> users = loadUsers();
            users.add(user);
            saveUsers(users);
        }
    }


    public List<User> loadUsers()
    {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USERS_FILENAME)))
        {
            return (List<User>) in.readObject();
        }
        catch (IOException e)
        {
            if (e instanceof InvalidClassException)
            {
                //see if it is possible to happen when saving?
                throw new RuntimeException("One or more of the User subclasses has likely changed." +
                        " Serializable versions are not supported." +
                        " Recreate the users file.", e);
            }

            logger.log(Level.SEVERE, "Error occurred", e);
        }
        catch (ClassNotFoundException e)
        {
            // Should never happen
            throw new RuntimeException(e);
        }

        return null;
    }

    public void saveUsers(List<User> users)
    {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILENAME)))
        {
            out.writeObject(users);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Error occurred", e);
        }
    }

    /*private static boolean checkPassword(password)
    {
        if (password.length() < 5)
            throw new CredentialsException("Error: Password must be at least 5 characters");
    }*/
    public List<adminActions> getAdminActions() {
        return Arrays.asList(adminActions.values());
    }
}