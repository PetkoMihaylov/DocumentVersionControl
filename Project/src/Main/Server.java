package Main;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private static final String USERS_FILENAME = "users.bin";
    private ServerSocket serverSocket;

    public Server()
    {
      //empty
    }

    public void initAdmins()
    {
        if (new File(USERS_FILENAME).exists())
            return;
        List<User> users = new ArrayList<>();
        users.add(new Administrator("admin", "12345"));
        UserMenu userMenu = new UserMenu();
        userMenu.saveUsers(users);
    }

    public void start()
    {
        try
        {
            System.out.println("The server is listening.");
            try {
                serverSocket = new ServerSocket(8080);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true)
            {
                Socket client = serverSocket.accept();

                Thread clientThread = new Thread(() ->
                {
                    System.out.println("Accepted client.");
                    Scanner sc = null;
                    PrintStream out = null;

                    try
                    {
                        sc = new Scanner(client.getInputStream());
                        out = new PrintStream(client.getOutputStream());
                        UserMenu userMenu = new UserMenu();
                        userMenu.startMenu(sc, out);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (sc != null)
                            sc.close();
                        if (out != null)
                            out.close();
                    }
                });

                clientThread.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        // For offline testing old
        //userMenu(new Scanner(System.in), System.out);
    }





}
