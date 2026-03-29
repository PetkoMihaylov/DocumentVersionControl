package Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class UserMenu {

    private static final String USERS_FILENAME = "users.bin";

    private enum adminActions {
            CREATEUSER,
            CHANGEROLE,
            EDITCONFIGURATION
    }

    private final Object usersLock = new Object();

    public UserMenu()
    {
        initAdmins();
    }

    public void initAdmins()
    {
        if (new File(USERS_FILENAME).exists())
            return;
        List<User> users = new ArrayList<>();
        users.add(new Administrator("admin", "12345"));
        saveUsers(users);
    }


    public void startMenu(Scanner sc, PrintStream out) {
        showUserMenu(sc, out);
    }

    private void showUserMenu(Scanner sc, PrintStream out){
        while (true)
        {
            out.println("Login? Y/N");
            String login = sc.nextLine();

            if (!login.equalsIgnoreCase("Y"))
            {
                out.println("Goodbye.");
                return;
            }

            out.println("Enter username:");
            String userName = sc.nextLine();

            out.println("Enter password:");
            String password = sc.nextLine();

            User user = login(userName, password);

            if (user == null)
            {
                out.println("Error: Invalid login.");
                continue;
            }

            switch (user.getUserType())
            {
                case ADMINISTRATOR:
                {
                    adminMenu(sc, out, (Administrator) user);
                    break;
                }
                case AUTHOR:
                {
                    authorMenu(sc, out, (Author) user);
                    break;
                }
                case REVIEWER:
                {
                    reviewerMenu(sc, out, (Reviewer) user);
                    break;
                }
                case READER:
                {
                    readerMenu(sc, out, (Reader) user);
                    break;
                }
            }
        }
    }

    private void readerMenu(Scanner sc, PrintStream out, Reader user) {
    }

    private void reviewerMenu(Scanner sc, PrintStream out, Reviewer user) {
    }

    private void authorMenu(Scanner sc, PrintStream out, Author user) {
    }

    private User login(String userName, String password)
    {
        synchronized (usersLock)
        {
            for (User user : loadUsers())
            {
                if (Objects.equals(user.getUserName(), userName) && Objects.equals(user.getPassword(), password))
                    return user;
            }

            return null;
        }
    }


    private void adminMenu(Scanner sc, PrintStream out, Administrator admin)
    {
        out.println("Logged in as admin.");
        out.println("Choose what action you want to take: CREATEUSER | CHANGEROLE | EDITCONFIGURATION");


        if(sc.nextLine().equalsIgnoreCase("CREATEUSER")) {
            out.println("Enter user type to create: (ADMIN | STUDENT | TEACHER");
            try {
                UserType userType = UserType.valueOf(sc.nextLine().toUpperCase());

                out.println("Enter username:");
                String userName = sc.nextLine();

                out.println("Enter password:");
                String password = sc.nextLine();

                registerUser(userName, password, userType);

                out.println("Success.");
            } catch (IllegalArgumentException e) {
                out.println("Error: Invalid user type.");
            } catch (CredentialsException e) {
                out.println(e.getMessage());
            }
        }
    }

    private void registerUser(String userName, String password, UserType userType) throws CredentialsException
    {
        User user  = UserCreation.createUser(userName, password, userType);
        synchronized (usersLock)
        {
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

            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

}
