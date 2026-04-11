package manager;
import java.util.logging.Logger;
import java.util.logging.Level;

import customExceptions.IncompatibleUserDataException;
import customExceptions.UserCreationException;
import model.*;
import model.Reader;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class UserManager {
    private final static String regex1="\\d{9}"; //test regex
    private final static Pattern emailPattern = Pattern.compile("[a-z]+@tu-sofia.bg");

    private static final String USERS_FILENAME = "users.txt";
    private static final Logger logger = Logger.getLogger(UserManager.class.getName());

    private static final UserService userService = new UserService();

    private final Object usersLock = new Object();

    private enum adminActions {
        CREATEUSER,
        LISTDOCUMENTS,
        CHANGEROLE,
        EDITCONFIGURATION
    }
    private enum authorActions {
        LISTDOCUMENTS,
        VIEWDOCUMENT,
        EDITDOCUMENT
    }
    private enum reviewerActions {
        LISTDOCUMENTS,
        VIEWDOCUMENT,
        APPROVEDOCUMENT,
        REJECTDOCUMENT
    }
    private enum readerActions {
        LISTDOCUMENTS,
        VIEWDOCUMENT
    }


    public UserManager() {
        initAdmins();
    }

    private void initAdmins() {
        if (new File(USERS_FILENAME).exists()) {
            return;
        }
        List<User> users = new ArrayList<>();
        User admin = new Administrator("admin", "12345");
        users.add(admin);
        userService.addUser(admin);
        saveUsers(users);
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


    private static User createUser(String userName, String password, UserType userType) throws UserCreationException {
        if (password.length() < 3) {
            throw new UserCreationException("Error: Password must be at least 5 characters");
        }
        switch (userType) {

            case ADMINISTRATOR: {
//                //how to not be repetitive with the ifs when static doesn't work outside of this subclass?
                return new Administrator(userName, password);
            }
            case AUTHOR: {
                return new Author(userName, password);
            }
            case REVIEWER: {
                return new Reviewer(userName, password);
            }
            case READER: {
                return new Reader(userName, password);
            }

            default:
                return null;
        }
    }

    public User registerUser(String userName, String password, UserType userType) throws UserCreationException {
        //add try and exception?
        User user = UserManager.createUser(userName, password, userType);
        synchronized (usersLock) {
            List<User> users = loadUsers();
            users.add(user);
            userService.addUser(user);
            saveUsers(users);
        }
        return user;
    }

    private List<User> checkIfObjectIsValid(Object obj){
        List<?> tempList = (List<?>) obj;
        if (obj instanceof List<?>) {

            for (Object item : tempList) {
                if (!(item instanceof User)) {
                    throw new ClassCastException("List contains non-User elements");
                }
            }

        }
        return (List<User>) tempList;
    }

    public List<User> loadUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USERS_FILENAME))) {
            Object obj = in.readObject();
            List<User> userList = checkIfObjectIsValid(obj);

            Map<Integer, User> userMap = new HashMap<>();
            for (User user : userList) {
                userMap.put(user.getUserId(), user);
            }

            userService.setUsers(userMap);

            //userService.setUsers((Map<String, User>) in.readObject());
            return userList;
            //return (List<User>) in.readObject();
        }
        catch (IOException e) {
            if (e instanceof InvalidClassException) {
                //see if it is possible to happen when saving?
                try {
                    throw new IncompatibleUserDataException("One or more of the User subclasses has likely changed." +
                            " Serializable versions are not supported." +
                            " Recreate the users file.", e);
                } catch (IncompatibleUserDataException ex) {
                    logger.log(Level.SEVERE, "Error occurred", e);
                }
            }
        }
        catch (ClassNotFoundException e) {
            // should never happen
            throw new IllegalStateException(e);
        }

        return null;
    }

    public void saveUsers(List<User> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILENAME))) {
            out.writeObject(users);
        }
        catch (IOException e) {
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
    public List<authorActions> getAuthorActions() {
        return Arrays.asList(authorActions.values());
    }
    public List<reviewerActions> getReviewerActions() {
        return Arrays.asList(reviewerActions.values());
    }
    public List<readerActions> getReaderActions() {
        return Arrays.asList(readerActions.values());
    }
}