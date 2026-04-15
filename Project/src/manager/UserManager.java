package manager;
import java.util.logging.Logger;
import java.util.logging.Level;

import exceptions.IncompatibleUserDataException;
import exceptions.UserCreationException;
import model.*;

import java.io.*;
import java.util.*;

public class UserManager {
    //private final static String regex1="\\d{9}"; //test regex
    //private final static Pattern emailPattern = Pattern.compile("[a-z]+@tu-sofia.bg");

    private static final String USERS_FILENAME = "users.bin";
    private static final Logger logger = Logger.getLogger(UserManager.class.getName());

    private static final UserService userService = new UserService();

    private final Object usersLock = new Object();



    public UserManager() {
        initAdmins();
    }

    private void initAdmins() {
        if (new File(USERS_FILENAME).exists()) {
            return;
        }
        List<User> users = new ArrayList<>();
        User admin = new User("admin", "12345", Role.ADMINISTRATOR);
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


    private static User createUser(String userName, String password, Role role) throws UserCreationException {
        if (password.length() < 3) {
            throw new UserCreationException("Error: Password must be at least 5 characters");
        }
        switch (role) {

            case ADMINISTRATOR: {
                return new User(userName, password, role);
            }
            case AUTHOR: {
                return new User(userName, password, role);
            }
            case REVIEWER: {
                return new User(userName, password, role);
            }
            case READER: {
                return new User(userName, password, role);
            }

            default:
                return null;
        }
    }

    public User registerUser(String userName, String password, Role role) throws UserCreationException {
        //add try and exception?
        User user = UserManager.createUser(userName, password, role);
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

    public List<AdminActions> getAdminActions() {
        return Arrays.asList(AdminActions.values());
    }
    public List<AuthorActions> getAuthorActions() {
        return Arrays.asList(AuthorActions.values());
    }
    public List<ReviewerActions> getReviewerActions() {
        return Arrays.asList(ReviewerActions.values());
    }
    public List<ReaderActions> getReaderActions() {
        return Arrays.asList(ReaderActions.values());
    }
}