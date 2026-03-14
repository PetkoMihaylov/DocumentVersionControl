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


    private ServerSocket serverSocket;

    public Server()
    {
      //empty
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
                        //userMenu(sc, out);
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


        // For offline testing
        //userMenu(new Scanner(System.in), System.out);
    }





}
