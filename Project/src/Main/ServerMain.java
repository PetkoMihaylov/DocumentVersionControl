package main;

import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        //DocumentManager documentManager = new DocumentManager();
        //DocumentCreator documentCreator = new DocumentCreator();
        //documentCreator.createNewDocuments("for you", 2);
        Server server = new Server();
        server.start();
    }
}
