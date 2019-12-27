
package server;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import static java.lang.System.exit;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    public ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
    static ArrayList<User> users = new ArrayList<User>();

    public Server(int port) {

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File Dir = new File("Main");

        if (!Dir.exists()) {
            System.out.println("Creating main directory: " + Dir.getName());
            int check = 0;

            try {
                if(Dir.mkdir()) {
                    check = 1;
                }
            } catch (SecurityException se) {
               // handle it;
            }
            if (check==1) {
                System.out.println("Directory created");
            } else {
                System.out.println("Erorr !! problem in creating directory");
                exit(0);
            }
        } else {
            if(Dir.delete()) {
                Dir = new File("Main");
                if(!Dir.mkdir()){
                    System.out.println("Erorr in creating directory");
                    exit(0);
                }
            }
        }
    }    

    public void RunServer() {

        while (true) {

            try {
                Socket s;
                s = serverSocket.accept();
                ClientHandler CH = new ClientHandler(s);
                clientHandlers.add(CH);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}
    

